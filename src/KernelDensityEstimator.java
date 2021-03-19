import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import static java.lang.Math.*;

public class KernelDensityEstimator {
    private List<Double> data;
    private double[] data1;
    private int listSize = 0;
    private double listSum = 0;
    private boolean init = true;

    private double bandwidth = 3;
    private static double pi = 3.14159265359;

    public KernelDensityEstimator() {
        data = new ArrayList<Double>();
    }

    public void add(double value) {
        this.data.add(value);
        listSize++;
        listSum += value;
    }

    public void sort() {
        data.sort(Comparator.naturalOrder());
        data1=new double[listSize];
        for (int i=0;i<listSize;i++) {
            data1[i]=data.get(i).doubleValue();
        }
    }

/*    public double getProb(double x) {
        // to optimize the computation, we find all sample values which have positive weights
        int from = Arrays.binarySearch(data1, (x - 4 * bandwidth));
        if (from < 0) from = -from - 1;
        int to = Arrays.binarySearch(data1, (x + 4 * bandwidth));
        if (to < 0) to = -to - 1;
        double prob = 0;
        for (int x_i = from; x_i < to; x_i++) {
            prob += exp(-0.5 * pow((x - x_i) / bandwidth, 2));// (e^(((x-xi)/bandwidth)^2))/(n*bandwidth*sqrt(2pi))
        }
        return prob / (listSize * bandwidth);
    }
    */




    public double getProb(double x){
        double prob=0.0;

        for (double x_i:data) {
            prob += exp(-0.5*pow((x-x_i)/bandwidth,2))/(listSize*bandwidth*sqrt(2*pi));// (e^(((x-xi)/bandwidth)^2))/(n*bandwidth*sqrt(2pi))
        }

        return prob;
    }

}
