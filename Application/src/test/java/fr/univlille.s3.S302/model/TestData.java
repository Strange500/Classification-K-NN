package fr.univlille.s3.S302.model;

import fr.univlille.s3.S302.model.data.FakeData;
import fr.univlille.s3.S302.model.data.Iris;
import fr.univlille.s3.S302.utils.DistanceEuclidienne;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

import static fr.univlille.s3.S302.model.DataManager.PATH;
import static org.junit.jupiter.api.Assertions.*;

public class TestData {
    private Data data;
    private Map<String, Number> map;
    private DataManager<Data> dm;

    private static final String KEY = "sepalLength";

    @BeforeEach
    public void setUp() {
        DataLoader.registerHeader(Iris.class,
                "\"sepal.length\",\"sepal.width\",\"petal.length\",\"petal.width\",\"variety\"");
        dm = DataManager.getInstance();
        dm.reset();
        try {
            dm.loadData(PATH);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        dm.changeCategoryField(KEY);
        map = new HashMap<>();
        map.put(KEY, 1);
        data = new FakeData(map, "");
    }

    @Test
    void distance() {
        map.put(KEY, 2);
        FakeData data2 = new FakeData(map, "");
        assertEquals(1.0, Data.distance(data, data2, new DistanceEuclidienne()));
    }

    @Test
    void valueOf() {
        // clé non valide et attribut non valide
        assertEquals(10, Data.valueOf("String", "10"));

        // clé non valide mais attribut valide
        assertEquals(1, Data.valueOf("String", "1"));

        // clé valide mais attribut non valide
        assertEquals(10, Data.valueOf(KEY, "10"));

        // clé valide et attribut valide
        assertEquals(1.0, Data.valueOf(KEY, "1"));
    }

    @Test
    void attributeIsClass() {
        try {
            assertTrue(data.attributeIsClass(KEY, Number.class));
            assertFalse(data.attributeIsClass(KEY, String.class));
            // Exception test
            assertFalse(data.attributeIsClass("Test", Object.class));
        } catch (NoSuchElementException e) {
            System.out.println("Exception caught");
        }
    }

    @Test
    void isEqualOrSubclass() {
        // sous classe
        FakeData fd = new FakeData(new HashMap<>(map), KEY);
        assertTrue(Data.isEqualOrSubclass(fd.getClass(), Data.class));

        // classe équivalente
        assertTrue(Data.isEqualOrSubclass(FakeData.class, fd.getClass()));

        // classe sans lien
        assertFalse(Data.isEqualOrSubclass(Data.class, String.class));
    }

    @Test
    void getValue() {
        // attribut non valide
        assertThrows(NoSuchElementException.class, () -> data.getValue("String", 1));
        // attribut valide
        dm.changeCategoryField(KEY);
        data.setCategoryField(KEY);
        assertEquals("1", data.getValue(KEY, 1));
    }

    @Test
    void getAttributes() {
        assertNotNull(data.getAttributes());
        assertEquals(1, data.getAttributes().get(KEY));
    }

    @Test
    void getCategoryField() {
        data.setCategoryField(KEY);
        assertEquals(KEY, data.getCategoryField());
    }

    @Test
    void setCategory() {
        data.setCategoryField(KEY);
        data.setCategory("1.0");
        assertEquals("1.0", data.getCategory());
    }

    @Test
    void setCategoryField() {
        data.setCategoryField(KEY);
        assertEquals(KEY, data.getCategoryField());
    }

}
