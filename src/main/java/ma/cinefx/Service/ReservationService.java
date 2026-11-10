package ma.cinefx.Service;

import ma.cinefx.Dao.ReservationDAO;
import ma.cinefx.Model.Reservation;
import ma.cinefx.Model.Seance;
import ma.cinefx.Model.Salle;
import ma.cinefx.Exception.EntityAlreadyExistsException;
import ma.cinefx.Exception.EntityNotFoundException;
import ma.cinefx.Exception.EntityPersistenceException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import ma.cinefx.Dto.RecetteSeanceDTO;

public class ReservationService {

    private final ReservationDAO reservationDAO;

    public ReservationService() {
        this.reservationDAO = new ReservationDAO();
    }

    // -----------------------------
    // MÉTHODES MÉTIER
    // -----------------------------

    /**
     * Méthode appelée depuis le contrôleur JavaFX.
     * Applique les règles métier (capacité, données valides, etc.).
     */
    public void creerReservation(Reservation reservation) {
        creerReservationAvecControle(reservation);
    }

    // Créer une réservation en vérifiant la capacité de la salle
    public void creerReservationAvecControle(Reservation reservation) {
        if (reservation == null
                || reservation.getSeance() == null
                || reservation.getClientNom() == null
                || reservation.getClientNom().isBlank()
                || reservation.getNbPlaces() <= 0) {
            throw new IllegalArgumentException("Données de réservation invalides.");
        }

        Seance seance = reservation.getSeance();
        Salle salle = seance.getSalle();
        int capacite = salle.getCapacite();

        // Récupérer toutes les réservations de cette séance
        List<Reservation> existantes =
                reservationDAO.getReservationsBySeance(seance.getId());

        int dejaReserve = existantes.stream()
                .mapToInt(Reservation::getNbPlaces)
                .sum();

        int totalApres = dejaReserve + reservation.getNbPlaces();

        if (totalApres > capacite) {
            throw new IllegalArgumentException(
                    "Capacité dépassée : " + totalApres + " > " + capacite
                            + " pour la salle " + salle.getNom()
            );
        }

        // Si tout est OK, on ajoute
        ajouterReservation(reservation);
    }

    // -----------------------------
    // CRUD "BRUT"
    // -----------------------------

    // Ajouter une réservation (sans logique métier)
    public void ajouterReservation(Reservation reservation) {
        try {
            reservationDAO.addReservation(reservation);
            System.out.println("Réservation ajoutée avec succès !");
        } catch (EntityAlreadyExistsException e) {
            System.out.println("Impossible d'ajouter la réservation : " + e.getMessage());
            throw e;
        } catch (EntityPersistenceException e) {
            System.out.println("Erreur technique lors de l'ajout : " + e.getMessage());
            throw e;
        }
    }
    public RecetteSeanceDTO getRecetteBySeance(long seanceId) {
        return reservationDAO.getRecetteBySeance(seanceId);
    }

    public List<RecetteSeanceDTO> getRecettesParFilm(Long filmId, LocalDateTime start, LocalDateTime end) {
        return reservationDAO.getRecettesParFilm(filmId, start, end);
    }


    // Récupérer une réservation par ID
    public Reservation recupererReservationParId(Long id) {
        try {
            return reservationDAO.getReservationById(id);
        } catch (EntityNotFoundException e) {
            System.out.println("Réservation introuvable : " + e.getMessage());
            return null;
        } catch (EntityPersistenceException e) {
            System.out.println("Erreur lors de la récupération : " + e.getMessage());
            return null;
        }
    }

    // Lister toutes les réservations
    public List<Reservation> listerToutesLesReservations() {
        try {
            return reservationDAO.getAllReservations();
        } catch (EntityPersistenceException e) {
            System.out.println("Erreur lors de la récupération des réservations : " + e.getMessage());
            return new ArrayList<>();
        }
    }

    // Mettre à jour une réservation
    public void modifierReservation(Reservation reservation) {
        try {
            reservationDAO.updateReservation(reservation);
            System.out.println("Réservation mise à jour avec succès !");
        } catch (EntityNotFoundException e) {
            System.out.println("Réservation introuvable : " + e.getMessage());
        } catch (EntityPersistenceException e) {
            System.out.println("Erreur lors de la mise à jour : " + e.getMessage());
        }
    }

    // Supprimer une réservation (suppression "brute")
    public void supprimerReservation(Long id) {
        try {
            reservationDAO.deleteReservation(id);
            System.out.println("Réservation supprimée avec succès !");
        } catch (EntityNotFoundException e) {
            System.out.println("Réservation introuvable : " + e.getMessage());
        } catch (EntityPersistenceException e) {
            System.out.println("Erreur lors de la suppression : " + e.getMessage());
        }
    }
    public double getRecetteByPeriode(LocalDateTime start, LocalDateTime end) {
        return reservationDAO.getRecetteByPeriode(start, end);
    }
    public long countSeancesInRange(LocalDateTime start, LocalDateTime end) {
        return reservationDAO.countSeancesInRange(start, end);
    }


    public List<RecetteSeanceDTO> getRecettesParSeance(LocalDateTime start, LocalDateTime end) {
        return reservationDAO.getRecettesParSeance(start, end);
    }


    // Annuler une réservation : seulement avant l'heure de début de la séance
    public void annulerReservation(Long idReservation) {
        if (idReservation == null || idReservation <= 0) {
            throw new IllegalArgumentException("ID de réservation invalide.");
        }

        try {
            Reservation reservation = reservationDAO.getReservationById(idReservation);
            Seance seance = reservation.getSeance();

            if (seance.getDateHeure().isBefore(LocalDateTime.now())) {
                throw new IllegalStateException(
                        "Impossible d'annuler : la séance a déjà commencé."
                );
            }

            reservationDAO.deleteReservation(idReservation);
            System.out.println("Réservation annulée avec succès.");

        } catch (EntityNotFoundException e) {
            System.out.println("Réservation introuvable : " + e.getMessage());
            throw e;
        } catch (EntityPersistenceException e) {
            System.out.println("Erreur technique lors de l'annulation : " + e.getMessage());
            throw e;
        }
    }
}
