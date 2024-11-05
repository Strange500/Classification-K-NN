package fr.univlille.s3.S302.model;


import fr.univlille.s3.S302.utils.Countable;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

public interface AbstractData {

    static Map<String, Map<String, Number>> attributesMap = new HashMap<>();


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
                if (!(values[i] instanceof Number)) {
                    if (isCountable(obj, fields[i].getName())) {
                        System.out.println("countable");
                        values[i] = getCountableValue(fields[i], values[i]);
                    }
                    else {
                        System.out.println("not countable");
                        values[i] = 0;
                    }
                }

                map.put(fields[i].getName(), (Number)values[i]);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return new FakeData(map);
    };

    public default boolean isCountable(AbstractData obj, String attribute) {
        Field[] fields = obj.getClass().getDeclaredFields();
        for (Field field : fields) {
            if (field.getName().equals(attribute)) {
                return field.isAnnotationPresent(Countable.class);
            }
        }
        return false;
    }

    public default Number getCountableValue(Field field, Object value) {
        if (attributesMap.containsKey(field.getName())) {
            Map<String, Number> map = attributesMap.get(field.getName());
            if (map.containsKey(value.toString())) {
                return map.get(value.toString()).intValue();
            } else {
                int max = getMax(map);
                map.put(value.toString(), max + 1);
                return max + 1;
            }
        }
        Map<String, Number> map = new HashMap<>();
        map.put(value.toString(), 0);
        attributesMap.put(field.getName(), map);
        return 0;
    }

    private static int getMax(Map<String, Number> map) {
        int max = 0;
        for (Number n : map.values()) {
            if (n.intValue() > max) {
                max = n.intValue();
            }
        }
        return max;
    }


}
