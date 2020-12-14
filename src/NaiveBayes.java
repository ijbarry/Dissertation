import java.io.File;
import java.io.FileNotFoundException;
import java.util.Hashtable;
import java.util.Map;
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


        int analysis=0;
        int backdoor=1;
        int dos=2;
        int exploits=3;
        int fuzzers=4;
        int generic=5;
        int reconnaissance=6;
        int shellcode=7;
        int worms=8;

        int protoUpdate= 0;

        Hashtable<String,Double>[] protoCount = new Hashtable[9];
        try {
            File trainingSet = new File("reduced_training-set.csv");
            Scanner scanner = new Scanner(trainingSet);
            while (scanner.hasNextLine()) {
                String data = scanner.nextLine();
                String[] datum = data.split(",");
                switch (datum[attack_cat]){
                    case "Analysis":
                        protoUpdate= analysis;
                        break;
                    case "Backdoor":
                        protoUpdate= backdoor;
                        break;
                    case "DoS":
                        protoUpdate= dos;
                        break;
                    case "Exploits":
                        protoUpdate= exploits;
                        break;
                    case "Fuzzers":
                        protoUpdate= fuzzers;
                        break;
                    case "Generic":
                        protoUpdate= generic;
                        break;
                    case "Reconnaissance":
                        protoUpdate= reconnaissance;
                        break;
                    case "Shellcode":
                        protoUpdate= shellcode;
                        break;
                }
                if(protoCount[protoUpdate].containsKey(datum[proto])){
                    protoCount[protoUpdate].put(datum[proto],protoCount[protoUpdate].get(datum[proto])+1.0);
                }
                else{
                    protoCount[protoUpdate].put(datum[proto],0.0);
                }
            }
            scanner.close();
        } catch (FileNotFoundException e) {
            System.out.println("FileNotFoundException.");
            e.printStackTrace();
        }


    }
}
