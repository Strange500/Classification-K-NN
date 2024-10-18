package fr.univlille.s3.S302.model;

import java.util.Map;

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

    Data createObject(Map<String,Number> map);
}
