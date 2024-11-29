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

        DataManager<Data> dataManager = DataManager.getInstance();

        for (String key : attributs1.keySet()) {
            double value1 = attributs1.get(key).doubleValue();
            double value2 = attributs2.get(key).doubleValue();

            double min = dataManager.getMin(key).doubleValue();
            double max = dataManager.getMax(key).doubleValue();

            double diff = max - min;

            if (diff == 0) {
                diff = 1;
            }

            double normalizedValue1 = (value1 - min) / diff;
            double normalizedValue2 = (value2 - min) / diff;

            somme += Math.abs(normalizedValue1 - normalizedValue2);
        }
        return somme;
    }
}
