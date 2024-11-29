package fr.univlille.s3.S302.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class DataColorManagerTest {

    @Test
    void nextColor() {
        DataColorManager manager = new DataColorManager();
        assertEquals(0, manager.getColorMap().size());
        manager.nextColor(5);
        assertEquals(5, manager.getColorMap().size());
    }
}
