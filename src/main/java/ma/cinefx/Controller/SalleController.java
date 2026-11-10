package ma.cinefx.Controller;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import ma.cinefx.Model.Salle;
import ma.cinefx.Service.SalleService;

import java.io.IOException;
import java.util.List;

public class SalleController {

    @FXML
    private TextField nomField;
    @FXML
    private TextField capaciteField;

    @FXML
    private TableView<Salle> salleTable;
    @FXML
    private TableColumn<Salle, Long> colId;
    @FXML
    private TableColumn<Salle, String> colNom;
    @FXML
    private TableColumn<Salle, Integer> colCapacite;

    private final SalleService salleService = new SalleService();
    private final ObservableList<Salle> salleData = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        // Colonnes
        colId.setCellValueFactory(cd ->
                new SimpleLongProperty(cd.getValue().getId()).asObject());

        colNom.setCellValueFactory(cd ->
                new SimpleStringProperty(cd.getValue().getNom()));

        colCapacite.setCellValueFactory(cd ->
                new SimpleIntegerProperty(cd.getValue().getCapacite()).asObject());

        // Charger les salles
        chargerSalles();

        salleTable.setItems(salleData);

        // Quand on sélectionne une salle, remplir le formulaire
        salleTable.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldSel, newSel) -> afficherSalleDansFormulaire(newSel)
        );
    }

    private void chargerSalles() {
        try {
            List<Salle> salles = salleService.listerToutesLesSalles();
            salleData.setAll(salles);
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors du chargement des salles : " + e.getMessage());
        }
    }

    private void afficherSalleDansFormulaire(Salle salle) {
        if (salle == null) {
            clearForm();
            return;
        }

        nomField.setText(salle.getNom());
        capaciteField.setText(String.valueOf(salle.getCapacite()));
    }

    @FXML
    private void onCreerSalle() {
        try {
            Salle salle = construireSalleDepuisFormulaire(null);
            salleService.creerSalle(salle);
            showAlert(Alert.AlertType.INFORMATION, "Succès", "Salle créée avec succès.");
            chargerSalles();
            clearForm();
        } catch (IllegalArgumentException e) {
            showAlert(Alert.AlertType.WARNING, "Données invalides", e.getMessage());
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur technique : " + e.getMessage());
        }
    }

    @FXML
    private void onModifierSalle() {
        Salle selected = salleTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert(Alert.AlertType.INFORMATION, "Aucune sélection", "Veuillez sélectionner une salle.");
            return;
        }

        try {
            Salle salle = construireSalleDepuisFormulaire(selected.getId());
            salleService.modifierSalle(salle);
            showAlert(Alert.AlertType.INFORMATION, "Succès", "Salle modifiée avec succès.");
            chargerSalles();
            clearForm();
        } catch (IllegalArgumentException e) {
            showAlert(Alert.AlertType.WARNING, "Données invalides", e.getMessage());
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur technique : " + e.getMessage());
        }
    }

    @FXML
    private void onSupprimerSalle() {
        Salle selected = salleTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert(Alert.AlertType.INFORMATION, "Aucune sélection", "Veuillez sélectionner une salle.");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirmation");
        confirm.setHeaderText("Suppression de la salle");
        confirm.setContentText("Voulez-vous vraiment supprimer cette salle ?");
        confirm.showAndWait().ifPresent(result -> {
            if (result == ButtonType.OK) {
                try {
                    salleService.supprimerSalle(selected.getId());
                    showAlert(Alert.AlertType.INFORMATION, "Succès", "Salle supprimée.");
                    chargerSalles();
                    clearForm();
                } catch (Exception e) {
                    showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur technique : " + e.getMessage());
                }
            }
        });
    }

    @FXML
    private void onRetourMenu() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/View/fxml/MainMenuView.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) salleTable.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("CineFX - Menu principal");
            stage.show();
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible de revenir au menu : " + e.getMessage());
        }
    }

    private Salle construireSalleDepuisFormulaire(Long idExistant) {
        String nom = nomField.getText();
        String capaciteTxt = capaciteField.getText();

        if (nom == null || nom.isBlank()
                || capaciteTxt == null || capaciteTxt.isBlank()) {
            throw new IllegalArgumentException("Tous les champs sont obligatoires.");
        }

        int capacite;
        try {
            capacite = Integer.parseInt(capaciteTxt);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("La capacité doit être un entier (ex: 100).");
        }

        Salle salle = new Salle();
        if (idExistant != null) {
            salle.setId(idExistant);
        }
        salle.setNom(nom);
        salle.setCapacite(capacite);

        return salle;
    }

    private void clearForm() {
        nomField.clear();
        capaciteField.clear();
        salleTable.getSelectionModel().clearSelection();
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
