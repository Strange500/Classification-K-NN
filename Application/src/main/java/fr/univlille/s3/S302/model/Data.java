package fr.univlille.s3.S302.model;

import com.google.common.collect.Maps;
import fr.univlille.s3.S302.utils.Distance;
import fr.univlille.s3.S302.utils.HasNoOrder;
import fr.univlille.s3.S302.model.data.FakeData;
import javafx.util.Pair;

import java.lang.reflect.Field;
import java.util.*;

/**
 * Classe abstraite représentant un point de données.
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
     * Calcule la distance entre deux points de données en utilisant la métrique de
     * distance spécifiée.
     *
     * @param d1       le premier point de données
     * @param d2       le deuxième point de données
     * @param distance la métrique de distance à utiliser
     * @return la distance entre les deux points de données
     */
    public static double distance(Data d1, Data d2, Distance distance) {
        Pair<Data, Data> pair = computeAttributes(d1, d2);
        return distance.distance(pair.getKey(), pair.getValue());
    }

    /**
     * Ajuste les valeurs des attributs pour les attributs qui n'ont pas d'ordre.
     * Les deux points de données doivent avoir les mêmes attributs.
     */
    private static Pair<Data, Data> computeAttributes(Data d1, Data d2) {
        FakeData fakeData1 = new FakeData(d1.getAttributes(), d1.categoryField);
        FakeData fakeData2 = new FakeData(d2.getAttributes(), d2.categoryField);

        fakeData2.attributes.remove(d2.categoryField);
        fakeData1.attributes.remove(d1.categoryField);

        fakeData1.attributes = Maps.filterKeys(fakeData1.attributes, fakeData2.attributes::containsKey);
        fakeData2.attributes = Maps.filterKeys(fakeData2.attributes, fakeData1.attributes::containsKey);

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
     * Convertit la valeur de l'attribut en une valeur numérique.
     *
     * @param attribute le nom de l'attribut
     * @param value     la valeur de l'attribut
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
        throw new NumberFormatException("Valeur non trouvée");
    }

    /**
     * Vérifie si l'attribut est de la classe spécifiée.
     *
     * @param attribute le nom de l'attribut
     * @param clazz     la classe à vérifier
     * @return true si l'attribut est de la classe spécifiée, false sinon
     */
    protected boolean attributeIsClass(String attribute, Class<?> clazz) {
        if (attributes.containsKey(attribute) && dataTypes.containsKey(attribute)) {
            return isEqualOrSubclass(dataTypes.get(attribute), clazz);
        }
        throw new NoSuchElementException("Attribute not found");
    }

    /**
     * Vérifie si class1 est égale ou une sous-classe de class2.
     *
     * @param class1 la première classe
     * @param class2 la deuxième classe
     * @return true si class1 est égale ou une sous-classe de class2, false sinon
     */
    protected static boolean isEqualOrSubclass(Class<?> class1, Class<?> class2) {
        return class2.isAssignableFrom(class1);
    }

    /**
     * Obtient la valeur originale de l'attribut à partir de sa valeur numérique.
     *
     * @param attribute le nom de l'attribut
     * @param value     la valeur numérique
     * @return la valeur originale de l'attribut
     */
    public String getValue(String attribute, Number value) {
        if (!attributeIsClass(attribute, Number.class)
                && (attributesNumericalValueToAttributesOriginalMap.getOrDefault(attribute, null) == null
                        || value.intValue() > attributesNumericalValueToAttributesOriginalMap.get(attribute).size())) {
            return null;
        }
        if (attributeIsClass(attribute, Number.class)) {
            return value.toString();
        }
        return attributesNumericalValueToAttributesOriginalMap.get(attribute).get(value.intValue()).toString();
    }

    /**
     * Met à jour les index des attributs.
     *
     * @param attribute     le nom de l'attribut
     * @param insertedIndex l'index à insérer
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
     * Initialise les attributs du point de données.
     */
    public void makeData() {
        Field[] fields = this.getClass().getDeclaredFields();
        Object[] values = new Object[fields.length];
        Map<String, Number> attrMap = new HashMap<>();
        String category = fields[0].getName(); // default category can be changed later
        for (int i = 0; i < fields.length; i++) {
            fields[i].setAccessible(true);
            try {
                values[i] = fields[i].get(this);
                if (!(values[i] instanceof Number)) {
                    Number tmp = registerValue(fields, i, values);
                    values[i] = tmp;
                }
                attrMap.put(fields[i].getName(), (Number) values[i]);
                fieldsMap.put(fields[i].getName(), fields[i]); // on garde une référence vers les fields pour accelerer
                                                               // les accès
            } catch (IllegalAccessException e) {
                System.err
                        .println("Erreur lors de la récupération de la valeur de l'attribut : " + fields[i].getName());
            }
        }
        this.categoryField = category;
        this.attributes = attrMap;
        this.category = attrMap.get(category).toString();
    }

    private static Number registerValue(Field[] fields, int i, Object[] values) {
        Number tmp = getIntValue(fields[i], values[i]);
        attributesNumericalValueToAttributesOriginalMap.putIfAbsent(fields[i].getName(), new ArrayList<>() {
            {
                add(0, "Unknown");
            }
        });
        if (!attributesNumericalValueToAttributesOriginalMap.get(fields[i].getName()).contains(values[i].toString())) {
            attributesNumericalValueToAttributesOriginalMap.get(fields[i].getName()).add(tmp.intValue(),
                    values[i].toString());
        }
        return tmp;
    }

    /**
     * Vérifie si l'attribut a un ordre.
     *
     * @param attribute le nom de l'attribut
     * @return true si l'attribut a un ordre, false sinon
     */
    public boolean hasOrder(String attribute) {
        if (attributes.containsKey(attribute) && dataTypes.containsKey(attribute)) {
            return !dataTypes.get(attribute).isAnnotationPresent(HasNoOrder.class);
        }
        throw new NoSuchElementException("Attribut : " + attribute + " non trouvé");
    }

    /**
     * Convertit la valeur de l'attribut en une valeur numérique.
     *
     * @param field le champ représentant l'attribut
     * @param value la valeur de l'attribut
     * @return la valeur numérique de l'attribut
     */
    private static Number getIntValue(Field field, Object value) {
        initField(field);
        List<Object> ls = attributesMap.get(field.getName());
        if (ls.contains(value)) {
            return ls.indexOf(value);
        } else if (value instanceof Comparable) {
            // skip the first as it is "Unknown"
            return insertValue(field, value, ls);

        } else {
            // not comparable so we append it
            return appendValue(field, value, ls);
        }
    }

    private static int appendValue(Field field, Object value, List<Object> ls) {
        int max = ls.size();
        ls.add(max, value.toString());
        attributesMap.get(field.getName()).add(max, value.toString());
        return max;
    }

    private static int insertValue(Field field, Object value, List<Object> ls) {
        int cpt = 1;
        while (cpt < ls.size() && ((Comparable) ls.get(cpt)).compareTo(value) < 0) {
            cpt++;
        }
        ls.add(cpt, value);
        updateAttributesIndexes(field.getName(), cpt);
        return cpt;
    }

    private static void initField(Field field) {
        if (attributesMap.getOrDefault(field.getName(), null) == null) {
            List<Object> map = new ArrayList<>();
            map.add(0, "Unknown");
            attributesMap.put(field.getName(), map);
        }
    }

    /**
     * Met à jour les types de données des attributs.
     *
     * @param obj l'objet de données
     */
    static void updateDataTypes(Data obj) {
        dataTypes.clear();
        Field[] fields = obj.getClass().getDeclaredFields();
        for (Field field : fields) {
            dataTypes.put(field.getName(), field.getType());
        }
    }

    /**
     * Obtient les attributs du point de données.
     *
     * @return les attributs du point de données
     */
    public Map<String, Number> getAttributes() {
        return this.attributes;
    }

    /**
     * Obtient la catégorie du point de données.
     *
     * @return la catégorie du point de données
     */
    public String getCategory() {
        if (attributeIsClass(categoryField, Number.class)) {
            return attributes.get(categoryField).toString();
        }
        return getValue(categoryField, attributes.get(categoryField));
    }

    /**
     * Définit la catégorie du point de données.
     *
     * @param category la nouvelle catégorie
     */
    public void setCategory(String category) {
        this.category = category;
        System.out.println("categoryField = " + categoryField);
        System.out.println("fieldsMap = " + fieldsMap);
        System.out.println(fieldsMap.get(categoryField));
        this.attributes.put(categoryField, valueOf(fieldsMap.get(categoryField).getName(), category));
    }

    /**
     * Définit le champ de catégorie du point de données.
     *
     * @param categoryField le nouveau champ de catégorie
     */
    public void setCategoryField(String categoryField) {
        if (!attributes.containsKey(categoryField)) {
            System.err.println("Le champ de catégorie n'existe pas dans les attributs");
        }
        this.categoryField = categoryField;
        this.category = getCategory();
    }

    /**
     * Obtient le champ de catégorie du point de données.
     *
     * @return le champ de catégorie
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
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Data data = (Data) o;
        return Objects.equals(categoryField, data.categoryField) && Objects.equals(category, data.category)
                && Objects.equals(attributes, data.attributes);
    }

    @Override
    public int hashCode() {
        return Objects.hash(categoryField, category, attributes);
    }
}
