package fr.univlille.s3.S302.model;

import java.util.Map;

/**
 * Interface représentant une donnée
 */
public interface Data {

    /**
     * @return les attributs de la donnée
     */
    Map<String, Number> getattributes();

    /**
     * @return la catégorie de la donnée
     */
    String getCategory();

    /**
     * @param category la nouvelle catégorie de la donnée
     */
    void setCategory(String category);

    /**
     * @param attributs les attributs de la donnée
     * @return une nouvelle donnée
     */
    Data createObject(Map<String,Number> attributs);
}
