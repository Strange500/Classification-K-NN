package fr.univlille.s3.S302.utils;

import fr.univlille.s3.S302.model.Data;
import fr.univlille.s3.S302.model.data.FakeData;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestDistanceManhattan {
    @Test
    public void testDistanceManhattan() {
        DistanceManhattan distance = new DistanceManhattan();
        Map<String, Number> map = new HashMap<>();
        map.put("Longueur", 6);
        Data d1 = new FakeData(map, "Sepale");
        map.clear();
        map.put("Longueur", 5);
        Data d2 = new FakeData(map, "Vertige");

        assertEquals(1, distance.distance(d1, d2));
    }
}
