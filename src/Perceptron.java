import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Scanner;

public class Perceptron {
    private static double grad =1;
    static File trainingSet = new File("one_hot_training-set.csv");
    static File testingSet = new File("one_hot_testing-set.csv");

    private static double[] averages;
    private static double[] stdDevs;

    public static void main(String[] args) {
        double fullycorrect =0.0;
        double fullywrong = 0.0;
        double partialcorrect =0.0;
        double partialwrong = 0.0;

        double truePos=0.0;
        double falsePos=0.0;
        double trueNeg=0.0;
        double falseNeg=0.0;
        try {
            List<String>[] results = test();
            PrintStream fileWriter = new PrintStream(new File("Perceptron_Results.csv"));
            for (int i = 0; i < results[1].size(); i++) {
                fileWriter.println(results[0].get(i)+","+results[1].get(i));
                if (results[0].get(i).equals(results[1].get(i))) {
                    fullycorrect = fullycorrect + 1.0;
                } else {
                    fullywrong = fullywrong + 1.0;
                }
                if(results[0].get(i).equals("Normal")&&results[1].get(i).equals("Normal")){
                    trueNeg+=1.0;
                }
                else if(results[0].get(i).equals("Normal")&&!results[1].get(i).equals("Normal")){
                    falseNeg+=1.0;
                }
                else if(!results[0].get(i).equals("Normal")&&!results[1].get(i).equals("Normal")){
                    truePos+=1.0;
                }
                else if(!results[0].get(i).equals("Normal")&&results[1].get(i).equals("Normal")){
                    falsePos+=1.0;
                }
            }
        }
        catch (IOException e){
            System.out.println("IOException.");
            e.printStackTrace();
        }
        System.out.println("correct type:"+fullycorrect);
        System.out.println("wrong type:"+fullywrong);
        double fullaccuracy= fullycorrect/(fullycorrect+fullywrong);
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
                    scaledInput[i] = ((Double.parseDouble(datum[i])-averages[i])/stdDevs[i]);
                }
                if(datum[inputs]=="0"){
                    results[0].add("Normal");
                }
                else {
                    results[0].add("Attack");
                }

                if(!attackPredicted(weights,scaledInput)){
                    results[1].add("Normal");
                }
                else {
                    results[1].add("Attack");
                }
            }
        } catch (FileNotFoundException e) {
            System.out.println("FileNotFoundException.");
            e.printStackTrace();
        }
        return results;
    }

    private static double[] weights() throws FileNotFoundException{
        scaling();
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
                double in = Double.parseDouble(datum[i]);
                if(stdDevs[i]==0.0){
                    scaledInput[i]=(in-averages[i]);
                }
                else {
                    scaledInput[i] = (in - averages[i]) / stdDevs[i];
                }

            }
            boolean attack = datum[inputs].equals("1");
            double error = error(attackPredicted(weights,scaledInput),attack);
            for (int i = 0; i <inputs ; i++) {
                weights[i] = weights[i]+ grad*error*scaledInput[i];
            }

        }
        return weights;
    }

    private static void scaling() throws FileNotFoundException{ //update to reflect one hot
        Scanner scanner=null;
        double Count=0;
        scanner = new Scanner(trainingSet);
        String d =scanner.nextLine();
        String[] dt = d.split(",");
        int inputs = dt.length-1;
        List<Double>[] values=new List[inputs];
        for (int i=0;i<inputs;i++){
            values[i]= new ArrayList<Double>();
        }
        averages = new double[inputs];
        stdDevs = new double[inputs];
        while (scanner.hasNextLine()) {
                Count++;
                String data = scanner.nextLine();
                String[] datum = data.split(",");
                for (int i=0;i<inputs;i++) {
                    Double add = Double.parseDouble(datum[i]);
                    if(add.isNaN()){
                        add=0.0;
                    }
                    averages[i] += add;
                    values[i].add(add);
                }
        }
            for (int i=0;i<inputs;i++) {
                averages[i] = averages[i]/Count;
                for (int j=0; j< values[i].size();j++) {
                    stdDevs[i] += (values[i].get(j)-averages[i])*(values[i].get(j)-averages[i]);
                }

                stdDevs[i] = Math.sqrt(stdDevs[i]/Count);
                if(stdDevs[i]==0.0){
                    stdDevs[i]=1.0;
                }

            }
        System.out.println();

        scanner.close();
    }

    private static boolean attackPredicted(double[] weights, double[] features){
        double sum = 0.0;
        for (int i = 0; i < features.length; i++) {
            sum += weights[i]*features[i];
        }
        if(sum>=0.0){
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