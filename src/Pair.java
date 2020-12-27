public class Pair {
    private Double Left;
    private Double Right;
    

    public Pair(){
        Left= 0.0;
        Right= 0.0;
    }

    public Double getLeft() {
        return Left;
    }

    public Double getRight() {
        return Right;
    }

    public void setLeft(Double left) {
        Left = left;
    }

    public void setRight(Double right) {
        Right = right;
    }

    public void addLeft(Double left) {
        Left += left;
    }

    public void addRight(Double right) {
        Right += right;
    }

    public String toString() {
        return "<"+Left.toString()+","+Right.toString()+">";
    }
}
