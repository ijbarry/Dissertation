import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Scanner;

public class Perceptron {
    private static double grad =0.001;
    static File trainingSet = new File("Dataset/scaled_one_hot_training-set.csv");
    static File testingSet = new File("Dataset/scaled_one_hot_testing-set.csv");

    public static void main(String[] args) {
        double correct =0.0;
        double wrong = 0.0;

        double truePos=0.0;
        double falsePos=0.0;
        double trueNeg=0.0;
        double falseNeg=0.0;

        int actual =0;
        int predicted = 1;
        try {
            List<String>[] results = test();
            PrintStream fileWriter = new PrintStream(new File("Results/Perceptron_Results.csv"));
            for (int i = 0; i < results[1].size(); i++) {
                fileWriter.println(results[0].get(i)+","+results[1].get(i));
                if (results[actual].get(i).equals(results[1].get(i))) {
                    correct = correct + 1.0;
                } else {
                    wrong = wrong + 1.0;
                }
                if(results[actual].get(i).equals("Normal")&&results[predicted].get(i).equals("Normal")){
                    trueNeg+=1.0;
                }
                else if(results[actual].get(i).equals("Attack")&&results[predicted].get(i).equals("Normal")){
                    falseNeg+=1.0;
                }
                else if(results[actual].get(i).equals("Attack")&&results[predicted].get(i).equals("Attack")){
                    truePos+=1.0;
                }
                else if(results[actual].get(i).equals("Normal")&&results[predicted].get(i).equals("Attack")){
                    falsePos+=1.0;
                }
            }
        }
        catch (IOException e){
            System.out.println("IOException.");
            e.printStackTrace();
        }
        System.out.println("correct type:"+correct);
        System.out.println("wrong type:"+wrong);
        double fullaccuracy= correct/(correct+wrong);
        System.out.println("typing accuracy:"+fullaccuracy);

        System.out.println("False Positive:"+falsePos);
        System.out.println("True Positive:"+truePos);
        System.out.println("False Negative:"+falseNeg);
        System.out.println("True Negative:"+trueNeg);


    }

    private static List<String>[] test(){
        List<String>[] results = new ArrayList[]{new ArrayList<String>(),new ArrayList<String>()};
        Scanner scanner=null;
        try {
            double[] weights = weights();
            scanner = new Scanner(testingSet);
            scanner.nextLine();
            while (scanner.hasNextLine()) {
                String data = scanner.nextLine();
                String[] datum = data.split(",");
                int inputs = datum.length-1;
                double[] scaledInput = new double[inputs];
                for (int i = 0; i <inputs ; i++) {
                    scaledInput[i] = Double.parseDouble(datum[i]);
                }
                if(datum[inputs].equals("0")){
                    results[0].add("Normal");
                }
                else if(datum[inputs].equals("1")){
                    results[0].add("Attack");
                }

                if(attackPredicted(weights,scaledInput)){
                    results[1].add("Attack");
                }
                else {
                    results[1].add("Normal");
                }
            }
        } catch (FileNotFoundException e) {
            System.out.println("FileNotFoundException.");
            e.printStackTrace();
        }
        return results;
    }

    private static double[] weights() throws FileNotFoundException{
        Scanner scanner=null;
        scanner = new Scanner(trainingSet);
        scanner.nextLine();
        String d = scanner.nextLine();
        String[] dt = d.split(",");
        int inputs = dt.length-1;
        double[] weights = new double[inputs];
        for (int i=0;i<inputs;i++) {
            weights[i]=1.0;
        }
        while (scanner.hasNextLine()) {
            String data = scanner.nextLine();
            String[] datum = data.split(",");
            double[] scaledInput = new double[inputs];
            for (int i = 0; i <inputs ; i++) {
                scaledInput[i] = Double.parseDouble(datum[i]);
            }
            boolean attack = datum[inputs].equals("1");
            double error = error(attackPredicted(weights,scaledInput),attack);
            for (int i = 0; i <inputs ; i++) {
                weights[i] = weights[i]+ grad*error*scaledInput[i];
            }

        }
        return weights;
    }

    private static boolean attackPredicted(double[] weights, double[] features){
        double sum = 0.0;
        for (int i = 0; i < features.length; i++) {
            sum += weights[i]*features[i];
        }
        if(sum<=0.0){
            return false;
        }
        else{
            return true;
        }
    }

    private static double error(boolean predicted,boolean actual){
        if(predicted && actual||!predicted && !actual){ // correct
            return 0.0;
        }
        else if(predicted && !actual){ //predicted attack but not really
            return -1.0;
        }
        else{ // attack not predicted
            return 1.0;
        }
    }
}
