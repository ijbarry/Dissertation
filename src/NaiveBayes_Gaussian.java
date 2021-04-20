import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Scanner;

import static java.lang.Double.parseDouble;
import static java.lang.Math.log;
import static java.lang.Math.sqrt;

public class NaiveBayes_Gaussian extends NaiveBayes{
    public static void main(String[] args) {
        try {
            Hashtable<String,Double>[][] discParam = NaiveBayes.DiscreteParameters(); //proto,service,state,ct_state_ttl
            Gaussian[][] contParam = GaussianProb();  //dur, dpkts, sbytes,dttl, sjit, ackdat, smean, dmean, ct_dst_src_ltm, ct_flw_http_mthd
            //ct_srv_dst, trans_depth, attack_cat

            List<String>[] results = NaiveBayes.NBResults(discParam,contParam);
            NaiveBayes.WriteResults(results,"Gaussian");
        }
        catch (IOException e){
            System.out.println("IOException.");
            e.printStackTrace();
        }

    }
    protected static Gaussian[][] GaussianProb() throws FileNotFoundException {
        int update= 0;
        Scanner scanner = null;
        List<Double>[][] splitCont = new List[13][10];
        for (int i=0;i<13;i++){
            for (int j=0;j<10;j++){
                splitCont[i][j]= new ArrayList<>();
            }
        }
        Pair[][] contParam = new Pair[13][10];
        for (int i=0;i<13;i++){
            for (int j=0;j<10;j++){
                contParam[i][j]=new Pair();
            }
        }

        File trainingSet = new File("Dataset/reduced_training-set.csv");
        scanner = new Scanner(trainingSet);
        scanner.nextLine();
        while (scanner.hasNextLine()) {
            String data = scanner.nextLine();
            String[] datum = data.split(",");
            update = Shared.whichAttack(datum[Shared.getAttack_cat()]);
            for (int i = 0; i < 12; i++) {
                splitCont[i][update].add(parseDouble(datum[Shared.getContFeatures()[i]]));
                contParam[i][update].addLeft(parseDouble(datum[Shared.getContFeatures()[i]]));
                contParam[i][update].addRight(1.0);
            }
        }
        for (int feature = 0; feature < 12; feature++) {
            for (int attack = 0; attack < 9; attack++) {
                Double count=contParam[feature][attack].getRight();
                Double sum =contParam[feature][attack].getLeft();
                if(contParam[feature][attack].getRight()!=0.0){
                    contParam[feature][attack].setLeft(sum/count);
                }
                Double sqSum=0.0;
                for (int i=0;i<splitCont[feature][attack].size();i++) {
                    Double diff = splitCont[feature][attack].get(i)- contParam[feature][attack].getLeft();
                    sqSum += diff * diff;
                }
                contParam[feature][attack].setRight(sqrt(sqSum/count));
            }
        }
        scanner.close();
        Gaussian[][] GaussianProb = new Gaussian[13][10];
        for (int i=0;i<13;i++){
            for (int j=0;j<10;j++){
                GaussianProb[i][j]=new Gaussian(contParam[i][j].getLeft(),contParam[i][j].getRight());
            }
        }
        return GaussianProb;
    }
}
