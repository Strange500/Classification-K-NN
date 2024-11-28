package fr.univlille.s3.S302.utils;

import java.util.Map;
import fr.univlille.s3.S302.model.Data;
import fr.univlille.s3.S302.model.DataManager;

public class DistanceManhattanNormalisee implements Distance {
    @Override
    public double distance(Data j1, Data j2) {
        Map<String, Number> attributs1 = j1.getAttributes();
        Map<String, Number> attributs2 = j2.getAttributes();
        double somme = 0;
        double facteurNormalisation = 0;

        for (Data data : DataManager.getInstance().getDataList()) {
            for (Number value : data.getAttributes().values()) {
                facteurNormalisation += Math.abs(value.doubleValue());
            }
        }

        for (String key : attributs1.keySet()) {
            double diff = Math.abs(attributs1.get(key).doubleValue() - attributs2.get(key).doubleValue());
            somme += diff;
        }

        return somme / facteurNormalisation;
    }
}
