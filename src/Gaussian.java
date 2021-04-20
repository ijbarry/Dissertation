import static java.lang.Math.*;

public class Gaussian extends Estimator{
    private double mean;
    private double stdDev;
    private static final double pi = 3.14159265359;
    public Gaussian(double mean,double stdDev){
        this.mean =mean;
        this.stdDev= stdDev;
    }

    public void setMean(double mean) {
        this.mean = mean;
    }

    public void setStdDev(double stdDev) {
        this.stdDev = stdDev;
    }

    @Override
    public double getProb(double x) {
        return exp(-0.5*pow((x-mean)/stdDev,2))/(stdDev*sqrt(2*pi)); // (e^(((x-u)/rho)^2))/(rho*sqrt(2pi))
    }
}