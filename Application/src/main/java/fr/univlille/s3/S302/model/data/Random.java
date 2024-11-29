package fr.univlille.s3.S302.model.data;

import com.opencsv.bean.CsvBindByName;
import com.opencsv.bean.CsvDate;
import fr.univlille.s3.S302.model.Data;
import fr.univlille.s3.S302.model.DataLoader;

import java.util.Date;

public class Random extends Data {

    static {
        DataLoader.registerHeader(Random.class, "\"date\",\"name\"");
    }
    @CsvDate(value = "dd-MM-yyyy")
    @CsvBindByName(column = "date")
    public Date date;

    @CsvBindByName(column = "name")
    public String name;
}
