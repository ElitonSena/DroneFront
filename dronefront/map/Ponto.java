package dronefront.map;

public class Ponto {
    private final double x;
    private final double y;

    public Ponto(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    @Override
    public String toString() {
        return String.format("x=%.1f, y=%.1f)", x, y);
    }
}