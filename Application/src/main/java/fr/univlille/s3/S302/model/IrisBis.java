package fr.univlille.s3.S302.model;

import com.opencsv.bean.CsvBindByName;
import fr.univlille.s3.S302.utils.HasOrder;

import java.util.Map;

public class IrisBis extends Data {

    @CsvBindByName(column = "sepal.length")
    protected double sepalLength;

    @CsvBindByName(column = "sepal.width")
    protected double sepalWidth;

    @CsvBindByName(column = "petal.length")
    protected double petalLength;

    @CsvBindByName(column = "petal.width")
    @HasOrder
    protected String petalWidth;


    public static void main(String[] args) {
        IrisBis iris = new IrisBis();
        iris.sepalLength = 5.1;
        iris.sepalWidth = 3.5;
        iris.petalLength = 1.4;
        iris.petalWidth = "0.2";
        Map<String, Number> d = iris.getattributes();
        System.out.println(d);
        // create second
        IrisBis iris2 = new IrisBis();
        iris2.sepalLength = 4.9;
        iris2.sepalWidth = 3.0;
        iris2.petalLength = 1.4;
        iris2.petalWidth = "0.4";
        Map<String, Number> d2 = iris2.getattributes();
        System.out.println(d2);
    }
}
