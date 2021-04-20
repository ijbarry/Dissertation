import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Scanner;

import static java.lang.Double.parseDouble;
import static java.lang.Math.log;

public class HMM_KDE implements HMM{

    public static void main(String[] args) {
        try {
            Hashtable<String,Double>[][] discParam = NaiveBayes.DiscreteParameters(); //proto,service,state,ct_state_ttl
            KernelDensityEstimator[][] contParam = NaiveBayes.KernelDensityProb();  //dur, dpkts, sbytes,dttl, sjit, ackdat, smean, dmean, ct_dst_src_ltm, ct_flw_http_mthd
            //ct_srv_dst, trans_depth, attack_cat

            List<String>[] results = HMM.HMMResults(discParam,contParam);
            HMM.WriteResults(results,"KDE");
        }
        catch (IOException e){
            System.out.println("IOException.");
            e.printStackTrace();
        }

    }}