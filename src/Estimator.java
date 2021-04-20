public interface Estimator {
    default double getProb(double x) {
        return 0;
    }
}
