package fr.univlille.s3.S302.model;

import com.opencsv.bean.CsvBindByName;
import fr.univlille.s3.S302.utils.HasOrder;

public class random extends Data {

    static {
        DataLoader.registerHeader(random.class, "\"test\",\"name\"");
    }

    @CsvBindByName(column = "test")
    @HasOrder
    protected String test;

    @CsvBindByName(column = "name")
    protected String name;

    public static void main(String[] args) {
        DataManager<Data> dm = new DataManager<>("iris.csv");
        System.out.println(dm.getDataList());
    }

}
