package ma.cinefx.Service;

import ma.cinefx.Dao.SalleDAO;
import ma.cinefx.Exception.EntityAlreadyExistsException;
import ma.cinefx.Exception.EntityNotFoundException;
import ma.cinefx.Exception.EntityPersistenceException;
import ma.cinefx.Model.Salle;

import java.util.List;

public class SalleService {

    private final SalleDAO salleDAO;

    public SalleService() {
        this.salleDAO = new SalleDAO();
    }

    // Lister toutes les salles
    public List<Salle> listerToutesLesSalles() {
        try {
            return salleDAO.getAllSalles();
        } catch (EntityPersistenceException e) {
            System.out.println("Erreur technique lors de la récupération des salles : " + e.getMessage());
            return List.of();
        }
    }

    // Créer une salle
    public void creerSalle(Salle salle) {
        validerSalle(salle);

        try {
            salleDAO.addSalle(salle);
            System.out.println("Salle créée avec succès !");
        } catch (EntityAlreadyExistsException e) {
            throw new IllegalArgumentException("Une salle avec ce nom existe déjà.");
        } catch (EntityPersistenceException e) {
            System.out.println("Erreur technique lors de la création de la salle : " + e.getMessage());
            throw e;
        }
    }

    // Modifier une salle
    public void modifierSalle(Salle salle) {
        if (salle == null || salle.getId() == null || salle.getId() <= 0) {
            throw new IllegalArgumentException("Salle ou ID invalide.");
        }
        validerSalle(salle);

        try {
            salleDAO.updateSalle(salle);
            System.out.println("Salle modifiée avec succès !");
        } catch (EntityNotFoundException e) {
            throw new IllegalArgumentException("Salle introuvable : " + e.getMessage());
        } catch (EntityPersistenceException e) {
            System.out.println("Erreur technique lors de la modification de la salle : " + e.getMessage());
            throw e;
        }
    }

    // Supprimer une salle
    public void supprimerSalle(Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("ID invalide.");
        }

        try {
            salleDAO.deleteSalle(id);
            System.out.println("Salle supprimée avec succès !");
        } catch (EntityNotFoundException e) {
            throw new IllegalArgumentException("Salle introuvable : " + e.getMessage());
        } catch (EntityPersistenceException e) {
            System.out.println("Erreur technique lors de la suppression de la salle : " + e.getMessage());
            throw e;
        }
    }

    // Validation métier de base
    private void validerSalle(Salle salle) {
        if (salle == null
                || salle.getNom() == null || salle.getNom().isBlank()) {
            throw new IllegalArgumentException("Le nom de la salle est obligatoire.");
        }
        if (salle.getCapacite() <= 0) {
            throw new IllegalArgumentException("La capacité doit être > 0.");
        }
    }
}
