package fr.univlille.s3.S302.model;

import com.opencsv.bean.CsvBindByName;
import fr.univlille.s3.S302.utils.HasNoOrder;

public class Iris extends Data {

    static {
        DataLoader.registerHeader(Iris.class, "\"sepal.length\",\"sepal.width\",\"petal.length\",\"petal.width\",\"variety\"");
    }

    @CsvBindByName(column = "sepal.length")
    protected Double sepalLength;
    @CsvBindByName(column = "sepal.width")
    protected Double sepalWidth;
    @CsvBindByName(column = "petal.length")
    protected Double petalLength;
    @CsvBindByName(column = "petal.width")
    protected Double petalWidth;

    @CsvBindByName(column = "variety")
    @HasNoOrder
    protected String species;

}
