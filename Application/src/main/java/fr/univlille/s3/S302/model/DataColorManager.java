package fr.univlille.s3.S302.model;

import java.util.HashMap;
import java.util.Map;

/**
 * Classe permettant de gérer les couleurs des données
 */
public class DataColorManager {

    private final Map<String, String> colorMap;
    private static int idxColor = 0;

    /**
     * Constructeur de la classe DataColorManager
     */
    public DataColorManager() {
        this.colorMap = new HashMap<>();
    }

    /**
     * Méthode permettant de récupérer la couleur suivante
     * @param nbCategories le nombre de catégories
     * @return la couleur suivante
     */
    public String nextColor(int nbCategories) {
        if (colorMap.size() != nbCategories) {
            createColor(nbCategories);
        }
        String color = colorMap.get("Color" + idxColor);
        idxColor = (idxColor + 1) % nbCategories;
        return color;
    }

    /**
     * Méthode permettant de créer les couleurs
     * @param nbCategories le nombre de catégories
     */
    protected void createColor(int nbCategories) {
        colorMap.clear();
        for (int i = 0; i < nbCategories; i++) {
            colorMap.put("Color" + i, "rgb(" + (int) (Math.random() * 255) + "," + (int) (Math.random() * 255) + "," + (int) (Math.random() * 255) + ")");
        }
    }
}