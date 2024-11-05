package fr.univlille.s3.S302.model;

import java.util.Map;

/**
 * Classe permettant de créer des données fictives
 */
public class FakeData implements Data{

    private final Map<String, Number> attributes;
    private String category = "Unknown";
    public FakeData(Map<String, Number> attr) {
        attributes = attr;

    }

    /**
     * @return les attributs de la donnée
     */
    @Override
    public Map<String, Number> getattributes() {
        return attributes;
    }

    /**
     * @return la catégorie de la donnée
     */
    @Override
    public String getCategory() {
        return category;
    }

    /**
     * @param category la nouvelle catégorie de la donnée
     */
    @Override
    public void setCategory(String category) {
        this.category = category;
    }

    /**
     * @param attributs les attributs de la donnée
     * @return une nouvelle donnée
     */
    @Override
    public Data createObject(Map<String, Number> attributs) {
        return null;
    }
}
