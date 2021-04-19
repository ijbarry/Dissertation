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
            File results = new File("Results/NB_Gaussian_Results.csv");
            scanner = new Scanner(results);
            scanner.nextLine();
            while (scanner.hasNextLine()) {
                String data = scanner.nextLine();
                String[] result = data.split(",");
                if (result[actual].equals(result[predicted])) {
                    fullycorrect = fullycorrect + 1.0;
                } else {
                    fullywrong = fullywrong + 1.0;
                }

                if(result[actual].equals("Normal")&&result[predicted].equals("Normal")){
                    trueNeg+=1.0;
                }
                else if(result[actual].equals("Normal")&&!result[predicted].equals("Normal")){
                    falsePos+=1.0;
                }
                else if(!result[actual].equals("Normal")&&!result[predicted].equals("Normal")){
                    truePos+=1.0;
                }
                else if(!result[actual].equals("Normal")&&result[predicted].equals("Normal")){
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

    public static void main1(String[] args) {
        int[] Count = new int[10];
        int[] CountP = new int[10];

        int actual = 0;
        int predicted = 1;
        try {
            Scanner scanner=null;
            File trainingSet = new File("reduced_training-set.csv");
            scanner = new Scanner(trainingSet);
            scanner.nextLine();
            while (scanner.hasNextLine()) {
                String data = scanner.nextLine();
                String[] results = data.split(",");
                Count[Shared.whichAttack(results[Shared.getAttack_cat()])]++;
                //CountP[Shared.whichAttack(results[predicted])]++;
            }
            System.out.println("Analysis Actual:"+Count[0]+", Analysis Predicted:"+CountP[0]);
            System.out.println("Backdoor:"+Count[1]+", Backdoor Predicted:"+CountP[1]);
            System.out.println("DoS:"+Count[2]+" ,DoS Predicted:"+CountP[2]);
            System.out.println("Exploits:"+Count[3]+", Exploits Predicted:"+CountP[3]);
            System.out.println("Fuzzers:"+Count[4]+", Fuzzers Predicted:"+CountP[4]);
            System.out.println("Generic:"+Count[5]+", Generic Predicted:"+CountP[5]);
            System.out.println("Reconnaissance:"+Count[6]+", Reconnaissance Predicted:"+CountP[6]);
            System.out.println("Shellcode:"+Count[7]+", Shellcode Predicted:"+CountP[7]);
            System.out.println("Worms:"+Count[8]+", Worms Predicted:"+CountP[8]);
            System.out.println("Normal:"+Count[9]+", Normal Predicted:"+CountP[9]);

        }
        catch (IOException e){
            System.out.println("IOException.");
            e.printStackTrace();
        }



    }

}
