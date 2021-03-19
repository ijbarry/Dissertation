import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Hashtable;
import java.util.List;
import java.util.Scanner;

public class Table {
    //continous features
    private static int dur=0;
    private static int dpkts=4;
    private static int sbytes=5;
    private static int dttl=5;
    private static int sjit=7;
    private static int ackdat=8;
    private static int smean=9;
    private static int dmean=10;
    private static int ct_dst_src_ltm=12;
    private static int ct_flw_http_mthd=13;
    private static int ct_srv_dst=14;
    private static int	trans_depth=15;
    private static int[] contFeatures= new int[]{dur,dpkts,sbytes,dttl,sjit,ackdat,smean,dmean,ct_dst_src_ltm,ct_flw_http_mthd,ct_srv_dst,trans_depth};

     public static void main2(String[] args) {

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
            File trainingSet = new File("KDE_Results1.csv");
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
