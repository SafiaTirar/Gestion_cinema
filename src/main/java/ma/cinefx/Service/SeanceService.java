package ma.cinefx.Service;
import java.time.LocalDate;
import ma.cinefx.Dao.SeanceDAO;
import ma.cinefx.Exception.EntityAlreadyExistsException;
import ma.cinefx.Exception.EntityNotFoundException;
import ma.cinefx.Exception.EntityPersistenceException;
import ma.cinefx.Model.Seance;

import java.time.LocalDateTime;
import java.util.List;

public class SeanceService {

    private final SeanceDAO seanceDAO;

    public SeanceService() {
        this.seanceDAO = new SeanceDAO();
    }

    // Lister toutes les séances
    public List<Seance> listerToutesLesSeances() {
        try {
            return seanceDAO.getAllSeances();
        } catch (EntityPersistenceException e) {
            System.out.println("Erreur technique lors de la récupération des séances : " + e.getMessage());
            return List.of();
        }
    }
    public List<Seance> searchSeances(String filmTitre, LocalDate date, String genre) {
        return seanceDAO.searchSeances(filmTitre, date, genre);
    }

    // Création avec logique métier
    public void creerSeance(Seance seance) {
        // Validation des champs obligatoires
        validerSeance(seance);

        try {
            // Le DAO va déjà vérifier qu'il n'y a pas de séance
            // dans la même salle à la même date/heure
            seanceDAO.addSeance(seance);
            System.out.println("Séance créée avec succès !");
        } catch (EntityAlreadyExistsException e) {
            // On traduit en IllegalArgumentException pour que le contrôleur
            // puisse afficher un message propre à l'utilisateur
            throw new IllegalArgumentException(
                    "Il existe déjà une séance dans cette salle à cette date/heure."
            );
        } catch (EntityPersistenceException e) {
            System.out.println("Erreur technique lors de la création de la séance : " + e.getMessage());
            throw e;
        }
    }

    // Récupérer une séance par ID
    public Seance recupererSeanceParId(Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("ID invalide");
        }

        try {
            return seanceDAO.getSeanceById(id);
        } catch (EntityNotFoundException e) {
            System.out.println("Séance introuvable : " + e.getMessage());
            return null;
        } catch (EntityPersistenceException e) {
            System.out.println("Erreur technique lors de la récupération de la séance : " + e.getMessage());
            return null;
        }
    }

    // Modification avec logique métier
    public void modifierSeance(Seance seance) {
        if (seance == null) {
            throw new IllegalArgumentException("Séance invalide");
        }
        if (seance.getId() == null || seance.getId() <= 0) {
            throw new IllegalArgumentException("ID de séance manquant pour la modification.");
        }

        // Validation des champs
        validerSeance(seance);

        try {
            // Ici, ton DAO ne re-vérifie pas le conflit de salle/dateHeure,
            // donc si tu veux la même règle à la modif, tu peux la coder
            // soit ici avec getAllSeances(), soit plus tard dans le DAO.
            seanceDAO.updateSeance(seance);
            System.out.println("Séance mise à jour avec succès !");
        } catch (EntityNotFoundException e) {
            System.out.println("Séance introuvable : " + e.getMessage());
            throw e;
        } catch (EntityPersistenceException e) {
            System.out.println("Erreur technique lors de la mise à jour de la séance : " + e.getMessage());
            throw e;
        }
    }

    // Supprimer une séance
    public void supprimerSeance(Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("ID invalide");
        }

        try {
            seanceDAO.deleteSeance(id);
            System.out.println("Séance supprimée avec succès !");
        } catch (EntityNotFoundException e) {
            System.out.println("Séance introuvable : " + e.getMessage());
            throw e;
        } catch (EntityPersistenceException e) {
            System.out.println("Erreur technique lors de la suppression de la séance : " + e.getMessage());
            throw e;
        }
    }

    // Validation commune
    private void validerSeance(Seance seance) {
        if (seance == null
                || seance.getFilm() == null
                || seance.getSalle() == null
                || seance.getTarif() == null
                || seance.getDateHeure() == null) {
            throw new IllegalArgumentException("Tous les champs de la séance sont obligatoires.");
        }

        if (seance.getDateHeure().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("La séance ne peut pas être programmée dans le passé.");
        }
    }
}
