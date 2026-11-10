package ma.cinefx.Service;

import ma.cinefx.Dao.FilmDAO;
import ma.cinefx.Exception.EntityAlreadyExistsException;
import ma.cinefx.Exception.EntityNotFoundException;
import ma.cinefx.Exception.EntityPersistenceException;
import ma.cinefx.Model.Film;

import java.util.List;

public class FilmService {

    private final FilmDAO filmDAO;

    public FilmService() {
        this.filmDAO = new FilmDAO();
    }

    // Lister tous les films
    public List<Film> listerTousLesFilms() {
        try {
            return filmDAO.getAllFilms();
        } catch (EntityPersistenceException e) {
            System.out.println("Erreur technique lors de la récupération des films : " + e.getMessage());
            return List.of();
        }
    }

    // Créer un film
    public void creerFilm(Film film) {
        validerFilm(film);

        try {
            filmDAO.addFilm(film);
            System.out.println("Film créé avec succès !");
        } catch (EntityAlreadyExistsException e) {
            throw new IllegalArgumentException("Un film avec ce titre existe déjà.");
        } catch (EntityPersistenceException e) {
            System.out.println("Erreur technique lors de la création du film : " + e.getMessage());
            throw e;
        }
    }

    // Modifier un film
    public void modifierFilm(Film film) {
        if (film == null || film.getId() == null || film.getId() <= 0) {
            throw new IllegalArgumentException("Film ou ID invalide.");
        }
        validerFilm(film);

        try {
            filmDAO.updateFilm(film);
            System.out.println("Film modifié avec succès !");
        } catch (EntityNotFoundException e) {
            throw new IllegalArgumentException("Film introuvable : " + e.getMessage());
        } catch (EntityPersistenceException e) {
            System.out.println("Erreur technique lors de la modification du film : " + e.getMessage());
            throw e;
        }
    }

    // Supprimer un film
    public void supprimerFilm(Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("ID invalide.");
        }

        try {
            filmDAO.deleteFilm(id);
            System.out.println("Film supprimé avec succès !");
        } catch (EntityNotFoundException e) {
            throw new IllegalArgumentException("Film introuvable : " + e.getMessage());
        } catch (EntityPersistenceException e) {
            System.out.println("Erreur technique lors de la suppression du film : " + e.getMessage());
            throw e;
        }
    }

    // Validation métier de base
    private void validerFilm(Film film) {
        if (film == null
                || film.getTitre() == null || film.getTitre().isBlank()
                || film.getGenre() == null || film.getGenre().isBlank()) {
            throw new IllegalArgumentException("Titre et genre sont obligatoires.");
        }
        if (film.getDuree() <= 0) {
            throw new IllegalArgumentException("La durée du film doit être > 0.");
        }
    }
}
