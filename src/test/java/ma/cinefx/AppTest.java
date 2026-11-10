package ma.cinefx;

import ma.cinefx.Model.Film;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class AppTest {

    @Test
    void filmShouldExposeItsProperties() {
        Film film = new Film("Avengers", "Action", 150);

        assertEquals("Avengers", film.getTitre());
        assertEquals("Action", film.getGenre());
        assertEquals(150, film.getDuree());
    }
}
