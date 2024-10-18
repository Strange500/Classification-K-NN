package fr.univlille.s3.S302.model;

import com.opencsv.bean.CsvBindByName;

import java.util.HashMap;
import java.util.Map;

public class FormatDonneeBrut implements Data {
    @CsvBindByName(column = "sepal.length")
    private double sepalLength;
    @CsvBindByName(column = "sepal.width")
    private double sepalWidth;
    @CsvBindByName(column = "petal.length")
    private double petalLength;
    @CsvBindByName(column = "petal.width")
    private double petalWidth;
    @CsvBindByName(column = "variety")
    private String species;

    public static Data createObject(FormatDonneeBrut f) {
        return new Iris(f.getSepalLength(), f.getSepalWidth(), f.getPetalLength(), f.getPetalWidth(), f.getSpecies());
    }

    @Override
    public Data createObject(Map<String,Number> map) {
        return null;
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
        return species;
    }

    @Override
    public String toString() {
        return "Iris{" + "sepalLength=" + sepalLength + ", sepalWidth=" + sepalWidth + ", petalLength=" + petalLength + ", petalWidth=" + petalWidth + ", species='" + species + '\'' + '}';
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
        return species;
    }

    @Override
    public void setCategory(String category) {
        species = category;
    }


}
