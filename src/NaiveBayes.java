import java.awt.image.Kernel;
import java.io.*;
import java.lang.reflect.Array;
import java.util.*;

import static java.lang.Double.doubleToLongBits;
import static java.lang.Double.parseDouble;
import static java.lang.Math.*;


public class NaiveBayes {

    private static double[] attackCount= new double[]{1.0,1.0,1.0,1.0,1.0,1.0,1.0,1.0,1.0,1.0};

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
            Hashtable<String,Double>[][] discParam = DiscreteParameters(); //proto,service,state,ct_state_ttl
            KernelDensityEstimator[][] contParam = KernelDensityProb();  //dur, dpkts, sbytes,dttl, sjit, ackdat, smean, dmean, ct_dst_src_ltm, ct_flw_http_mthd
            //ct_srv_dst, trans_depth, attack_cat
            List<String>[] results = KDE(discParam,contParam);
            PrintStream fileWriter = new PrintStream(new File("KDE_Results1.csv"));
            for (int i = 0; i < results[1].size(); i++) {
                fileWriter.println(results[0].get(i)+","+results[1].get(i));
                if (results[0].get(i).equals(results[1].get(i))) {
                    fullycorrect = fullycorrect + 1.0;
                } else {
                    fullywrong = fullywrong + 1.0;
                }
                /*if (results[0].get(i).equals("Normal") && results[1].get(i).equals("Normal") || !results[0].get(i).equals("Normal") && !results[1].get(i).equals("Normal")) {
                    partialcorrect = partialcorrect + 1.0;
                } else {
                    partialwrong = partialwrong + 1.0;
                }*/
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

        System.out.println("False Positive:"+falsePos);
        System.out.println("True Positive:"+truePos);
        System.out.println("False Negative:"+falseNeg);
        System.out.println("True Negative:"+trueNeg);


    }

    static KernelDensityEstimator[][] KernelDensityProb() throws FileNotFoundException {
        Scanner scanner=null;

        int update= 0;

        KernelDensityEstimator[][] KernelDensityProb = new KernelDensityEstimator[13][10];
        for (int i=0;i<13;i++){
            for (int j=0;j<10;j++){
                KernelDensityProb[i][j]=new KernelDensityEstimator();
            }
        }

        File trainingSet = new File("reduced_training-set.csv");
        scanner = new Scanner(trainingSet);
        scanner.nextLine();
        while (scanner.hasNextLine()) {
            String data = scanner.nextLine();
            String[] datum = data.split(",");
            update = Shared.whichAttack(datum);
            for (int i = 0; i < 12; i++) {
                KernelDensityProb[i][update].add(parseDouble(datum[Shared.getContFeatures()[i]]));
            }
        }

        for (int i=0;i<13;i++){
            for (int j=0;j<10;j++){
                KernelDensityProb[i][j].sort();
            }
        }
        scanner.close();
        return KernelDensityProb;
    }

    private static List<String>[] KDE(Hashtable<String,Double>[][] discParam,KernelDensityEstimator[][] contParam) throws FileNotFoundException {
        Scanner scanner=null;
        double attackSum =0.0;

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
            for (int i = 1; i < 10; i++) {
                if(ProbAttacks[i]>ProbAttacks[greatest]){
                    greatest=i;
                }
            }
            results[1].add(attacks[greatest]);

        }
        scanner.close();
        return results;
    }

    static Hashtable<String,Double>[][] DiscreteParameters() throws FileNotFoundException {
        int update= 0;

        Hashtable<String,Double>[][] featureCount = new Hashtable[4][10];
        for(int i=0; i<4;i++){
            for(int j=0; j<10;j++){
                featureCount[i][j]=new Hashtable<>();
            }
        }

        Scanner scanner=null;
        scanner = new Scanner(Shared.getTrainingSet());
        scanner.nextLine();
        while (scanner.hasNextLine()) {
            String data = scanner.nextLine();
            String[] datum = data.split(",");
            update = Shared.whichAttack(datum);
            attackCount[update]++;
            for (int i = 0; i < 4; i++) {
                if(featureCount[i][update].containsKey(datum[Shared.getDiscFeatures()[i]])){
                    featureCount[i][update].put(datum[Shared.getDiscFeatures()[i]],featureCount[i][update].get(datum[Shared.getDiscFeatures()[i]])+1.0);
                }
                else{
                    featureCount[i][update].put(datum[Shared.getDiscFeatures()[i]],1.0);
                }
            }
        }
        for(int feature =0; feature<4; feature++){
            for(int attack=0;attack<10;attack++){
                featureCount[feature][attack].put("smallest",1.0);
                for (String key:featureCount[feature][attack].keySet()) {
                    featureCount[feature][attack].put(key,(featureCount[feature][attack].get(key)/attackCount[attack]));
                    if(featureCount[feature][attack].get(key)<featureCount[feature][attack].get("smallest")){
                        featureCount[feature][attack].put("smallest",featureCount[feature][attack].get(key));
                    }
                }
            }
        }
        scanner.close();
        return featureCount;
    }

    private static Gaussian[][] GaussianProb() throws FileNotFoundException {
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

        File trainingSet = new File("reduced_training-set.csv");
        scanner = new Scanner(trainingSet);
        scanner.nextLine();
        while (scanner.hasNextLine()) {
            String data = scanner.nextLine();
            String[] datum = data.split(",");
            update = Shared.whichAttack(datum);
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

    private static List<String>[] GaussianEstimator(Hashtable<String,Double>[][] discParam,Gaussian[][] contParam) throws FileNotFoundException {
        Scanner scanner=null;
        double attackSum =0.0;

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
            }
            String data = scanner.nextLine();
            String[] datum = data.split(",");
            results[0].add(datum[Shared.getAttack_cat()]);
            for (int attack = 0; attack < 10; attack++) {
                //discrete features
                for (int feature = 0; feature < 4; feature++) {
                    if (discParam[1][attack].containsKey(datum[Shared.getDiscFeatures()[feature]])) {
                        ProbAttacks[attack] = ProbAttacks[attack] + log(discParam[1][attack].get(datum[Shared.getDiscFeatures()[feature]]));
                    }
                    else {
                        ProbAttacks[attack] = ProbAttacks[attack] + log(discParam[1][attack].get("smallest"));
                    }
                }

                //continous features
                for (int feature = 0; feature < 12; feature++) {
                    double x = parseDouble(datum[Shared.getContFeatures()[feature]]);
                    ProbAttacks[attack] = ProbAttacks[attack] + log(contParam[feature][attack].getProb(x));
                }
            }
            int greatest = 1;
            for (int i = 1; i < 10; i++) {
                if(ProbAttacks[i]<ProbAttacks[greatest]){
                    greatest=i;
                }
            }
            results[1].add(attacks[greatest]);
        }
        scanner.close();
        return results;
    }
}