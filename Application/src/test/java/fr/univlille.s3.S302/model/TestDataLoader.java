package fr.univlille.s3.S302.model;

import fr.univlille.s3.S302.model.data.FakeData;
import org.junit.jupiter.api.Test;

import java.io.FileNotFoundException;
import java.io.StringReader;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class TestDataLoader {
    @Test
    void testChargerNullFilename() {
        assertThrows(FileNotFoundException.class, () -> DataLoader.charger(null));
    }

    @Test
    void testChargerNonExistentFile() {
        assertThrows(RuntimeException.class, () -> DataLoader.charger("src/main/resources/fr/univlille/s3/S302/view/iris.csv"));
    }

    @Test
    void testRegisterHeader() {
        DataLoader.registerHeader(FakeData.class, "\"sepal.length\",\"sepal.width\",\"petal.length\",\"petal.width\",\"variety\"");
        Class<? extends Data> clazz = DataLoader.getClassFromHeader(new StringReader("\"sepal.length\",\"sepal.width\",\"petal.length\",\"petal.width\",\"variety\""));
        assertEquals(FakeData.class, clazz);
    }
}