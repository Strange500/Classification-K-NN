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

    public static double distance(Data d1, Data d2, Distance distance) {
        Pair<Data, Data> pair = computeAttributes(d1, d2);
        return distance.distance(pair.getKey(), pair.getValue());
    }


    /**
     * Cette fonction de prétraitement permet de comparer les attributs de deux données
     * elle traite notamment les attributs qui n'ont pas d'ordre
     * @param d1
     * @param d2
     * @return
     */
    private static Pair<Data, Data> computeAttributes(Data d1, Data d2) {
        FakeData fakeData1 = new FakeData(d1.getAttributes(), d1.categoryField);
        FakeData fakeData2 = new FakeData(d2.getAttributes(), d2.categoryField);
        // on ne compare pas la catégorie
        fakeData2.attributes.remove(d2.categoryField);
        fakeData1.attributes.remove(d1.categoryField);

        // on retire les atttributs qui ne sont pas dans les deux données

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
     * Cette fonction permet de récupérer la valeur numérique d'un attribut a partir de sa valeur originale
     * @param attribute
     * @param value
     * @return
     */
    public static double valueOf(String attribute, String value) {
        if (attributesMap.containsKey(attribute) && attributesMap.get(attribute).contains(value)) {
            return attributesMap.get(attribute).indexOf(value);
        } else {
            return Double.parseDouble(value);
        }
    }

    /**
     * Cette fonction permet de vérifier si un attribut est une instance d'une classe
     * @param attribute
     * @param clazz
     * @return
     */
    boolean attributeIsClass(String attribute, Class<?> clazz) {
        if (dataTypes.getOrDefault(attribute, null) == null) {
            throw new NoSuchElementException("Attribute not found");
        }
        return isEqualOrSubclass(dataTypes.get(attribute), clazz);
    }

    /**
     * Cette fonction permet de vérifier si une classe est égale ou une sous-classe d'une autre
     * @param class1
     * @param class2
     * @return
     */
    protected static boolean isEqualOrSubclass(Class<?> class1, Class<?> class2) {
        return class2.isAssignableFrom(class1);
    }


    /**
     * Cette fonction permet de récupérer la valeur originale d'un attribut
     * @param attribute
     * @param value
     * @return
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
     *
     * cette fonction permet de mettre à jour les index des attributs après l'insertion d'une nouvelle valeur
     * Ce traitement est néccessaire car les Data ne mettent pas a jour automatiquement les index de leur attributs
     * @param attribute
     * @param insertedIndex
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
     * Cette fonction permet compléter les données de la Data a partir des attributs de la classe en héritant
     * elle mets aussi a jour la map attributesNumericalValueToAttributesOriginalMap
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
                    attributesNumericalValueToAttributesOriginalMap.get(fields[i].getName()).add(tmp.intValue(), values[i].toString());
                    values[i] = tmp;
                }
                map.put(fields[i].getName(), (Number)values[i]);
                fieldsMap.put(fields[i].getName(), fields[i]);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        this.categoryField = category;
        this.attributes = map;
        this.category = map.get(category).toString();
    };

    /**
     * Cette fonction vérifie la présence de l'annotation HasNoOrder sur un attribut
     * @param attribute l'attribut à vérifier
     * @return true si l'attribut a un ordre, false sinon
     */
    public boolean hasOrder(String attribute) {
        if (fieldsMap.getOrDefault(attribute, null) == null) {
            throw new NoSuchElementException("Attribut : " + attribute + " non trouvé");
        }
        return !fieldsMap.get(attribute).isAnnotationPresent(HasNoOrder.class);
    }

    /**
     * Cette fonction permet de récupérer la valeur numérique d'un attribut
     * Elle mets aussi à jour la liste des valeurs possibles de l'attribut
     * @param field
     * @param value
     * @return
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
     * Cette fonction permet de mettre à jour la map dataTypes qui associe à chaque attribut sa classe
     * @param obj
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
            return category;
        }
        // on retourne la valeur originale de la catégorie
        return getValue(categoryField, Integer.parseInt(category));
    }
    /**
     * @param category la nouvelle catégorie de la donnée
     */
    public void setCategory(Data data) {
        this.category = data.getCategory();
        this.attributes.put(categoryField, data.getAttributes().get(categoryField));
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

