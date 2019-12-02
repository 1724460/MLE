package mle.kNearest;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Comparator;

public class KNearest {

    public static void main(String[] args) {
        new KNearest(13);
    }

    public int k;

    class LabeledPoint {
        int label;
        double x, y;

        LabeledPoint(int label, double x, double y) {
            this.label = label;
            this.x = x;
            this.y = y;
        }

        @Override
        public String toString() {
            return String.format("Prototype [%d](%f, %f)", label, x, y);
        }

        double distance(LabeledPoint other) {
            return Math.sqrt(Math.pow(x - other.x, 2) + Math.pow(y - other.y, 2));
        }
    }


    private ArrayList<LabeledPoint> prototypes;

    public KNearest(int k) {
        this.k = k;
        initPrototypes();
    }

    public ArrayList<LabeledPoint> getPrototypes(){return prototypes;}

    public int getKNearest(double x, double y) {
        LabeledPoint inputVector = new LabeledPoint(0, x, y);
        //sort by distance
        prototypes.sort(new Comparator<LabeledPoint>() {
            @Override
            public int compare(LabeledPoint p1, LabeledPoint p2) {
                double distance1 = inputVector.distance(p1);
                double distance2 = inputVector.distance(p2);
                if (distance1 < distance2)
                    return -1;
                else
                    return 1;

            }
        });
        //get k nearest prototypes
        int labelPositiveCounter = 0;
        int labelNegativeCounter = 0;
        for (int i = 0; i < k; i++) {
            if (prototypes.get(i).label == 1)
                labelPositiveCounter++;
            else if (prototypes.get(i).label == -1)
                labelNegativeCounter++;
        }
        //get winning label
        return (labelNegativeCounter > labelPositiveCounter) ? -1 : 1;
    }

    public void initPrototypes() {
        //get spiral data
        try {
            prototypes = new ArrayList<>();
            File file = new File("res//spiraldaten.txt");
            BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                String[] values = line.split(";");
                double x = Double.parseDouble(values[0]);
                double y = Double.parseDouble(values[1]);
                int label = Integer.parseInt(values[2]);
                LabeledPoint p = new LabeledPoint(label, x, y);
                prototypes.add(p);
            }
        } catch (Exception e) {
            System.err.println(e);
        }
    }

}
