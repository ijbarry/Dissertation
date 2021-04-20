import java.awt.image.Kernel;
import java.io.*;
import java.lang.reflect.Array;
import java.util.*;

import static java.lang.Double.doubleToLongBits;
import static java.lang.Double.parseDouble;
import static java.lang.Math.*;


public class NaiveBayes_KDE extends NaiveBayes {

    private static double[] attackCount = new double[]{1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0};

    public static void main(String[] args) {
        try {
            Hashtable<String,Double>[][] discParam = DiscreteParameters(); //proto,service,state,ct_state_ttl
            KernelDensityEstimator[][] contParam = KernelDensityProb();  //dur, dpkts, sbytes,dttl, sjit, ackdat, smean, dmean, ct_dst_src_ltm, ct_flw_http_mthd
            //ct_srv_dst, trans_depth, attack_cat

            List<String>[] results = NBResults(discParam,contParam);
            NaiveBayes.WriteResults(results,"Gaussian");
        }
        catch (IOException e){
            System.out.println("IOException.");
            e.printStackTrace();
        }

    }
    protected static KernelDensityEstimator[][] KernelDensityProb() throws FileNotFoundException {
        Scanner scanner = null;

        int update = 0;

        KernelDensityEstimator[][] KernelDensityProb = new KernelDensityEstimator[13][10];
        for (int i = 0; i < 13; i++) {
            for (int j = 0; j < 10; j++) {
                KernelDensityProb[i][j] = new KernelDensityEstimator();
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
                KernelDensityProb[i][update].add(parseDouble(datum[Shared.getContFeatures()[i]]));
            }
        }

        for (int i = 0; i < 13; i++) {
            for (int j = 0; j < 10; j++) {
                KernelDensityProb[i][j].sort();
            }
        }
        scanner.close();
        return KernelDensityProb;
    }
}