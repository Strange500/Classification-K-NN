package fr.univlille.s3.S302.model.data;

import com.opencsv.bean.CsvBindByName;
import fr.univlille.s3.S302.model.Data;
import fr.univlille.s3.S302.model.DataLoader;
import fr.univlille.s3.S302.utils.HasNoOrder;

public class Iris extends Data {

    static {
        DataLoader.registerHeader(Iris.class,
                "\"sepal.length\",\"sepal.width\",\"petal.length\",\"petal.width\",\"variety\"");
    }

    @CsvBindByName(column = "sepal.length")
    public Double sepalLength;
    @CsvBindByName(column = "sepal.width")
    public Double sepalWidth;
    @CsvBindByName(column = "petal.length")
    public Double petalLength;
    @CsvBindByName(column = "petal.width")
    public Double petalWidth;

    @CsvBindByName(column = "variety")
    @HasNoOrder
    public String species;

}
