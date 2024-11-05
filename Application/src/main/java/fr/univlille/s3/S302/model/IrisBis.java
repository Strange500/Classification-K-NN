package fr.univlille.s3.S302.model;

import com.opencsv.bean.CsvBindByName;

public class IrisBis implements AbstractData{

    @CsvBindByName(column = "sepal.length")
    protected double sepalLength;

    @CsvBindByName(column = "sepal.width")
    protected double sepalWidth;

    @CsvBindByName(column = "petal.length")
    protected double petalLength;

    @CsvBindByName(column = "petal.width")
    protected double petalWidth;


    public static void main(String[] args) {
        IrisBis iris = new IrisBis();
        iris.sepalLength = 5.1;
        iris.sepalWidth = 3.5;
        iris.petalLength = 1.4;
        iris.petalWidth = 0.2;
        Data d = iris.getData(iris);
        System.out.println(d.getattributes());

    }
}
