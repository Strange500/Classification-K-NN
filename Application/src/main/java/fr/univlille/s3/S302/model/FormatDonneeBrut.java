package fr.univlille.s3.S302.model;

import com.opencsv.bean.CsvBindByName;

import java.util.HashMap;
import java.util.Map;

public class FormatDonneeBrut implements Data {

    @CsvBindByName(column = "sepal.length")
    protected double sepalLength;
    @CsvBindByName(column = "sepal.width")
    protected double sepalWidth;
    @CsvBindByName(column = "petal.length")
    protected double petalLength;
    @CsvBindByName(column = "petal.width")
    protected double petalWidth;
    @CsvBindByName(column = "variety")
    protected String species;

    /**
     * Cree un objet a partir d'un objet FormatDonneeBrut
     * 
     * @param f l'objet FormatDonneeBrut
     * @return l'objet Data
     */
    public static Data createObject(FormatDonneeBrut f) {
        return new Iris(f.sepalLength, f.sepalWidth, f.petalLength, f.petalWidth, f.species);
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

    /**
     * @return une représentation textuelle de l'objet
     */
    @Override
    public String toString() {
        return "Iris{" + "sepalLength=" + sepalLength + ", sepalWidth=" + sepalWidth + ", petalLength=" + petalLength
                + ", petalWidth=" + petalWidth + ", species='" + species + '\'' + '}';
    }

    /**
     * @return les attributs de la donnée
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
     * @return la catégorie de la donnée
     */
    @Override
    public String getCategory() {
        return species;
    }

    /**
     * @param category la nouvelle catégorie de la donnée
     */
    @Override
    public void setCategory(String category) {
        species = category;
    }
}
