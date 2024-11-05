package fr.univlille.s3.S302.model;


import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

public interface AbstractData {



    public default Data getData(AbstractData obj) {
        // get all class implementing data
        Class[] classes = Data.class.getClasses();

        //get all Attributes and values of obj using reflection
        Field[] fields = obj.getClass().getDeclaredFields();
        Object[] values = new Object[fields.length];
        // store in a map associating the attribute name to its value
        Map<String, Number> map = new HashMap<>();
        for (int i = 0; i < fields.length; i++) {
            fields[i].setAccessible(true);
            try {
                values[i] = fields[i].get(obj);
                // traitement temporaire
                if (!(values[i] instanceof Number)) {
                    continue;
                }
                map.put(fields[i].getName(), (Number)values[i]);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return new FakeData(map);
    };


}
