package fr.univlille.s3.S302.model.data;

import java.util.Map;

public class FakeDataValidation extends FakeData {

    private final String effectiveCategory;

    /**
     * Constructeur de la classe FakeData
     *
     * @param attr           Map d'attributs
     * @param categorieField Champ de catégorie
     */
    public FakeDataValidation(Map<String, Number> attr, String categorieField, String effectiveCategory) {
        super(attr, categorieField);
        this.effectiveCategory = effectiveCategory;
        this.attributes.put(categorieField, valueOf(categorieField, effectiveCategory));
    }
}
