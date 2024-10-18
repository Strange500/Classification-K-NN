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

    public Iris(double sepalLength, double sepalWidth, double petalLength, double petalWidth, String variety) {
        this.sepalLength = sepalLength;
        this.sepalWidth = sepalWidth;
        this.petalLength = petalLength;
        this.petalWidth = petalWidth;
        this.variety = variety;
        this.choosenAttributes = new Pair<>("sepalLength", "sepalWidth");
    }

    public double getSepalLength() {
        return sepalLength;
    }

    public double getSepalWidth() {
        return sepalWidth;
    }

    public double getPetalLength() {
        return petalLength;
    }

    public double getPetalWidth() {
        return petalWidth;
    }

    public String getSpecies() {
        return variety;
    }

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

    @Override
    public Map<String, Number> getattributes() {
        Map<String, Number> attributes = new HashMap<>();
        attributes.put("sepalLength", sepalLength);
        attributes.put("sepalWidth", sepalWidth);
        attributes.put("petalLength", petalLength);
        attributes.put("petalWidth", petalWidth);
        return attributes;
    }

    @Override
    public String getCategory() {
        return variety;
    }

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
