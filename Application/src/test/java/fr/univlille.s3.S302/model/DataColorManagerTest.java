package fr.univlille.s3.S302.model;

import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class DataColorManagerTest {

    @Test
    void nextColor() {
        DataColorManager manager = new DataColorManager();
        assertEquals(0, manager.getColorMap().size());
        manager.nextColor(10);
        assertTrue(10 <= manager.getColorMap().size()); // on veut au moins 10 couleurs
    }
}
