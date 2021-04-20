import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Shared {
    private static File trainingSet = new File("Dataset/reduced_training-set.csv");
    private static File testingSet = new File("Dataset/reduced_testing-set.csv");

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
    private static int dttl=6;
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


    public static File getTestingSet() {
        return testingSet;
    }
    public static File getTrainingSet() {
        return trainingSet;
    }
    public static int getProto() {
        return proto;
    }
    public static int getService() {
        return service;
    }
    public static int getState() {
        return state;
    }
    public static int getCt_state_ttl() {
        return ct_state_ttl;
    }
    public static int getAttack_cat() {
        return attack_cat;
    }
    public static int[] getDiscFeatures() {
        return discFeatures;
    }

    public static int getDur() {return dur; }
    public static int getDpkts() {return dpkts; }
    public static int getSbytes() {return sbytes; }
    public static int getDttl() {return dttl; }
    public static int getSjit() {return sjit; }
    public static int getAckdat() {return ackdat; }
    public static int getSmean() {return smean; }
    public static int getDmean() {return dmean; }
    public static int getCt_dst_src_ltm() {return ct_dst_src_ltm; }
    public static int getCt_flw_http_mthd() {return ct_flw_http_mthd; }
    public static int getCt_srv_dst() {return ct_srv_dst; }
    public static int getTrans_depth() {return trans_depth; }
    public static int[] getContFeatures() {return contFeatures; }

    public static int getAnalysis() {return analysis; }
    public static int getBackdoor() {return backdoor; }
    public static int getDos() {return dos; }
    public static int getExploits() {return exploits; }
    public static int getFuzzers() {return fuzzers; }
    public static int getGeneric() {return generic; }
    public static int getReconnaissance() {return reconnaissance; }
    public static int getShellcode() {return shellcode; }
    public static int getWorms() {return worms; }
    public static int getNormal() {return normal; }


    public static int whichAttack(String data){
        int update=0;
        switch (data){
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
        return update;
    }
}