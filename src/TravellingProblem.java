import java.sql.SQLOutput;
import java.util.Random;

public class TravellingProblem {
    private int[] startHypothesis;
    private int[][] distances;

    public TravellingProblem(int numberOfNodes, long seed) {
        startHypothesis = new int[numberOfNodes];
        distances = new int[numberOfNodes][numberOfNodes];

        Random random = new Random(seed);
        for (int a = 0; a < numberOfNodes; a++) {
            startHypothesis[a] = a; //results in an array from 0 to (numberOfNodes -1)
            for (int b = a; b < numberOfNodes; b++) {

                if (a == b) {
                    distances[a][b] = 0;
                } else {
                    int randomValue = random.nextInt(100);
                    distances[a][b] = randomValue;
                    distances[b][a] = randomValue; //results in a symmetrical matrix
                }
            }
        }
    }

    public void hillClimb(int minIterations){
        System.out.println("HILL CLIMB" );
        int[] hypothesis = startHypothesis.clone();
        int lastFitness = getFitness(hypothesis, distances);
        System.out.println("initial fitness >> " + -lastFitness);
        for (int iteration = 0; iteration < minIterations; iteration++) {
            int[] hypothesisBackup = hypothesis.clone();
            mutateRandom(hypothesis);
            int currentFitness = getFitness(hypothesis, distances);
            if (currentFitness > lastFitness) {
                lastFitness = currentFitness;
            } else {
                hypothesis = hypothesisBackup;
            }
        }
        System.out.println("best fitness    >> "+ -lastFitness);
    }
    public void simulatedAnnealing(double startTemperature, double epsilon){
        System.out.println("SIMULATED ANNEALING" );
        int[] hypothesis = startHypothesis.clone();
        int lastFitness = getFitness(hypothesis, distances);
        System.out.println("initial fitness >> " + -lastFitness);
        double temperature = startTemperature;
        do {

            int[] hypothesisBackup = hypothesis.clone();
            mutateRandom(hypothesis);
            int currentFitness = getFitness(hypothesis, distances);
            if (currentFitness > lastFitness) {
                lastFitness = currentFitness;
            } else if(temperature > 0 && Math.random() < Math.exp((currentFitness-lastFitness)/temperature)){
                lastFitness = currentFitness;

            } else {
                hypothesis = hypothesisBackup;
            }
            temperature = temperature - epsilon;
        }while(temperature > epsilon);
        System.out.println("best fitness    >> "+ -lastFitness);
    }


    private void mutateRandom(int[] hypothesis) {
        //get random indices
        Random random = new Random();
        int a = random.nextInt(hypothesis.length);
        int b = a;
        while (a == b)
            b = random.nextInt(hypothesis.length);
        //swap
        int bValue = hypothesis[b];
        hypothesis[b] = hypothesis[a];
        hypothesis[a] = bValue;
    }

    private int getFitness(int[] hypothesis, int[][] distances) {
        int sum = 0;
        int firstIndex;
        for (int b = 1; b < hypothesis.length; b++) {

            int a = b - 1;
            sum += distances[hypothesis[a]][hypothesis[b]];
        }
        sum += distances[hypothesis[hypothesis.length - 1]][hypothesis[0]];
        return -sum;
    }


    public static void main(String[] args) {
        TravellingProblem problem = new TravellingProblem(100, 1); //generate the problem
        problem.hillClimb(100000);
        problem.simulatedAnnealing(1000, .0001);
    }


}
