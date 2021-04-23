import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Random;
import java.util.Scanner;
import java.util.stream.IntStream;
import java.util.stream.Stream;


public class PreProcessor {

    public static void main1(String[] args) {
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


    public static void main(String[] args) {
        try {
            File ValidationSet = new File("Dataset/reduced_validation-set.csv");
            FileWriter fileWriter = new FileWriter("Dataset/reduced_validation-set.csv");
            BufferedWriter bw = new BufferedWriter(fileWriter);
            Random ran = new Random();
            for (int i = 0; i < 8233; i++) {
                try (Stream<String> lines = Files.lines(Paths.get("Dataset/UNSW_NB15_training-set.csv"))) {
                    String line = lines.skip(ran.nextInt(82332)).findFirst().get();
                    String[] datum = line.split(",");
                    bw.write(datum[1]+","+datum[2]+","+datum[3]+","+
                            datum[4]+","+datum[5]+","+datum[6]+","+datum[7]+","+
                            datum[8]+","+datum[9]+","+datum[10]+","+
                            datum[11]+","+datum[12]+","+datum[13]+","+datum[14]+","+datum[15]+","+datum[16]+","+datum[17]);
                    bw.newLine();
                }
            }
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
