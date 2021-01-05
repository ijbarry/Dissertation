
import java.awt.image.Kernel;
import java.io.File;
import java.io.FileNotFoundException;
import java.lang.reflect.Array;
import java.util.*;

import static java.lang.Double.doubleToLongBits;
import static java.lang.Double.parseDouble;
import static java.lang.Math.*;


public class NaiveBayes {
    static File trainingSet = new File("reduced_training-set.csv");
    static File testingSet = new File("reduced_testing-set.csv");

    //discrete features
    private static int proto = 1;
    private static int service = 2;
    private static int state = 3;
    private static int ct_state_ttl = 11;
    private static int attack_cat=16;
    private static int[] discFeatures= new int[]{proto,service,state,ct_state_ttl};


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


    //attacks
    private static int analysis=0;
    private static int backdoor=1;
    private static int dos=2;
    private static int exploits=3;
    private static int fuzzers=4;
    private static int generic=5;
    private static int reconnaissance=6;
    private static int shellcode=7;
    private static int worms=8;
    private static int normal=9;

    private static double[] attackCount= new double[]{1.0,1.0,1.0,1.0,1.0,1.0,1.0,1.0,1.0,1.0};

    public static void main(String[] args) {
       Hashtable<String,Double>[][] discParam = DiscreteParameters(); //proto,service,state,ct_state_ttl
        KernelDensityEstimator[][] contParam = KernelDensityProb();  //dur, dpkts, sbytes,dttl, sjit, ackdat, smean, dmean, ct_dst_src_ltm, ct_flw_http_mthd
        //ct_srv_dst, trans_depth, attack_cat

        List<String>[] results = KDE(discParam,contParam);
        double fullycorrect =0.0;
        double fullywrong = 0.0;
        double partialcorrect =0.0;
        double partialwrong = 0.0;
        for (int i =0;i<results[1].size();i++) {
            if(results[0].get(i).equals(results[1].get(i))){
                fullycorrect=fullycorrect+1.0;
            }
            else {
                fullywrong=fullywrong+1.0;
            }
            if(results[0].get(i).equals("Normal")&&results[1].get(i).equals("Normal")||!results[0].get(i).equals("Normal")&&!results[1].get(i).equals("Normal")){
                partialcorrect=partialcorrect+1.0;
            }
            else {
                partialwrong=partialwrong+1.0;
            }
        }
        System.out.println("correct type:"+fullycorrect);
        System.out.println("wrong type:"+fullywrong);
        double fullaccuracy= fullycorrect/(fullycorrect+fullywrong);
        System.out.println("typing accuracy:"+fullaccuracy);

        System.out.println("correct attack identification:"+partialcorrect);
        System.out.println("wrong  attack identification:"+partialwrong);
        double partialaccuracy= partialcorrect/(partialcorrect+partialwrong);
        System.out.println("typing accuracy:"+partialaccuracy);


    }

    private static KernelDensityEstimator[][] KernelDensityProb() {

        int analysis=0;
        int backdoor=1;
        int dos=2;
        int exploits=3;
        int fuzzers=4;
        int generic=5;
        int reconnaissance=6;
        int shellcode=7;
        int worms=8;
        int normal=9;
        Scanner scanner=null;

        int update= 0;

        KernelDensityEstimator[][] KernelDensityProb = new KernelDensityEstimator[13][10];
        for (int i=0;i<13;i++){
            for (int j=0;j<10;j++){
                KernelDensityProb[i][j]=new KernelDensityEstimator();
            }
        }

        try {
            File trainingSet = new File("reduced_training-set.csv");
            scanner = new Scanner(trainingSet);
            scanner.nextLine();
            while (scanner.hasNextLine()) {
                String data = scanner.nextLine();
                String[] datum = data.split(",");
                switch (datum[attack_cat]){
                    case "Analysis":
                        update= analysis;
                        break;
                    case "Backdoor":
                        update= backdoor;
                        break;
                    case "DoS":
                        update= dos;
                        break;
                    case "Exploits":
                        update= exploits;
                        break;
                    case "Fuzzers":
                        update= fuzzers;
                        break;
                    case "Generic":
                        update= generic;
                        break;
                    case "Reconnaissance":
                        update= reconnaissance;
                        break;
                    case "Shellcode":
                        update= shellcode;
                        break;
                    case "Worms":
                        update= worms;
                        break;
                    default:
                        update= normal;
                        break;
                }
                for (int i = 0; i < 12; i++) {
                    KernelDensityProb[i][update].add(parseDouble(datum[contFeatures[i]]));

                }
            }
        }
        catch (FileNotFoundException e){
            System.out.println("FileNotFoundException.");
            e.printStackTrace();
        }
        finally {
            scanner.close();
        }

        return KernelDensityProb;
    }

    private static List<String>[] KDE(Hashtable<String,Double>[][] discParam,KernelDensityEstimator[][] contParam){
        Scanner scanner=null;
        double attackSum =0.0;

        for (double count:attackCount) {
            attackSum += count;
        }
        List<String>[] results = new ArrayList[]{new ArrayList<String>(),new ArrayList<String>()};
        String[] attacks = new String[]{"Analysis","Backdoor","DoS","Exploits","Fuzzers","Generic","Reconnaissance","Shellcode","Worms","Normal"};
        try {
            scanner = new Scanner(testingSet);
            scanner.nextLine();
            while (scanner.hasNextLine()) {
                double[] ProbAttacks = new double[10];
                for (int i = 0; i < 10; i++) {
                    ProbAttacks[i]=log(attackCount[i]/attackSum);
                }
                String data = scanner.nextLine();
                String[] datum = data.split(",");
                results[0].add(datum[attack_cat]);

                for (int attack = 0; attack < 10; attack++) {
                    //discrete features
                    for (int feature = 0; feature < 4; feature++) {
                        if (discParam[1][attack].containsKey(datum[discFeatures[feature]])) {
                            ProbAttacks[attack] = ProbAttacks[attack] + log(discParam[1][attack].get(datum[discFeatures[feature]]));
                        }
                        else {
                            ProbAttacks[attack] = ProbAttacks[attack] + log(discParam[1][attack].get("smallest"));
                        }
                    }

                    //continous features
                    for (int feature = 0; feature < 12; feature++) {
                        double x = parseDouble(datum[contFeatures[feature]]);
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
        }
        catch (FileNotFoundException e){
            System.out.println("FileNotFoundException.");
            e.printStackTrace();
        }
        finally {
            scanner.close();
        }
        return results;
    }

    private static Hashtable<String,Double>[][] DiscreteParameters() {
        int update= 0;

        Hashtable<String,Double>[][] featureCount = new Hashtable[4][10];
        for(int i=0; i<4;i++){
            for(int j=0; j<10;j++){
                featureCount[i][j]=new Hashtable<>();
            }
        }

        Scanner scanner=null;

        try {
            scanner = new Scanner(trainingSet);
            scanner.nextLine();
            while (scanner.hasNextLine()) {
                String data = scanner.nextLine();
                String[] datum = data.split(",");
                switch (datum[attack_cat]){
                    case "Analysis":
                        update= analysis;
                        break;
                    case "Backdoor":
                        update= backdoor;
                        break;
                    case "DoS":
                        update= dos;
                        break;
                    case "Exploits":
                        update= exploits;
                        break;
                    case "Fuzzers":
                        update= fuzzers;
                        break;
                    case "Generic":
                        update= generic;
                        break;
                    case "Reconnaissance":
                        update= reconnaissance;
                        break;
                    case "Shellcode":
                        update= shellcode;
                        break;
                    case "Worms":
                        update= worms;
                        break;
                    default:
                        update= normal;
                        break;
                }
                attackCount[update]++;
                for (int i = 0; i < 4; i++) {
                    if(featureCount[i][update].containsKey(datum[discFeatures[i]])){
                        featureCount[i][update].put(datum[discFeatures[i]],featureCount[i][update].get(datum[discFeatures[i]])+1.0);
                    }
                    else{
                        featureCount[i][update].put(datum[discFeatures[i]],1.0);
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
        } catch (FileNotFoundException e) {
            System.out.println("FileNotFoundException.");
            e.printStackTrace();
        }
        finally {
            scanner.close();
        }
        return featureCount;

    }

    private static Gaussian[][] GaussianProb() {

        int analysis=0;
        int backdoor=1;
        int dos=2;
        int exploits=3;
        int fuzzers=4;
        int generic=5;
        int reconnaissance=6;
        int shellcode=7;
        int worms=8;
        int normal=9;
        Scanner scanner=null;

        int update= 0;

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

        try {
            File trainingSet = new File("reduced_training-set.csv");
            scanner = new Scanner(trainingSet);
            scanner.nextLine();
            while (scanner.hasNextLine()) {
                String data = scanner.nextLine();
                String[] datum = data.split(",");
                switch (datum[attack_cat]){
                    case "Analysis":
                        update= analysis;
                        break;
                    case "Backdoor":
                        update= backdoor;
                        break;
                    case "DoS":
                        update= dos;
                        break;
                    case "Exploits":
                        update= exploits;
                        break;
                    case "Fuzzers":
                        update= fuzzers;
                        break;
                    case "Generic":
                        update= generic;
                        break;
                    case "Reconnaissance":
                        update= reconnaissance;
                        break;
                    case "Shellcode":
                        update= shellcode;
                        break;
                    case "Worms":
                        update= worms;
                        break;
                    default:
                        update= normal;
                        break;
                }
                for (int i = 0; i < 12; i++) {
                    splitCont[i][update].add(parseDouble(datum[contFeatures[i]]));
                    contParam[i][update].addLeft(parseDouble(datum[contFeatures[i]]));
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
        }
        catch (FileNotFoundException e){
            System.out.println("FileNotFoundException.");
            e.printStackTrace();
        }
        finally {
            scanner.close();
        }

        Gaussian[][] GaussianProb = new Gaussian[13][10];
        for (int i=0;i<13;i++){
            for (int j=0;j<10;j++){
                GaussianProb[i][j]=new Gaussian(contParam[i][j].getLeft(),contParam[i][j].getRight());
            }
        }
        return GaussianProb;
    }

    private static List<String>[] GaussianEstimator(Hashtable<String,Double>[][] discParam,Gaussian[][] contParam){
            Scanner scanner=null;
            double attackSum =0.0;

            for (double count:attackCount) {
                attackSum += count;
            }
            List<String>[] results = new ArrayList[]{new ArrayList<String>(),new ArrayList<String>()};
            String[] attacks = new String[]{"Analysis","Backdoor","DoS","Exploits","Fuzzers","Generic","Reconnaissance","Shellcode","Worms","Normal"};
            try {
                scanner = new Scanner(testingSet);
                scanner.nextLine();
                while (scanner.hasNextLine()) {
                    double[] ProbAttacks = new double[10];
                    for (int i = 0; i < 10; i++) {
                        ProbAttacks[i]=log(attackCount[i]/attackSum);
                    }
                    String data = scanner.nextLine();
                    String[] datum = data.split(",");
                    results[0].add(datum[attack_cat]);

                    for (int attack = 0; attack < 10; attack++) {
                        //discrete features
                        for (int feature = 0; feature < 4; feature++) {
                            if (discParam[1][attack].containsKey(datum[discFeatures[feature]])) {
                                ProbAttacks[attack] = ProbAttacks[attack] + log(discParam[1][attack].get(datum[discFeatures[feature]]));
                            }
                            else {
                                ProbAttacks[attack] = ProbAttacks[attack] + log(discParam[1][attack].get("smallest"));
                            }
                        }

                        //continous features
                        for (int feature = 0; feature < 12; feature++) {
                            double x = parseDouble(datum[contFeatures[feature]]);
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
            }
            catch (FileNotFoundException e){
                System.out.println("FileNotFoundException.");
                e.printStackTrace();
            }
            finally {
                scanner.close();
            }
        return results;
    }
}
