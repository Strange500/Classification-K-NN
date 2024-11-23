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
    private void createColor(int nbCategories) {
        colorMap.clear();
        int nbColor = 0;
        while (nbColor <= nbCategories || nbColor % 3 != 0) {
            nbColor++;
        }
        int step, r = 0, g = 0, b = 0;
        for (int i = 0; i < nbColor; i++) {
            switch (i % 3) {
                case 0:
                    step = i / 3;
                    r = 255 - step * 255 / (nbColor / 3);
                    g = step * 255 / (nbColor / 3);
                    b = 0;
                    break;
                case 1:
                    step = i / 3;
                    r = 0;
                    g = 255 - step * 255 / (nbColor / 3);
                    b = step * 255 / (nbColor / 3);
                    break;
                case 2:
                    step = i / 3;
                    r = step * 255 / (nbColor / 3);
                    g = 0;
                    b = 255 - step * 255 / (nbColor / 3);
                    break;
            }
            colorMap.put("Color" + i, "rgb(" + r + "," + g + "," + b + ")");
        }
    }
}