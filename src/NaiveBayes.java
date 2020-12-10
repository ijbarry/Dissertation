import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;


public class NaiveBayes {
    public static void main(String[] args) {
        int dur = 0;
        int proto = 1;
        int service = 2;
        int state = 3;
        int dpkts = 4;
        int sbytes = 5;
        int dttl = 6;
        int sjit = 7;
        int ackdat = 8;
        int smean = 9;
        int dmean = 10;
        int ct_state_ttl = 11;
        int ct_dst_src_ltm = 12;
        int ct_flw_http_mthd=13;
        int ct_srv_dst=14;
        int	trans_depth=15;
        int attack_cat=16;


        float analysis=0;
        float backdoor=1;
        float dos=2;
        float exploits=3;
        float fuzzers=4;
        float generic=5;
        float reconnaissance=6;
        float shellcode=7;
        float worms=8;


        float[][] count= new float[15][7];

        try {
            File trainingSet = new File("reduced_training-set.csv");
            Scanner scanner = new Scanner(trainingSet);
            while (scanner.hasNextLine()) {
                String data = scanner.nextLine();
                String[] datum = data.split(",");
                switch (datum[attack_cat]){
                    case "Analysis":
                        break;
                    case "Backdoor":
                        break;
                }
            }
            scanner.close();
        } catch (FileNotFoundException e) {
            System.out.println("FileNotFoundException.");
            e.printStackTrace();
        }


    }
}
