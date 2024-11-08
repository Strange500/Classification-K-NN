package fr.univlille.s3.S302.model;

import fr.univlille.s3.S302.utils.HasOrder;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

/**
 * Interface représentant une donnée
 */
public abstract class Data {

    String categoryField;
    private String category;
    protected Map<String, Number> attributes;

    private static final Map<String, Map<String, Number>> attributesMap = new HashMap<>();


    public void makeData() {
        // pour le momment l'odre est suppose être celui d'entree
        Field[] fields = this.getClass().getDeclaredFields();
        Object[] values = new Object[fields.length];
        // store in a map associating the attribute name to its value
        Map<String, Number> map = new HashMap<>();
        String category = fields[0].getName();
        for (int i = 0; i < fields.length; i++) {
            fields[i].setAccessible(true);
            try {
                values[i] = fields[i].get(this);
                if (!(values[i] instanceof Number)) {
                    values[i] = getIntValue(fields[i], values[i]);
                }
                map.put(fields[i].getName(), (Number)values[i]);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        this.categoryField = category;
        this.attributes = map;
        this.category = map.get(category).toString();
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


    /**
     * @return les attributs de la donnée
     */
    public Map<String, Number> getAttributes() {
        return this.attributes;
    }
    /**
     * @return la catégorie de la donnée
     */
    public String getCategory() {
        return this.attributes.get(categoryField).toString();
    }
    /**
     * @param category la nouvelle catégorie de la donnée
     */
    public void setCategory(String category) {
        this.attributes.put(categoryField, Integer.parseInt(category));
    }

    public void setCategoryField(String categoryField) {
        if (!attributes.containsKey(categoryField)) {
            System.err.println("The category field does not exist in the attributes");
        }
        this.categoryField = categoryField;
    }

    public String getCategoryField() {
        return categoryField;
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

