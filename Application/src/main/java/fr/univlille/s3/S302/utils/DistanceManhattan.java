package fr.univlille.s3.S302.utils;

import java.util.Map;

import com.google.common.collect.Maps;
import fr.univlille.s3.S302.model.Data;

/**
 * Classe implémentant l'interface Distance, permettant de calculer la distance de Manhattan entre deux données.
 */
public class DistanceManhattan implements Distance {

    @Override
    public double distance(Data j1, Data j2) {
        Map<String, Number> attributs1 = j1.getAttributes();
        Map<String, Number> attributs2 = j2.getAttributes();
        Map<String, Number> attributsIntersection = Maps.filterKeys(attributs1, attributs2::containsKey);
        double somme = 0;
        for (String key : attributsIntersection.keySet()) {
            double diff = attributs1.get(key).doubleValue() - attributs2.get(key).doubleValue();
            somme += Math.abs(diff);
        }
        return somme;
    }
}
