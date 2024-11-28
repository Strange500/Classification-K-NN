package fr.univlille.s3.S302.model;

import fr.univlille.s3.S302.utils.DistanceEuclidienne;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;

public class TestData {
    private Data data;
    private Map<String, Number> map;

    @BeforeEach
    public void setUp() {
        map = new HashMap<>();
        map.put("Key1", 1);
        data = new FakeData(map, "");
    }

    @Test
    void distance() {
        map.put("key1", 2);
        FakeData data2 = new FakeData(map, "");
        assertEquals(1.0, Data.distance(data, data2, new DistanceEuclidienne()));
    }

    @Test
    void valueOf() {
        //clé non valide et attribut non valide
        assertEquals(10, Data.valueOf("String", "10"));

        //clé non valide mais attribut valide
        assertEquals(1, Data.valueOf("String", "1"));

        //clé valide mais attribut non valide
        assertEquals(10, Data.valueOf("Key1", "10"));

        //clé valide et attribut valide
        assertEquals(2.0, Data.valueOf("Key1", "1"));
    }

    @Test
    void attributeIsClass() {
        try {
            assertTrue(data.attributeIsClass("Key1", Number.class));
            assertFalse(data.attributeIsClass("Key1", String.class));
            // Exception test
            assertFalse(data.attributeIsClass("Test", Object.class));
        } catch (NoSuchElementException e) {
            System.out.println("Exception caught");
        }
    }

    @Test
    void isEqualOrSubclass() {
        // sous classe
        FakeData fd = new FakeData(null, null);
        assertTrue(Data.isEqualOrSubclass(Data.class, fd.getClass()));

        // classe équivalente
        assertTrue(Data.isEqualOrSubclass(FakeData.class, fd.getClass()));

        // classe sans lien
        assertFalse(Data.isEqualOrSubclass(Data.class, String.class));
    }

    @Test
    void getValue() {
        //attribut non valide
        assertNull(data.getValue("Key", 1));
        //attribut valide
        assertEquals("1", data.getValue("Key1", 1));
        assertEquals("1", data.getValue("Key1", 1.0));
    }

    @Test
    void getAttributes() {
        assertEquals(map, data.getAttributes());
    }

    @Test
    void getCategory() {
        data.setCategoryField("1.0");
        assertEquals("1.0", data.getCategory());
    }

    @Test
    void setCategory() {
        int sizeMap = data.attributes.size();
        data.setCategory("1.0");
        assertEquals("1.0", data.getCategory());
        assertEquals(sizeMap+1, data.attributes.size());
    }

    @Test
    void setCategoryField() {
        data.setCategoryField("key1");
        assertEquals("key1", data.getCategoryField());
    }

    @Test
    void getCategoryField() {
        assertNull(data.getCategoryField());
    }
}
