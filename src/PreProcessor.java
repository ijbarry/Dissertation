import java.io.*;
import java.util.Scanner;


public class PreProcessor {

    public static void main(String[] args) {
        int dur=1;
        int proto=2;
        int service=3;
        int state=4;
        int dpkts=6;
        int sbytes=7;
        int dttl=11;
        int sjit=18;
        int ackdat=26;
        int smean=27;
        int dmean=28;
        int ct_state_ttl=32;
        int ct_dst_src_ltm=36;
        int ct_flw_http_mthd=39;
        int ct_srv_dst=41;
        int	trans_depth=29;
        int attack_cat=43;
        try {
            File trainingSet = new File("Dataset/UNSW_NB15_testing-set.csv");
            File reducedTrainingSet = new File("Dataset/reduced_testing-set.csv");
            FileWriter fileWriter = new FileWriter("Dataset/reduced_testing-set.csv");
            BufferedWriter bw = new BufferedWriter(fileWriter);
            Scanner scanner = new Scanner(trainingSet);
            while (scanner.hasNextLine()) {
                String data = scanner.nextLine();
                String[] datum = data.split(",");
                bw.write(datum[dur]+","+datum[proto]+","+datum[service]+","+
                        datum[state]+","+datum[dpkts]+","+datum[sbytes]+","+datum[dttl]+","+
                        datum[sjit]+","+datum[ackdat]+","+datum[smean]+","+
                        datum[dmean]+","+datum[ct_state_ttl]+","+datum[ct_dst_src_ltm]+","+datum[ct_flw_http_mthd]+","+datum[ct_srv_dst]+","+datum[trans_depth]+","+datum[attack_cat]);
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
    }
