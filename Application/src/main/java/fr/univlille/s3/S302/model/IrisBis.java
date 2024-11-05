package fr.univlille.s3.S302.model;

import com.opencsv.bean.CsvBindByName;
import fr.univlille.s3.S302.utils.Countable;

public class IrisBis implements AbstractData{

    @CsvBindByName(column = "sepal.length")
    protected double sepalLength;

    @CsvBindByName(column = "sepal.width")
    protected double sepalWidth;

    @CsvBindByName(column = "petal.length")
    protected double petalLength;

    @CsvBindByName(column = "petal.width")
    @Countable
    protected String petalWidth;


    public static void main(String[] args) {
        IrisBis iris = new IrisBis();
        iris.sepalLength = 5.1;
        iris.sepalWidth = 3.5;
        iris.petalLength = 1.4;
        iris.petalWidth = "0.2";
        Data d = iris.getData(iris);
        System.out.println(d.getattributes());
        // create second
        IrisBis iris2 = new IrisBis();
        iris2.sepalLength = 4.9;
        iris2.sepalWidth = 3.0;
        iris2.petalLength = 1.4;
        iris2.petalWidth = "0.4";
        Data d2 = iris2.getData(iris2);
        System.out.println(d2.getattributes());

    }
}
