package fr.univlille.s3.S302.model;

public class Coordonnee {

    private final double x;
    private final double y;

    /**
     * Constructeur de la classe Coordonnee
     * 
     * @param x la coordonnée x
     * @param y la coordonnée y
     */
    public Coordonnee(double x, double y) {
        this.x = x;
        this.y = y;
    }

    /**
     * @return la coordonnée x
     */
    public double getX() {
        return x;
    }

    /**
     * @return la coordonnée y
     */
    public double getY() {
        return y;
    }

    /**
     * @return une représentation textuelle de la coordonnée
     */
    @Override
    public String toString() {
        return "Coordonnee{" + "x=" + x + ", y=" + y + '}';
    }
}
