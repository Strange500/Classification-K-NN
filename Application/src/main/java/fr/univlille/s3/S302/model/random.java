package fr.univlille.s3.S302.model;

import com.opencsv.bean.CsvBindByName;
import com.opencsv.bean.CsvDate;

import java.util.Date;

public class random extends Data {

    static {
        DataLoader.registerHeader(random.class, "\"date\",\"name\"");
    }
    @CsvDate(value = "dd-MM-yyyy")
    @CsvBindByName(column = "date")
    protected Date date;

    @CsvBindByName(column = "name")
    protected String name;

    public static void main(String[] args) {
        DataManager<Data> dm = new DataManager<>("iris.csv");
        System.out.println(dm.getDataList());
    }

}
