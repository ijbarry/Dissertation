import java.awt.image.Kernel;
import java.io.*;
import java.lang.reflect.Array;
import java.util.*;

import static java.lang.Double.doubleToLongBits;
import static java.lang.Double.parseDouble;
import static java.lang.Math.*;


public interface NaiveBayes {
    static void WriteResults(List<String>[] results, String type) throws FileNotFoundException{
        double fullycorrect =0.0;
        double fullywrong = 0.0;
        double partialcorrect =0.0;
        double partialwrong = 0.0;

        double truePos=0.0;
        double falsePos=0.0;
        double trueNeg=0.0;
        double falseNeg=0.0;
        int actual = 0;
        int predicted = 1;
        PrintStream fileWriter;
        switch (type) {
            case ("Gaussian"):
                fileWriter = new PrintStream(new File("NB_Gaussian_Results.csv"));
                break;
            case ("KDE"):
                fileWriter = new PrintStream(new File("Results/NB_KDE_Results.csv"));
            default:
                fileWriter = new PrintStream(new File("Results/NB_NewEstimator_Results.csv"));
        }

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
            if(results[actual].get(i).equals("Normal")&&results[predicted].get(i).equals("Normal")){
                trueNeg+=1.0;
            }
            else if(results[actual].get(i).equals("Normal")&&!results[predicted].get(i).equals("Normal")){
                falsePos+=1.0;
            }
            else if(!results[actual].get(i).equals("Normal")&&!results[predicted].get(i).equals("Normal")){
                truePos+=1.0;
            }
            else if(!results[actual].get(i).equals("Normal")&&results[predicted].get(i).equals("Normal")){
                falseNeg+=1.0;
            }
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

    static double[] attackCount= new double[]{1.0,1.0,1.0,1.0,1.0,1.0,1.0,1.0,1.0,1.0};
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
            update = Shared.whichAttack(datum[Shared.getAttack_cat()]);
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
    static List<String>[] NBResults(Hashtable<String,Double>[][] discParam,Estimator[][] contParam) throws FileNotFoundException {
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