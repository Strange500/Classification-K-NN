package fr.univlille.s3.S302.model;

import com.opencsv.bean.CsvBindByName;
import fr.univlille.s3.S302.utils.HasNoOrder;
import javafx.util.Pair;

import java.util.HashMap;
import java.util.Map;

public class Iris extends Data {

    static {
        DataLoader.registerHeader(Iris.class, "\"sepal.length\",\"sepal.width\",\"petal.length\",\"petal.width\",\"variety\"");
    }

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



}
