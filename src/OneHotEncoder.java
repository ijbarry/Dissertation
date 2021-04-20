import java.io.*;
import java.util.*;

public class OneHotEncoder {
    private static double[] averages;
    private static double[] stdDevs;
    public static void main(String[] args) {
        try {
            boolean train = false;
            oneHotEncoder(train);
            scaling(train);
            write(train);
        } catch (FileNotFoundException e) {
            System.out.println("FileNotFoundException.");
            e.printStackTrace();
        } catch (IOException e2) {
            System.out.println("IOException.");
            e2.printStackTrace();
        }
    }

    private static void oneHotEncoder(boolean train)throws IOException{
        Set<String>[] discVals = types();
        File read = null;
        File write = null;
        FileWriter fileWriter;
        if(train){
            read =new File("Dataset/reduced_training-set.csv");
            write = new File("Dataset/one_hot_training-set.csv");
        }else {
            read = new File("Dataset/reduced_testing-set.csv");
            write = new File("Dataset/one_hot_testing-set.csv");
        }
        fileWriter = new FileWriter(write);
        BufferedWriter bw = new BufferedWriter(fileWriter);
        Scanner scanner = new Scanner(read);
        String d = scanner.nextLine();
        String[] dt = d.split(",");
        for (int i:Shared.getContFeatures()) {
            bw.write(dt[i]+",");
        }
        for (int i=0;i<Shared.getDiscFeatures().length;i++) {
            for (String val: discVals[i]) {
                bw.write(val+",");
            }
        }
        bw.write("Attack");
        bw.newLine();
        while (scanner.hasNextLine()) {
            String data = scanner.nextLine();
            String[] datum = data.split(",");
            for (int i:Shared.getContFeatures()) {
                bw.write(datum[i]+",");
            }
            for (int i=0;i<Shared.getDiscFeatures().length;i++) {
                for (String val: discVals[i]) {
                    if(val.equals(datum[Shared.getDiscFeatures()[i]])){
                        bw.write("1,");
                    }
                    else {
                        bw.write("0,");
                    }
                }
            }
            if(datum[Shared.getAttack_cat()].equals("Normal")){
                bw.write("0");
            }
            else {
                bw.write("1");
            }
            bw.newLine();
        }
        scanner.close();
        bw.flush();
        bw.close();
    }
    //use trainingset for both
    public static Set<String>[] types() throws FileNotFoundException {
        Set<String>[] discVals = new Set[Shared.getDiscFeatures().length];
        File trainingSet = new File("Dataset/reduced_training-set.csv");
        Scanner scanner = new Scanner(trainingSet);
        for (int i = 0;i<Shared.getDiscFeatures().length;i++) {
            discVals[i]=new TreeSet<>();
        }
        while (scanner.hasNextLine()) {
            String data = scanner.nextLine();
            String[] datum = data.split(",");
            for (int i = 0;i<Shared.getDiscFeatures().length;i++) {
                discVals[i].add(datum[Shared.getDiscFeatures()[i]]);
            }
        }
        scanner.close();
        return discVals;

    }

    private static void scaling(boolean train) throws IOException {
        File read = null;
        File write = null;
        FileWriter fileWriter;
        double Count=0;
        if(train){
            read =new File("Dataset/one_hot_training-set.csv");
            write = new File("Dataset/scaled_one_hot_training-set.csv");
        }else {
            read = new File("Dataset/one_hot_testing-set.csv");
            write = new File("Dataset/scaled_one_hot_testing-set.csv");
        }
        Scanner scanner = new Scanner(read);
        fileWriter = new FileWriter(write);
        BufferedWriter bw = new BufferedWriter(fileWriter);
        String d = scanner.nextLine();
        String[] dt = d.split(",");
        int contFeat = Shared.getContFeatures().length;
        List<Double>[] values=new List[contFeat];
        for (int i=0;i<contFeat;i++){
            values[i]= new ArrayList<Double>();
        }
        averages = new double[contFeat];
        stdDevs = new double[contFeat];
        while (scanner.hasNextLine()) {
            Count++;
            String data = scanner.nextLine();
            String[] datum = data.split(",");
            for (int i=0;i<contFeat;i++) {
                Double add = Double.parseDouble(datum[i]);
                if(add.isNaN()){
                    add=0.0;
                }
                averages[i] += add;
                values[i].add(add);
            }
        }
        for (int i=0;i<contFeat;i++) {
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

    private static void write(boolean train) throws IOException {
        Set<String>[] discVals = types();
        File read = null;
        File write = null;
        FileWriter fileWriter;
        if(train){
            read =new File("Dataset/one_hot_training-set.csv");
            write = new File("Dataset/scaled_one_hot_training-set.csv");
        }else {
            read = new File("Dataset/one_hot_testing-set.csv");
            write = new File("Dataset/scaled_one_hot_testing-set.csv");
        }
        fileWriter = new FileWriter(write);
        BufferedWriter bw = new BufferedWriter(fileWriter);
        Scanner scanner = new Scanner(read);
        String d = scanner.nextLine();
        String[] dt = d.split(",");
        for (String i:dt) {
            bw.write(i+",");
        }
        bw.newLine();
        while (scanner.hasNextLine()) {
            String data = scanner.nextLine();
            String[] datum = data.split(",");
            for (int i=0;i<Shared.getContFeatures().length;i++) {
                bw.write((Double.parseDouble(datum[i])-averages[i])/stdDevs[i]+",");
            }
            for (int i=Shared.getContFeatures().length;i<datum.length-1;i++) {
                bw.write(Double.parseDouble(datum[i])+",");
            }
            bw.write(datum[datum.length-1]);

            bw.newLine();
        }
        scanner.close();
        bw.flush();
        bw.close();
    }
}