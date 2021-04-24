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
        double norm = (x-this.mean)/this.stdDev;
        if(exp(-0.5*norm*norm)/(stdDev*sqrt(2*pi)) ==0){
            return 0.0000001;
        }
        return exp(-0.5*(norm*norm))/(stdDev*sqrt(2*pi)); // (e^(((x-u)/2*rho)^2))/(rho*sqrt(2pi))
    }
}