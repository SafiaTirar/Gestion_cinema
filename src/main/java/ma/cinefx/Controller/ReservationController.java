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
import ma.cinefx.Model.Reservation;
import ma.cinefx.Model.Seance;
import ma.cinefx.Service.ReservationService;
import ma.cinefx.Service.SeanceService;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class ReservationController {

    @FXML
    private ComboBox<Seance> seanceCombo;
    @FXML
    private TextField clientField;
    @FXML
    private Spinner<Integer> nbPlacesSpinner;

    @FXML
    private TableView<Reservation> reservationTable;
    @FXML
    private TableColumn<Reservation, Long> colId;
    @FXML
    private TableColumn<Reservation, String> colFilm;
    @FXML
    private TableColumn<Reservation, String> colSalle;
    @FXML
    private TableColumn<Reservation, String> colDateHeure;
    @FXML
    private TableColumn<Reservation, String> colClient;
    @FXML
    private TableColumn<Reservation, Integer> colPlaces;

    private final ReservationService reservationService = new ReservationService();
    private final SeanceService seanceService = new SeanceService();

    private final ObservableList<Reservation> reservationData =
            FXCollections.observableArrayList();

    private final DateTimeFormatter formatter =
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    @FXML
    public void initialize() {
        // Spinner du nombre de places
        nbPlacesSpinner.setValueFactory(
                new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 20, 1)
        );

        // Charger les séances dans la ComboBox
        List<Seance> seances = seanceService.listerToutesLesSeances();
        seanceCombo.setItems(FXCollections.observableArrayList(seances));

        // Affichage "humain" pour une séance
        seanceCombo.setCellFactory(listView -> new ListCell<>() {
            @Override
            protected void updateItem(Seance seance, boolean empty) {
                super.updateItem(seance, empty);
                if (empty || seance == null) {
                    setText(null);
                } else {
                    setText(seance.getFilm().getTitre()
                            + " - " + seance.getSalle().getNom()
                            + " - " + seance.getDateHeure().format(formatter));
                }
            }
        });
        seanceCombo.setButtonCell(seanceCombo.getCellFactory().call(null));

        // Colonnes du tableau
        colId.setCellValueFactory(cellData ->
                new SimpleLongProperty(cellData.getValue().getId()).asObject());

        colFilm.setCellValueFactory(cellData ->
                new SimpleStringProperty(
                        cellData.getValue().getSeance().getFilm().getTitre())
        );

        colSalle.setCellValueFactory(cellData ->
                new SimpleStringProperty(
                        cellData.getValue().getSeance().getSalle().getNom())
        );

        colDateHeure.setCellValueFactory(cellData ->
                new SimpleStringProperty(
                        cellData.getValue().getSeance()
                                .getDateHeure()
                                .format(formatter))
        );

        colClient.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getClientNom())
        );

        colPlaces.setCellValueFactory(cellData ->
                new SimpleIntegerProperty(
                        cellData.getValue().getNbPlaces()).asObject()
        );

        // Charger les réservations existantes
        reservationData.setAll(reservationService.listerToutesLesReservations());
        reservationTable.setItems(reservationData);

        // Quand on sélectionne une réservation, on remplit le formulaire
        reservationTable.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldSel, newSel) -> afficherReservationDansFormulaire(newSel)
        );
    }

    private void afficherReservationDansFormulaire(Reservation reservation) {
        if (reservation == null) {
            clearForm();
            return;
        }

        seanceCombo.setValue(reservation.getSeance());
        clientField.setText(reservation.getClientNom());
        if (nbPlacesSpinner.getValueFactory() != null) {
            nbPlacesSpinner.getValueFactory().setValue(reservation.getNbPlaces());
        }
    }

    // -----------------------
    // Créer / réserver
    // -----------------------
    @FXML
    private void onReserver() {
        Seance seance = seanceCombo.getValue();
        String client = clientField.getText();
        Integer nbPlaces = nbPlacesSpinner.getValue();

        if (seance == null || client == null || client.isBlank()) {
            showAlert(Alert.AlertType.WARNING,
                    "Champs manquants",
                    "Veuillez sélectionner une séance et saisir un nom de client.");
            return;
        }

        Reservation reservation = new Reservation(seance, client, nbPlaces);

        try {
            // on passe par la méthode métier avec contrôle de capacité
            reservationService.creerReservationAvecControle(reservation);
            reservationData.setAll(reservationService.listerToutesLesReservations());
            clearForm();
        } catch (IllegalArgumentException | IllegalStateException e) {
            showAlert(Alert.AlertType.ERROR, "Réservation impossible", e.getMessage());
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur technique : " + e.getMessage());
        }
    }

    // -----------------------
    // Modifier la réservation
    // -----------------------
    @FXML
    private void onModifierReservation() {
        Reservation selected = reservationTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert(Alert.AlertType.INFORMATION,
                    "Aucune sélection",
                    "Veuillez sélectionner une réservation à modifier.");
            return;
        }

        Seance seance = seanceCombo.getValue();
        String client = clientField.getText();
        Integer nbPlaces = nbPlacesSpinner.getValue();

        if (seance == null || client == null || client.isBlank()) {
            showAlert(Alert.AlertType.WARNING,
                    "Champs manquants",
                    "Veuillez sélectionner une séance et saisir un nom de client.");
            return;
        }

        // Optionnel : empêcher la modification après le début de la séance
        if (seance.getDateHeure().isBefore(LocalDateTime.now())) {
            showAlert(Alert.AlertType.ERROR,
                    "Modification impossible",
                    "La séance a déjà commencé, la réservation ne peut plus être modifiée.");
            return;
        }

        selected.setSeance(seance);
        selected.setClientNom(client);
        selected.setNbPlaces(nbPlaces);

        try {
            reservationService.modifierReservation(selected);
            reservationData.setAll(reservationService.listerToutesLesReservations());
            clearForm();
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR,
                    "Erreur",
                    "Erreur technique lors de la mise à jour : " + e.getMessage());
        }
    }

    // -----------------------
    // Annuler (avec règle métier : avant début séance)
    // -----------------------
    @FXML
    private void onAnnulerReservation() {
        Reservation selected = reservationTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert(Alert.AlertType.INFORMATION,
                    "Aucune sélection",
                    "Veuillez sélectionner une réservation à annuler.");
            return;
        }

        try {
            reservationService.annulerReservation(selected.getId());
            reservationData.setAll(reservationService.listerToutesLesReservations());
            clearForm();
        } catch (IllegalStateException e) {
            showAlert(Alert.AlertType.ERROR, "Annulation impossible", e.getMessage());
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur technique : " + e.getMessage());
        }
    }

    // -----------------------
    // Supprimer (admin, sans logique métier)
    // -----------------------
    @FXML
    private void onSupprimerReservation() {
        Reservation selected = reservationTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert(Alert.AlertType.INFORMATION,
                    "Aucune sélection",
                    "Veuillez sélectionner une réservation à supprimer.");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirmation");
        confirm.setHeaderText("Supprimer la réservation");
        confirm.setContentText("Voulez-vous vraiment supprimer cette réservation ?");

        confirm.showAndWait().ifPresent(result -> {
            if (result == ButtonType.OK) {
                try {
                    reservationService.supprimerReservation(selected.getId());
                    reservationData.setAll(reservationService.listerToutesLesReservations());
                    clearForm();
                } catch (Exception e) {
                    showAlert(Alert.AlertType.ERROR,
                            "Erreur",
                            "Erreur technique lors de la suppression : " + e.getMessage());
                }
            }
        });
    }

    // -----------------------
    // Retour au menu principal
    // -----------------------
    @FXML
    private void onRetourMenu() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/View/fxml/MainMenuView.fxml")
            );
            Parent root = loader.load();

            Stage stage = (Stage) seanceCombo.getScene().getWindow();
            stage.setTitle("CineFX - Menu principal");
            stage.setScene(new Scene(root));
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
            showAlert(
                    Alert.AlertType.ERROR,
                    "Erreur",
                    "Impossible de revenir au menu : " + e.getMessage()
            );
        }
    }

    // -----------------------
    // Méthodes utilitaires
    // -----------------------
    private void clearForm() {
        seanceCombo.setValue(null);
        clientField.clear();
        if (nbPlacesSpinner.getValueFactory() != null) {
            nbPlacesSpinner.getValueFactory().setValue(1);
        }
        reservationTable.getSelectionModel().clearSelection();
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
