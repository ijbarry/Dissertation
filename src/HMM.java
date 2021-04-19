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

public class HMM {

    public static void main(String[] args) {

        double fullycorrect =0.0;
        double fullywrong = 0.0;
        double partialcorrect =0.0;
        double partialwrong = 0.0;

        double truePos=0.0;
        double falsePos=0.0;
        double trueNeg=0.0;
        double falseNeg=0.0;
        try {
            Hashtable<String,Double>[][] discParam = NaiveBayes.DiscreteParameters(); //proto,service,state,ct_state_ttl
            KernelDensityEstimator[][] contParam = NaiveBayes.KernelDensityProb();  //dur, dpkts, sbytes,dttl, sjit, ackdat, smean, dmean, ct_dst_src_ltm, ct_flw_http_mthd
            //ct_srv_dst, trans_depth, attack_cat

            List<String>[] results = HMMResults(discParam,contParam);
            PrintStream fileWriter = new PrintStream(new File("Results/HMM_KDE_Results.csv"));
            for (int i = 0; i < results[1].size(); i++) {
                fileWriter.println(results[0].get(i)+","+results[1].get(i));
                if (results[0].get(i).equals(results[1].get(i))) {
                    fullycorrect = fullycorrect + 1.0;
                } else {
                    fullywrong = fullywrong + 1.0;
                }
                if (results[0].get(i).equals("Normal") && results[1].get(i).equals("Normal") || !results[0].get(i).equals("Normal") && !results[1].get(i).equals("Normal")) {
                    partialcorrect = partialcorrect + 1.0;
                } else {
                    partialwrong = partialwrong + 1.0;
                }
                if(results[0].get(i).equals("Normal")&&results[1].get(i).equals("Normal")){
                    trueNeg+=1.0;
                }
                else if(results[0].get(i).equals("Normal")&&!results[1].get(i).equals("Normal")){
                    falseNeg+=1.0;
                }
                else if(!results[0].get(i).equals("Normal")&&!results[1].get(i).equals("Normal")){
                    truePos+=1.0;
                }
                else if(!results[0].get(i).equals("Normal")&&results[1].get(i).equals("Normal")){
                    falsePos+=1.0;
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

        System.out.println("overall type correct:"+fullycorrect);
        System.out.println("overall type incorrect:"+fullywrong);
        double overallaccuracy= partialcorrect/(partialcorrect+partialwrong);
        System.out.println("typing accuracy:"+fullaccuracy);

        System.out.println("False Positive:"+falsePos);
        System.out.println("True Positive:"+truePos);
        System.out.println("False Negative:"+falseNeg);
        System.out.println("True Negative:"+trueNeg);

   /*     double[][] tp = TransitionProbs();

        for(double[] row : tp) {
            for (double i : row) {
                System.out.print(i);
                System.out.print("\t");
            }
            System.out.println();
        }
        */

    }

    private static double[] attackCount = new double[]{1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0};
    //First-order HMM
    private static double[][] TransitionProbs() throws FileNotFoundException {
        Scanner scanner=null;
        double AttackCount[]=new double[11];
        double[][] TransitionCount=new double[11][11];
        for (int i = 0; i <11 ; i++) { //add-one-smoothed
            for (int j = 0; j <11 ; j++) {
                TransitionCount[i][j]=1.0;
            }
            AttackCount[i]=1.0;
        }
        File trainingSet = new File("Dataset/reduced_training-set.csv");
        scanner = new Scanner(trainingSet);
        scanner.nextLine();
        int current = -1;
        int prev =10; //start case
        while (scanner.hasNextLine()) {
            String data = scanner.nextLine();
            String[] datum = data.split(",");
            current = Shared.whichAttack(datum[Shared.getAttack_cat()]);
            AttackCount[current] +=1.0;
            TransitionCount[prev][current] += 1.0;
            prev = current;
        }
        TransitionCount[current][10] += 1.0; //end case

        for (int i = 0; i < 11 ; i++) {
            for (int j = 0; j < 10 ; j++) {
                AttackCount[i] += TransitionCount[i][j];
            }
            for (int j = 0; j < 11 ; j++) {
                TransitionCount[i][j] = TransitionCount[i][j]/AttackCount[i];
            }
        }
        scanner.close();
        return TransitionCount;
    }

    private static List<String>[] HMMResults(Hashtable<String,Double>[][] discParam,KernelDensityEstimator[][] contParam) throws FileNotFoundException {
        double[][] transitionProbs = TransitionProbs();
        Scanner scanner=null;
        double attackSum =0.0;

        int prev =10; //start case

        for (double count:attackCount) {
            attackSum += count;
        }
        List<String>[] results = new ArrayList[]{new ArrayList<String>(),new ArrayList<String>()};
        String[] attacks = new String[]{"Analysis","Backdoor","DoS","Exploits","Fuzzers","Generic","Reconnaissance","Shellcode","Worms","Normal"};
            scanner = new Scanner(Shared.getTestingSet());
            scanner.nextLine();
            while (scanner.hasNextLine()) {
                double[] ProbAttacks = new double[10];
                for (int i = 0; i < 10; i++) {
                    ProbAttacks[i]=log(attackCount[i]/attackSum);
                    ProbAttacks[i]=0;

                }
                String data = scanner.nextLine();
                String[] datum = data.split(",");
                results[0].add(datum[Shared.getAttack_cat()]);

                for (int attack = 0; attack < 10; attack++) {
                    //discrete features
                    for (int feature = 0; feature < 4; feature++) {
                        if (discParam[1][attack].containsKey(datum[Shared.getDiscFeatures()[feature]])) {
                            ProbAttacks[attack] += log(discParam[1][attack].get(datum[Shared.getDiscFeatures()[feature]]));
                        }
                        else {
                            ProbAttacks[attack] += log(discParam[1][attack].get("smallest"));
                        }
                    }

                    //continous features
                    for (int feature = 0; feature < 12; feature++) {
                        double x = parseDouble(datum[Shared.getContFeatures()[feature]]);
                        ProbAttacks[attack] += log(contParam[feature][attack].getProb(x));
                    }

                }
                int greatest = 1;
                double probOfGreatest = ProbAttacks[greatest]*transitionProbs[prev][greatest];
                for (int i = 1; i < 10; i++) {
                    double probOfattack = ProbAttacks[i]*transitionProbs[prev][i];
                    if(probOfattack>probOfGreatest){
                        greatest=i;
                        probOfGreatest = ProbAttacks[greatest]*transitionProbs[prev][greatest];
                    }
                }
                results[1].add(attacks[greatest]);
                prev = greatest;
            }
            scanner.close();

        return results;
    }
}
