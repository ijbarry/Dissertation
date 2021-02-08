import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Hashtable;
import java.util.List;
import java.util.Scanner;

public class Table {

    public static void main(String[] args) {

        int actual =0;
        int predicted = 1;
        double fullycorrect =0.0;
        double fullywrong = 0.0;
        double partialcorrect =0.0;
        double partialwrong = 0.0;

        double truePos=0.0;
        double falsePos=0.0;
        double trueNeg=0.0;
        double falseNeg=0.0;
        try {
            Scanner scanner=null;
            File trainingSet = new File("HMM_Results.csv");
            scanner = new Scanner(trainingSet);
            scanner.nextLine();
            while (scanner.hasNextLine()) {
                String data = scanner.nextLine();
                String[] results = data.split(",");
                if (results[actual].equals(results[predicted])) {
                    fullycorrect = fullycorrect + 1.0;
                } else {
                    fullywrong = fullywrong + 1.0;
                }
                /*if (results[0].get(i).equals("Normal") && results[1].get(i).equals("Normal") || !results[0].get(i).equals("Normal") && !results[1].get(i).equals("Normal")) {
                    partialcorrect = partialcorrect + 1.0;
                } else {
                    partialwrong = partialwrong + 1.0;
                }*/
                if(results[actual].equals("Normal")&&results[predicted].equals("Normal")){
                    trueNeg+=1.0;
                }
                else if(results[actual].equals("Normal")&&!results[predicted].equals("Normal")){
                    falsePos+=1.0;
                }
                else if(!results[actual].equals("Normal")&&!results[predicted].equals("Normal")){
                    truePos+=1.0;
                }
                else if(!results[actual].equals("Normal")&&results[predicted].equals("Normal")){
                    falseNeg+=1.0;
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
}
