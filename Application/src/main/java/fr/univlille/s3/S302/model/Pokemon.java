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
    protected int attack;

    @CsvBindByName(column = "base_egg_steps")
    protected int base_egg_steps;

    @CsvBindByName(column = "capture_rate")
    protected double capture_rate;

    @CsvBindByName(column = "defense")
    protected int defense;

    @CsvBindByName(column = "experience_growth")
    protected int experience_growth;

    @CsvBindByName(column = "hp")
    protected int hp;

    @CsvBindByName(column = "sp_attack")
    protected int sp_attack;

    @CsvBindByName(column = "sp_defense")
    protected int sp_defense;

    @CsvBindByName(column = "type1")
    protected String type1;

    @CsvBindByName(column = "type2")
    protected String type2;

    @CsvBindByName(column = "speed")
    protected double speed;

    @CsvBindByName(column = "is_legendary")
    protected boolean is_legendary;



}
