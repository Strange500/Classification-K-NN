package fr.univlille.s3.S302.utils;

import fr.univlille.s3.S302.model.Data;
import fr.univlille.s3.S302.model.DataLoader;
import fr.univlille.s3.S302.model.DataManager;
import fr.univlille.s3.S302.model.data.FakeData;
import fr.univlille.s3.S302.model.data.Iris;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestDistanceEuclidienne {
     @Test
    public void testDistanceEuclidienne() {
          DataLoader.registerHeader(Iris.class, "\"sepal.length\",\"sepal.width\",\"petal.length\",\"petal.width\",\"variety\"");
         DataManager dm = DataManager.getInstance();
         DistanceEuclidienne distance = new DistanceEuclidienne();
         Map<String, Number> map = new HashMap<>();
         map.put("petalWidth", 6);
         Data d1 = new FakeData(map, "sepalLength");
         map.clear();
         map.put("petalWidth", 5);
         Data d2 = new FakeData(map, "sepalLength");

         assertEquals(1, Data.distance(d1, d2, distance));
     }
}
