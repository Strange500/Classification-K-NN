package fr.univlille.s3.S302.utils;


import java.util.Map;
import fr.univlille.s3.S302.model.Data;

/**
 * Classe implémentant l'interface Distance, permettant de calculer la distance euclidienne entre deux données.
 */
public class DistanceEuclidienne implements Distance {
    
    @Override
    public double distance(Data j1, Data j2) {
        Map<String, Number> attributs1 = j1.getAttributes();
        Map<String, Number> attributs2 = j2.getAttributes();
        double somme = 0;
        for (String key : attributs1.keySet()) {
            double diff = attributs1.get(key).doubleValue() - attributs2.get(key).doubleValue();
            somme += diff * diff;
        }
        return Math.sqrt(somme);
    }
}
