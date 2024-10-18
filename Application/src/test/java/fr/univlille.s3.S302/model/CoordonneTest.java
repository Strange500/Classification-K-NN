package fr.univlille.s3.S302.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CoordonneTest {

    Coordonnee c;

    @BeforeEach
    public void setup() {
        c = new Coordonnee(0, 4);
    }

    @Test
    public void initialiseTest() {
        assertEquals(0, c.getX());
        assertEquals(4, c.getY());
    }

    @Test
    public void toStringTest() {
        assertEquals("Coordonnee{x=0.0, y=4.0}", c.toString());
    }
}
