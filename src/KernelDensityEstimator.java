import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import static java.lang.Math.*;

public class KernelDensityEstimator extends Estimator{
    private List<Double> data;
    private double[] dataSort;
    private int listSize = 0;
    private double listSum = 0;
    private boolean init = true;

    private static double pi = 3.14159265359;

    public KernelDensityEstimator() {
        data = new ArrayList<Double>();
    }

    public void add(double value) {
        this.data.add(value);
        listSize++;
        listSum += value;
    }
    private double bandwidth = 0.5;
    public void silvermanBandwidth() {
        sort();
        double stdDev=0.0;
        double lowerQuartile = Math.floor(listSize/4);
        double upperQuartile = Math.floor(3*listSize/4);
        double IQR=0.0;
        for(int i=0;i<listSize;i++){
            Double diff = dataSort[i]- listSum/listSize;
            stdDev += diff * diff;
            if(i==lowerQuartile){
                IQR-=lowerQuartile;
            }
            else if(i==upperQuartile){
                IQR+=upperQuartile;
            }
        }
        stdDev=sqrt(stdDev/listSize);
        IQR=IQR/1.34;
        double A= min(max(1.0,IQR),stdDev);
        bandwidth=1.06*A/(pow(listSize,0.2));
    }

    public void sort() {
        data.sort(Comparator.naturalOrder());
        dataSort=new double[listSize];
        for (int i=0;i<listSize;i++) {
            dataSort[i]=data.get(i).doubleValue();
        }
    }

    @Override
    public double getProb(double x){
        double prob=0.0;

        for (double x_i:data) {
            prob += exp(-0.5*pow((x-x_i)/bandwidth,2))/(listSize*bandwidth*sqrt(2*pi));// (e^(((x-xi)/bandwidth)^2))/(n*bandwidth*sqrt(2pi))
        }

        return prob;
    }

}