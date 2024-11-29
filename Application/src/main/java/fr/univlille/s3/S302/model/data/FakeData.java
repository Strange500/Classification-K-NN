package fr.univlille.s3.S302.model.data;

import fr.univlille.s3.S302.model.Data;
import fr.univlille.s3.S302.utils.HasNoOrder;

import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

/**
 * Classe qui permet de créer des données à partir d'une Map d'attributs et d'un
 * champ de catégorie.
 */
public class FakeData extends Data {

    /**
     * Constructeur de la classe FakeData
     * 
     * @param attr           Map d'attributs
     * @param categorieField Champ de catégorie
     * @throws IllegalArgumentException si le champ de catégorie n'est pas une clé
     *                                  valide dans attr
     */
    public FakeData(Map<String, Number> attr, String categorieField) {
        this.attributes = new HashMap<>(attr);
        this.categoryField = categorieField;
        this.attributes.put(categorieField, null);
    }

    @Override
    public String getCategory() {
        if (attributes.containsKey(categoryField) && attributes.get(categoryField) != null) {
            return super.getCategory();
        }
        return "Unknown";
    }

    /**
     * Vérifie si un attribut correspond à une classe donnée ou à l'une de ses
     * sous-classes.
     * 
     * @param attribute Nom de l'attribut.
     * @param clazz     Classe cible à vérifier.
     * @return true si l'attribut correspond à la classe ou à l'une de ses
     *         sous-classes, false sinon.
     * @throws NoSuchElementException si l'attribut n'est pas trouvé.
     */
    @Override
    protected boolean attributeIsClass(String attribute, Class<?> clazz) {
        if (!attributes.containsKey(attribute)) {
            throw new NoSuchElementException("Attribute not found");
        }
        return dataTypes.containsKey(attribute) && isEqualOrSubclass(dataTypes.get(attribute), clazz);
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
