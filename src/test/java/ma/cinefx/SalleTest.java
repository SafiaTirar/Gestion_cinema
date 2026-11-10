package ma.cinefx;

import ma.cinefx.Model.Salle;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SalleTest {

    @Test
    void salleShouldExposeItsProperties() {
        Salle salle = new Salle("Salle 1", 100);

        assertEquals("Salle 1", salle.getNom());
        assertEquals(100, salle.getCapacite());
    }
}
