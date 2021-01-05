import java.util.ArrayList;
import java.util.List;

import static java.lang.Math.*;

public class KernelDensityEstimator {
    private List<Double> data;
    private int listSize=0;
    private double listSum=0;

    private double bandwidth=3;
    private static double pi = 3.14159265359;

    public KernelDensityEstimator(){
        data = new ArrayList<Double>();
    }

    public void add(double value) {
        this.data.add(value);
        listSize++;
        listSum +=value;
    }

    public double getProb(double x){

        double prob=0.0;

        for (double x_i:data) {
            prob += exp(-0.5*pow((x-x_i)/bandwidth,2))/(listSize*bandwidth*sqrt(2*pi));// (e^(((x-xi)/bandwidth)^2))/(n*bandwidth*sqrt(2pi))
        }

        return prob;
    }

}
