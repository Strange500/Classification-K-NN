package fr.univlille.s3.S302.model;

import com.opencsv.bean.CsvBindByName;
import com.opencsv.bean.CsvDate;

import java.util.Date;


public class Random extends Data {

    static {
        DataLoader.registerHeader(Random.class, "\"date\",\"name\"");
    }
    @CsvDate(value = "dd-MM-yyyy")
    @CsvBindByName(column = "date")
    protected Date date;

    @CsvBindByName(column = "name")
    protected String name;
}
