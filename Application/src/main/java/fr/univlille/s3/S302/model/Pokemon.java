package fr.univlille.s3.S302.model;

import com.opencsv.bean.CsvBindByName;
import fr.univlille.s3.S302.utils.HasNoOrder;

public class Pokemon extends Data {

    static {
        DataLoader.registerHeader(Pokemon.class, "name,attack,base_egg_steps,capture_rate,defense,experience_growth,hp,sp_attack,sp_defense,type1,type2,speed,is_legendary");
    }

    @CsvBindByName(column = "name")
    @HasNoOrder
    protected String name;

    @CsvBindByName(column = "attack")
    protected Integer attack;

    @CsvBindByName(column = "base_egg_steps")
    protected Integer base_egg_steps;

    @CsvBindByName(column = "capture_rate")
    protected Double capture_rate;

    @CsvBindByName(column = "defense")
    protected Integer defense;

    @CsvBindByName(column = "experience_growth")
    protected Integer experience_growth;

    @CsvBindByName(column = "hp")
    protected Integer hp;

    @CsvBindByName(column = "sp_attack")
    protected Integer sp_attack;

    @CsvBindByName(column = "sp_defense")
    protected Integer sp_defense;

    @CsvBindByName(column = "type1")
    @HasNoOrder
    protected String type1;

    @CsvBindByName(column = "type2")
    @HasNoOrder
    protected String type2;

    @CsvBindByName(column = "speed")
    protected Double speed;

    @CsvBindByName(column = "is_legendary")
    @HasNoOrder
    protected Boolean is_legendary;


}
