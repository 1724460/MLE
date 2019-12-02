import java.util.ArrayList;
import java.util.Random;

public class GeneticAlgorithm {
    public static final int BIT_STRING_LENGTH = 128;
    public static final int INITIAL_POPULATION_SIZE = 2000;
    public static final double CROSSOVER_RATE = 0.8;
    public static final int MUTATION_RATE = 200;
    public static final double MUTATION_PROBABILITY = 0.1;
    public static final double FITNESS_THRESHOLD = 0.95 *BIT_STRING_LENGTH;


    private BitString optimum;
    private ArrayList<BitString> population;
    private int[] fitnessValues;
    private int maxFitnessValue;
    private double sumOfFitnessValues;
    private BitString maxFitnessIndividual;

    public GeneticAlgorithm(){

        //generate optimum
        optimum = new BitString();
        optimum.randomize();
        System.out.println("optimum created");

        //generate initial population
        population = new ArrayList<>();
        for (int i = 0; i < INITIAL_POPULATION_SIZE; i++){
            BitString individual = new BitString();
            individual.randomize();
            population.add(individual);
        }
       updateFitness();
       System.out.println("initial population of "+ INITIAL_POPULATION_SIZE + " individuals generated");

        System.out.println("starting algorithm...");
        int generations = 0;
        while(maxFitnessValue < FITNESS_THRESHOLD){

            //selection
            ArrayList<BitString> nextGeneration = new ArrayList<>();

            int amount = (int)Math.ceil((1- CROSSOVER_RATE) * population.size());
            for(int i = 0; i < amount; i++){
                nextGeneration.add(selectIndividual());
            }

            //crossover
            amount = (int)Math.ceil(CROSSOVER_RATE * population.size()/2);
            for(int i = 0; i < amount; i++){
                BitString a = selectIndividual();
                BitString b = selectIndividual();
                nextGeneration.add(a.crossover(b));
                nextGeneration.add(b.crossover(a));
            }

            //mutation
            ArrayList<BitString> mutatableIndivduals = (ArrayList<BitString>)nextGeneration.clone();
            for(int i = 0; i < MUTATION_RATE && i < nextGeneration.size(); i++){
                double randomNumber = Math.random();
                double sum = 0;
                int index = new Random().nextInt(mutatableIndivduals.size());
                do {
                    index = (index +1)% mutatableIndivduals.size();
                    sum += MUTATION_PROBABILITY;
                }while (sum < randomNumber);
                BitString selectedIndividual = mutatableIndivduals.get(index);
                selectedIndividual.flipRandomBit();
            }

            population = nextGeneration;
            updateFitness();
            System.out.println("generation " + generations + " MaxFitness = " + maxFitnessValue);
            generations++;
        }
        System.out.println("'X' := 1            '_' := 0");
        System.out.println("OPTIMUM " + optimum);
        System.out.println("FITTEST " +maxFitnessIndividual);



    }

    public static void main(String[] args){
       GeneticAlgorithm gA = new GeneticAlgorithm();
    }

    private void updateFitness(){
        fitnessValues = new int[population.size()];
        maxFitnessValue = Integer.MIN_VALUE;
        sumOfFitnessValues = 0;
        for (int i = 0; i < population.size(); i++){
            fitnessValues[i] = population.get(i).fitness(optimum);
            sumOfFitnessValues += fitnessValues[i];
            if(fitnessValues[i] > maxFitnessValue){
                maxFitnessValue = fitnessValues[i];
                maxFitnessIndividual = population.get(i);
            }
        }
    }
    private double probability(int index){
        return fitnessValues[index]/ sumOfFitnessValues;
    }

    private BitString selectIndividual(){
        double randomNumber = Math.random();
        double sum = 0;
        int index = new Random().nextInt(population.size());
        do {
            index = (index +1)%population.size();
            sum += probability(index);
        }while (sum < randomNumber);
        BitString selectedIndividual = population.get(index);
        return selectedIndividual;
    }



    class BitString{
        boolean[] bits;
        BitString(){
            bits = new boolean[BIT_STRING_LENGTH];
        }

         void randomize(){
            Random r = new Random();
            for(int i = 0; i < bits.length; i++){
                bits[i] = r.nextBoolean();
            }
         }

         public String toString(){
            String s = "";
             for(int i = 0; i < bits.length; i++){
                 s += bits[i]?"X":"_";
             }
             return "[" + s + "]";
         }



         public int fitness(BitString optimum){
            return bits.length - hummingDistance(optimum);
         }

         int hummingDistance(BitString other){
            int counter = 0;
             for(int i = 0; i < bits.length; i++){
                 if(bits[i] != other.bits[i])
                     counter++;
             }
             return counter;
         }

         void flipRandomBit(){
            Random r = new Random();
            int index = r.nextInt(BIT_STRING_LENGTH);
            bits[index] = !bits[index];
         }

         BitString crossover(BitString other){
            BitString descendant = new BitString();
            Random r = new Random();
            int crossoverPoint = r.nextInt(BIT_STRING_LENGTH-1)+1;
            for(int i = 0; i < BIT_STRING_LENGTH; i++){
                descendant.bits[i] = ((i < crossoverPoint)? this: other).bits[i];
            }
            return descendant;
         }

    }


}
