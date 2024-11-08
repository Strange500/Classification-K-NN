package fr.univlille.s3.S302.model;

import fr.univlille.s3.S302.utils.HasOrder;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

public abstract class Data {

    private String category;
    protected Map<String, Number> attributes;

    private static final Map<String, Map<String, Number>> attributesMap = new HashMap<>();


    public void makeData() {
        // pour le momment l'odre est suppose être celui d'entree
        Field[] fields = this.getClass().getDeclaredFields();
        Object[] values = new Object[fields.length];
        // store in a map associating the attribute name to its value
        Map<String, Number> map = new HashMap<>();
        String category = "";
        for (int i = 0; i < fields.length; i++) {
            fields[i].setAccessible(true);
            try {
                values[i] = fields[i].get(this);
                if (!(values[i] instanceof Number)) {
                    if (!hasOrder(this, fields[i].getName())) {
                        category = values[i].toString();
                    }
                    values[i] = getIntValue(fields[i], values[i]);

                }

                map.put(fields[i].getName(), (Number)values[i]);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        this.category = category;
        this.attributes = map;
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



    public Map<String, Number> getAttributes() {
        makeData();
        return this.attributes;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String toString() {
        makeData();
        Map<String, Number> attributes = this.getAttributes();
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        for (Map.Entry<String, Number> entry : attributes.entrySet()) {
            sb.append(entry.getKey()).append(": ").append(entry.getValue()).append(", ");
        }
        sb.append("category: ").append(category).append("}");
        return sb.toString();
    }

}
