package fr.univlille.s3.S302.model;

import com.google.common.collect.Maps;
import fr.univlille.s3.S302.utils.Distance;
import fr.univlille.s3.S302.utils.HasNoOrder;
import javafx.util.Pair;

import java.lang.reflect.Field;
import java.util.*;

/**
 * Interface représentant une donnée
 */
public abstract class Data {

    protected String categoryField;
    protected String category;
    protected Map<String, Number> attributes;

    private static final Map<String, List<Object>> attributesMap = new HashMap<>();
    private static final Map<String, List<Object>> attributesNumericalValueToAttributesOriginalMap = new HashMap<>();
    protected static final Map<String, Class<?>> dataTypes = new HashMap<>();
    protected static final Map<String, Field> fieldsMap = new HashMap<>();

    /**
     * @param distance la distance à utiliser
     * @return la distance entre deux données
     */
    public static double distance(Data d1, Data d2, Distance distance) {
        Pair<Data, Data> pair = computeAttributes(d1, d2);
        return distance.distance(pair.getKey(), pair.getValue());
    }

    /**
     * Cette fonction permet d'ajuster les valeurs des attributs n'ayant pas d'ordre entre eux
     * Les deux données doivent avoir les mêmes attributs.
     */
    private static Pair<Data, Data> computeAttributes(Data d1, Data d2) {
        FakeData fakeData1 = new FakeData(d1.getAttributes(), d1.categoryField);
        FakeData fakeData2 = new FakeData(d2.getAttributes(), d2.categoryField);
        // on ne compare pas la catégorie
        fakeData2.attributes.remove(d2.categoryField);
        fakeData1.attributes.remove(d1.categoryField);

        // on retire les attributs qui ne sont pas dans les deux données

        fakeData1.attributes = Maps.filterKeys(fakeData1.attributes, fakeData2.attributes::containsKey);
        fakeData2.attributes = Maps.filterKeys(fakeData2.attributes, fakeData1.attributes::containsKey);
        // on fait en sorte que les attributs qui n'ont pas d'ordre soient soit eqaux (alors leur différence est 0) soit différents (alors leur différence est 0)
        for (String key : fakeData1.getAttributes().keySet()) {
            if (!d1.hasOrder(key)) {
                int diff = fakeData1.getAttributes().get(key).equals(fakeData2.getAttributes().get(key)) ? 0 : 1;
                fakeData1.getAttributes().put(key, 0);
                fakeData2.getAttributes().put(key, diff);
            }
        }
        return new Pair<>(fakeData1, fakeData2);
    }

    /**
     * @return la valeur numérique de l'attribut
     */
    public static double valueOf(String attribute, String value) {
        if (attributesMap.getOrDefault(attribute, null) == null) {
            return Double.parseDouble(value);
        }
        for (int i = 0; i < attributesMap.get(attribute).size(); i++) {
            if (attributesMap.get(attribute).get(i).toString().equals(value)) {
                return i;
            }
        }
        return Double.parseDouble(value);
    }

    /**
     * @return si l'attribut est de la classe clazz
     */
    boolean attributeIsClass(String attribute, Class<?> clazz) {
        if (dataTypes.getOrDefault(attribute, null) == null) {
            throw new NoSuchElementException("Attribute not found");
        }
        return isEqualOrSubclass(dataTypes.get(attribute), clazz);
    }

    /**
     * @return si class1 est égal ou une sous-classe de class2
     */
    protected static boolean isEqualOrSubclass(Class<?> class1, Class<?> class2) {
        return class2.isAssignableFrom(class1);
    }

    /**
     * @return la valeur de l'attribut
     */
    public String getValue(String attribute, Number value) {
        if (attributesNumericalValueToAttributesOriginalMap.getOrDefault(attribute, null) == null || value.intValue() > attributesNumericalValueToAttributesOriginalMap.get(attribute).size()) {
            return null;
        }
        if (attributeIsClass(attribute, Number.class)) {
            return value.toString();
        }
        return attributesNumericalValueToAttributesOriginalMap.get(attribute).get(value.intValue()).toString();
    }

    /**
     * @param insertedIndex
     * Met à jour les index des attributs
     */
    public static void updateAttributesIndexes(String attribute, int insertedIndex) {
        List<? extends Data> ls = DataManager.getInstance().getDataList();
        for (Data d : ls) {
            if (d.getAttributes().containsKey(attribute)) {
                Number value = d.getAttributes().get(attribute);
                if (value.intValue() >= insertedIndex) {
                    d.getAttributes().put(attribute, value.intValue() + 1);
                }
            }
        }
    }

    /**
     * Crée les données
     */
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
                    Number tmp  = getIntValue(fields[i], values[i]);
                    attributesNumericalValueToAttributesOriginalMap.putIfAbsent(fields[i].getName(), new ArrayList<>(){{
                        add(0, "Unknown");
                    }});
                    if (!attributesNumericalValueToAttributesOriginalMap.get(fields[i].getName()).contains(values[i].toString())) {
                        attributesNumericalValueToAttributesOriginalMap.get(fields[i].getName()).add(tmp.intValue(), values[i].toString());
                    }
                    values[i] = tmp;
                }
                map.put(fields[i].getName(), (Number)values[i]);
                fieldsMap.put(fields[i].getName(), fields[i]);
            } catch (IllegalAccessException e) {
                System.err.println("Erreur lors de la récupération de la valeur de l'attribut : " + fields[i].getName());
            }
        }
        this.categoryField = category;
        this.attributes = map;
        this.category = map.get(category).toString();
    }

    /**
     * @return si l'attribut a un ordre
     */
    public boolean hasOrder(String attribute) {
        if (fieldsMap.getOrDefault(attribute, null) == null) {
            throw new NoSuchElementException("Attribut : " + attribute + " non trouvé");
        }
        return !fieldsMap.get(attribute).isAnnotationPresent(HasNoOrder.class);
    }

    /**
     * @return la valeur numérique de l'attribut
     */
    private static Number getIntValue(Field field, Object value) {
        if (attributesMap.getOrDefault(field.getName(), null) == null) {
            List<Object> map = new ArrayList<>();
            map.add( 0,"Unknown");
            attributesMap.put(field.getName(), map);
        }

        List<Object> ls = attributesMap.get(field.getName());
        if (ls.contains(value)) {
            return ls.indexOf(value);
        } else if (value instanceof Comparable){
            // skip the first
            int cpt = 1;
            while (cpt < ls.size() && ((Comparable) ls.get(cpt)).compareTo(value) < 0) {
                cpt++;
            }
            ls.add(cpt, value);
            updateAttributesIndexes(field.getName(), cpt);
            return cpt;

        } else {
            int max = ls.size();
            ls.add( max, value.toString());
            //setMax(ls, max + 1);
            attributesMap.get(field.getName()).add( max , value.toString());
            return max ;
        }
    }

    /**
     * @param obj
     * Met à jour les types des données
     */
    static void updateDataTypes(Data obj) {
        dataTypes.clear();
        Field[] fields = obj.getClass().getDeclaredFields();
        for (Field field : fields) {
            dataTypes.put(field.getName(), field.getType());
        }
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
        if (attributeIsClass(categoryField, Number.class)) {
            return attributes.get(categoryField).toString();
        }
        return getValue(categoryField, attributes.get(categoryField));
    }

    
    /**
     * @param category la nouvelle catégorie de la donnée
     */
    public void setCategory(String category) {
        this.category = category;
        this.attributes.put(categoryField, getIntValue(fieldsMap.get(categoryField), category));
    }

    /**
     * @param categoryField la nouvelle catégorie de la donnée
     */
    public void setCategoryField(String categoryField) {
        if (!attributes.containsKey(categoryField)) {
            System.err.println("The category field does not exist in the attributes");
        }
        this.categoryField = categoryField;
        this.category = getCategory();
    }

    /**
     * @return le nom de la catégorie
     */
    public String getCategoryField() {
        return categoryField;
    }

    @Override
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Data data = (Data) o;
        return Objects.equals(categoryField, data.categoryField) && Objects.equals(category, data.category) && Objects.equals(attributes, data.attributes);
    }

    @Override
    public int hashCode() {
        return Objects.hash(categoryField, category, attributes);
    }
}

