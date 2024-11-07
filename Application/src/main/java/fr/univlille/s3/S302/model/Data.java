package fr.univlille.s3.S302.model;

import fr.univlille.s3.S302.utils.HasOrder;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

public abstract class Data {

    private String category;

    private static Map<String, Map<String, Number>> attributesMap = new HashMap<>();


    public static Data getData(Data obj) {
        // pour le momment l'odre est suppose être celui d'entree
        Field[] fields = obj.getClass().getDeclaredFields();
        Object[] values = new Object[fields.length];
        // store in a map associating the attribute name to its value
        Map<String, Number> map = new HashMap<>();
        for (int i = 0; i < fields.length; i++) {
            fields[i].setAccessible(true);
            try {
                values[i] = fields[i].get(obj);
                if (!(values[i] instanceof Number)) {
                    values[i] = getIntValue(fields[i], values[i]);
                }

                map.put(fields[i].getName(), (Number)values[i]);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return new FakeData(map);
    };

    private static boolean hasOrder(Data obj, String attribute) {
        Field[] fields = obj.getClass().getDeclaredFields();
        for (Field field : fields) {
            if (field.getName().equals(attribute)) {
                return field.isAnnotationPresent(HasOrder.class);
            }
        }
        return false;
    }

    private static Number getIntValue(Field field, Object value) {
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



    public Map<String, Number> getattributes() {
        Data data = getData(this);
        return data.getattributes();
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String toString() {
        Data data = getData(this);
        Map<String, Number> attributes = data.getattributes();
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        for (Map.Entry<String, Number> entry : attributes.entrySet()) {
            sb.append(entry.getKey()).append(": ").append(entry.getValue()).append(", ");
        }
        sb.append("category: ").append(category).append("}");
        return sb.toString();
    }

}
