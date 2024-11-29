package fr.univlille.s3.S302.model.data;

import com.opencsv.bean.CsvBindByName;
import fr.univlille.s3.S302.model.Data;
import fr.univlille.s3.S302.model.DataLoader;
import fr.univlille.s3.S302.utils.HasNoOrder;

public class Pokemon extends Data {

    static {
        DataLoader.registerHeader(Pokemon.class,
                "name,attack,base_egg_steps,capture_rate,defense,experience_growth,hp,sp_attack,sp_defense,type1,type2,speed,is_legendary");
    }

    @CsvBindByName(column = "name")
    @HasNoOrder
    public String name;

    @CsvBindByName(column = "attack")
    public Integer attack;

    @CsvBindByName(column = "base_egg_steps")
    public Integer base_egg_steps;

    @CsvBindByName(column = "capture_rate")
    public Double capture_rate;

    @CsvBindByName(column = "defense")
    public Integer defense;

    @CsvBindByName(column = "experience_growth")
    public Integer experience_growth;

    @CsvBindByName(column = "hp")
    public Integer hp;

    @CsvBindByName(column = "sp_attack")
    public Integer sp_attack;

    @CsvBindByName(column = "sp_defense")
    public Integer sp_defense;

    @CsvBindByName(column = "type1")
    @HasNoOrder
    public String type1;

    @CsvBindByName(column = "type2")
    @HasNoOrder
    public String type2;

    @CsvBindByName(column = "speed")
    public Double speed;

    @CsvBindByName(column = "is_legendary")
    @HasNoOrder
    public Boolean is_legendary;

}
