import java.io.*;
import java.util.*;

public class OneHotEncoder {

    public static void main(String[] args) {
        try {
            Set<String>[] discVals = types();
            File trainingSet = new File("reduced_training-set.csv");
            File reducedTrainingSet = new File("one_hot_training-set.csv");
            FileWriter fileWriter = new FileWriter("one_hot_training-set.csv");
            BufferedWriter bw = new BufferedWriter(fileWriter);
            Scanner scanner = new Scanner(trainingSet);
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
        } catch (FileNotFoundException e) {
            System.out.println("FileNotFoundException.");
            e.printStackTrace();
        } catch (IOException e2) {
            System.out.println("IOException.");
            e2.printStackTrace();
        }
    }

    //use trainingset for both
    public static Set<String>[] types() throws FileNotFoundException {
        Set<String>[] discVals = new Set[Shared.getDiscFeatures().length];
            File trainingSet = new File("reduced_training-set.csv");
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

}

