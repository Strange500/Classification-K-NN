package fr.univlille.s3.S302.model;

import javafx.util.Pair;

import java.util.HashMap;
import java.util.Map;

public class Iris implements Data {

    private final double sepalLength;
    private final double sepalWidth;
    private final double petalLength;
    private final double petalWidth;
    public Pair<String, String> choosenAttributes;
    private String variety;
    private Coordonnee coordonnee;

    /**
     * Constructeur de la classe Iris
     * 
     * @param sepalLength la longueur du sépale
     * @param sepalWidth  la largeur du sépale
     * @param petalLength la longueur du pétale
     * @param petalWidth  la largeur du pétale
     * @param variety     l'espèce de la fleur
     */
    public Iris(double sepalLength, double sepalWidth, double petalLength, double petalWidth, String variety) {
        this.sepalLength = sepalLength;
        this.sepalWidth = sepalWidth;
        this.petalLength = petalLength;
        this.petalWidth = petalWidth;
        this.variety = variety;
        this.choosenAttributes = new Pair<>("sepalLength", "sepalWidth");
    }

    /**
     * @return la longueur du sépale
     */
    public double getSepalLength() {
        return sepalLength;
    }

    /**
     * @return la largeur du sépale
     */
    public double getSepalWidth() {
        return sepalWidth;
    }

    /**
     * @return la longueur du pétale
     */
    public double getPetalLength() {
        return petalLength;
    }

    /**
     * @return la largeur du pétale
     */
    public double getPetalWidth() {
        return petalWidth;
    }

    /**
     * @return l'espèce de la fleur
     */
    public String getSpecies() {
        return variety;
    }

    /**
     * @return une représentation textuelle de l'objet Iris
     */
    @Override
    public String toString() {
        return "Iris{" +
                "sepalLength=" + sepalLength +
                ", sepalWidth=" + sepalWidth +
                ", petalLength=" + petalLength +
                ", petalWidth=" + petalWidth +
                ", species='" + variety + '\'' +
                '}';
    }

    /**
     * @return les attributs de l'objet Iris sous la forme d'une map
     */
    @Override
    public Map<String, Number> getattributes() {
        Map<String, Number> attributes = new HashMap<>();
        attributes.put("sepalLength", sepalLength);
        attributes.put("sepalWidth", sepalWidth);
        attributes.put("petalLength", petalLength);
        attributes.put("petalWidth", petalWidth);
        return attributes;
    }

    /**
     * @return les coordonnées de l'objet Iris
     */
    @Override
    public String getCategory() {
        return variety;
    }

    /**
     * @param category la catégorie de l'objet Iris
     */
    @Override
    public void setCategory(String category) {
        variety = category;
    }

    public Data createObject(Map<String,Number> map) {
        if(!map.keySet().equals(this.getattributes().keySet())){
            throw new IllegalArgumentException();
        }
        return new Iris(map.get("sepalLength").doubleValue(),map.get("sepalWidth").doubleValue(),map.get("petalLength").doubleValue(),map.get("petalWidth").doubleValue(),"Unknow");
    }
}
