package fr.univlille.s3.S302.model;

import fr.univlille.s3.S302.utils.HasNoOrder;

import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

public class FakeData extends Data{


    public FakeData(Map<String, Number> attr, String categorieField) {
        this.attributes = new HashMap<>(attr);
        this.categoryField = categorieField;
        this.attributes.remove(categorieField);
    }

    @Override
    public String getCategory() {
        if (attributes.containsKey(categoryField) && attributes.get(categoryField) != null) {
            return attributes.get(categoryField).toString();
        }
        return "Unknown";
    }


    @Override
    boolean attributeIsClass(String attribute, Class<?> clazz) {

        for (String key : attributes.keySet()) {
            if (key.equals(attribute) && dataTypes.containsKey(key)) {
                return isEqualOrSubclass(dataTypes.get(key), clazz);
            }
        }
        throw new NoSuchElementException("Attribute not found");
    }

    @Override
    public boolean hasOrder(String attribute) {
        for (String key : attributes.keySet()) {
            if (key.equals(attribute) && dataTypes.containsKey(key)) {
                return !dataTypes.get(key).isAnnotationPresent(HasNoOrder.class);
            }
        }
        throw new NoSuchElementException("Attribut : " + attribute + " non trouvé");
    }

    @Override
    public String toString() {
        return this.attributes.toString();
    }

}
