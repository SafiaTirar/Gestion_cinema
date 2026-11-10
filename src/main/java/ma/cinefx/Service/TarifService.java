package ma.cinefx.Service;

import ma.cinefx.Dao.TarifDAO;
import ma.cinefx.Exception.EntityAlreadyExistsException;
import ma.cinefx.Exception.EntityNotFoundException;
import ma.cinefx.Exception.EntityPersistenceException;
import ma.cinefx.Model.Tarif;

import java.util.List;

public class TarifService {

    private final TarifDAO tarifDAO;

    public TarifService() {
        this.tarifDAO = new TarifDAO();
    }

    // --------- NOUVELLE MÉTHODE UTILISÉE PAR SeanceController ---------
    public void ajouterTarif(Tarif tarif) {
        if (tarif == null) {
            throw new IllegalArgumentException("Tarif invalide");
        }

        try {
            tarifDAO.addTarif(tarif);
            System.out.println("Tarif ajouté avec succès !");
        } catch (EntityAlreadyExistsException e) {
            System.out.println("Impossible d'ajouter le tarif : " + e.getMessage());
            throw e;
        } catch (EntityPersistenceException e) {
            System.out.println("Erreur technique lors de l'ajout du tarif : " + e.getMessage());
            throw e;
        }
    }

    // (Optionnel) si tu utilisais avant une version avec libellé/prix séparés
    public void ajouterTarif(String libelle, double prix) {
        Tarif tarif = new Tarif();
        tarif.setLibelle(libelle);
        tarif.setPrix(prix);
        ajouterTarif(tarif);
    }

    // --------- Reste du CRUD classique ---------

    public Tarif recupererTarifParId(Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("ID invalide");
        }

        try {
            return tarifDAO.getTarifById(id);
        } catch (EntityNotFoundException e) {
            System.out.println("Tarif introuvable : " + e.getMessage());
            return null;
        } catch (EntityPersistenceException e) {
            System.out.println("Erreur technique lors de la récupération du tarif : " + e.getMessage());
            return null;
        }
    }

    public List<Tarif> listerTousLesTarifs() {
        try {
            return tarifDAO.getAllTarifs();
        } catch (EntityPersistenceException e) {
            System.out.println("Erreur technique lors de la récupération des tarifs : " + e.getMessage());
            return List.of();
        }
    }

    public void modifierTarif(Tarif tarif) {
        if (tarif == null) {
            throw new IllegalArgumentException("Tarif invalide");
        }

        try {
            tarifDAO.updateTarif(tarif);
            System.out.println("Tarif mis à jour avec succès !");
        } catch (EntityNotFoundException e) {
            System.out.println("Tarif introuvable : " + e.getMessage());
        } catch (EntityPersistenceException e) {
            System.out.println("Erreur technique lors de la mise à jour du tarif : " + e.getMessage());
        }
    }

    public void supprimerTarif(Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("ID invalide");
        }

        try {
            tarifDAO.deleteTarif(id);
            System.out.println("Tarif supprimé avec succès !");
        } catch (EntityNotFoundException e) {
            System.out.println("Tarif introuvable : " + e.getMessage());
        } catch (EntityPersistenceException e) {
            System.out.println("Erreur technique lors de la suppression du tarif : " + e.getMessage());
        }
    }
}
