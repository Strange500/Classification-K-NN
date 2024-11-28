package fr.univlille.s3.S302.utils;

import java.util.Map;
import fr.univlille.s3.S302.model.Data;
import fr.univlille.s3.S302.model.DataManager;

public class DistanceEuclidienneNormalisee implements Distance {

    @Override
    public double distance(Data j1, Data j2) {
        Map<String, Number> attributs1 = j1.getAttributes();
        Map<String, Number> attributs2 = j2.getAttributes();
        double somme = 0;
        double somme1 = 0;
        double somme2 = 0;

        double facteurNormalisation = 0;
        for (Data data : DataManager.getInstance().getDataList()) {
            for (Number value : data.getAttributes().values()) {
                facteurNormalisation += value.doubleValue() * value.doubleValue();
            }
        }
        facteurNormalisation = Math.sqrt(facteurNormalisation);

        for (String key : attributs1.keySet()) {
            double diff = attributs1.get(key).doubleValue() - attributs2.get(key).doubleValue();
            somme += diff * diff;
            somme1 += attributs1.get(key).doubleValue() * attributs1.get(key).doubleValue();
            somme2 += attributs2.get(key).doubleValue() * attributs2.get(key).doubleValue();
        }

        return Math.sqrt(somme) / facteurNormalisation;
    }
}
