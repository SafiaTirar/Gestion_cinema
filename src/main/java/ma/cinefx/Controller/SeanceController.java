package ma.cinefx.Controller;

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
import javafx.util.StringConverter;
import ma.cinefx.Model.Film;
import ma.cinefx.Model.Salle;
import ma.cinefx.Model.Seance;
import ma.cinefx.Model.Tarif;
import ma.cinefx.Service.FilmService;
import ma.cinefx.Service.SalleService;
import ma.cinefx.Service.SeanceService;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

public class SeanceController {

    @FXML private ComboBox<Film> filmCombo;
    @FXML private ComboBox<Salle> salleCombo;
    @FXML private DatePicker datePicker;
    @FXML private TextField heureField;
    @FXML private TextField tarifField;

    @FXML private TableView<Seance> seanceTable;
    @FXML private TableColumn<Seance, Long> colId;
    @FXML private TableColumn<Seance, String> colFilm;
    @FXML private TableColumn<Seance, String> colSalle;
    @FXML private TableColumn<Seance, String> colDateHeure;
    @FXML private TableColumn<Seance, String> colTarif;

    private final SeanceService seanceService = new SeanceService();
    private final FilmService filmService = new FilmService();
    private final SalleService salleService = new SalleService();

    private final ObservableList<Seance> seanceData = FXCollections.observableArrayList();

    private final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    private final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");

    @FXML
    public void initialize() {
        chargerFilms();
        chargerSalles();
        chargerSeances();

        // Converters ComboBox
        filmCombo.setConverter(new StringConverter<>() {
            @Override
            public String toString(Film film) {
                return film != null ? film.getTitre() : "";
            }
            @Override
            public Film fromString(String s) { return null; }
        });

        salleCombo.setConverter(new StringConverter<>() {
            @Override
            public String toString(Salle salle) {
                return salle != null ? salle.getNom() : "";
            }
            @Override
            public Salle fromString(String s) { return null; }
        });

        // Colonnes
        colId.setCellValueFactory(cellData ->
                new SimpleLongProperty(cellData.getValue().getId()).asObject());

        colFilm.setCellValueFactory(cellData -> {
            Film f = cellData.getValue().getFilm();
            return new SimpleStringProperty(f != null ? f.getTitre() : "");
        });

        colSalle.setCellValueFactory(cellData -> {
            Salle s = cellData.getValue().getSalle();
            return new SimpleStringProperty(s != null ? s.getNom() : "");
        });

        colDateHeure.setCellValueFactory(cellData -> {
            LocalDateTime dt = cellData.getValue().getDateHeure();
            return new SimpleStringProperty(dt != null ? dt.format(dateTimeFormatter) : "");
        });

        colTarif.setCellValueFactory(cellData -> {
            Tarif t = cellData.getValue().getTarif();
            return new SimpleStringProperty(t != null ? (t.getPrix() + "€") : "");
        });

        seanceTable.setItems(seanceData);

        // Remplir formulaire à la sélection
        seanceTable.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldSel, newSel) -> afficherSeanceDansFormulaire(newSel)
        );
    }

    private void chargerFilms() {
        try {
            List<Film> films = filmService.listerTousLesFilms();
            filmCombo.setItems(FXCollections.observableArrayList(films));
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur chargement films : " + getRootCause(e));
        }
    }

    private void chargerSalles() {
        try {
            List<Salle> salles = salleService.listerToutesLesSalles();
            salleCombo.setItems(FXCollections.observableArrayList(salles));
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur chargement salles : " + getRootCause(e));
        }
    }

    private void chargerSeances() {
        try {
            List<Seance> seances = seanceService.listerToutesLesSeances();
            seanceData.setAll(seances);
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur chargement séances : " + getRootCause(e));
        }
    }

    private void afficherSeanceDansFormulaire(Seance seance) {
        if (seance == null) return;

        filmCombo.setValue(seance.getFilm());
        salleCombo.setValue(seance.getSalle());

        if (seance.getDateHeure() != null) {
            datePicker.setValue(seance.getDateHeure().toLocalDate());
            heureField.setText(seance.getDateHeure().toLocalTime().format(timeFormatter));
        } else {
            datePicker.setValue(null);
            heureField.clear();
        }

        if (seance.getTarif() != null) {
            tarifField.setText(String.valueOf(seance.getTarif().getPrix()));
        } else {
            tarifField.clear();
        }
    }

    // --- Créer ---
    @FXML
    private void onCreerSeance() {
        try {
            Seance seance = construireSeanceDepuisFormulaire(null);
            seanceService.creerSeance(seance);

            showAlert(Alert.AlertType.INFORMATION, "Succès", "Séance créée avec succès.");
            chargerSeances();
            clearForm();

        } catch (IllegalArgumentException e) {
            showAlert(Alert.AlertType.WARNING, "Données invalides", e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur technique : " + getRootCause(e));
        }
    }

    // --- Modifier ---
    @FXML
    private void onModifierSeance() {
        Seance selected = seanceTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert(Alert.AlertType.INFORMATION, "Aucune sélection", "Veuillez sélectionner une séance.");
            return;
        }

        try {
            Seance seanceModifiee = construireSeanceDepuisFormulaire(selected.getId());

            // IMPORTANT : si tu modifies une séance existante, garde le tarif existant si tu ne veux pas en recréer un
            // Ici on laisse la logique: si tarifField est rempli => nouveau tarif, sinon garder l'ancien
            if (tarifField.getText() == null || tarifField.getText().isBlank()) {
                seanceModifiee.setTarif(selected.getTarif());
            }

            seanceService.modifierSeance(seanceModifiee);

            showAlert(Alert.AlertType.INFORMATION, "Succès", "Séance modifiée avec succès.");
            chargerSeances();
            clearForm();

        } catch (IllegalArgumentException e) {
            showAlert(Alert.AlertType.WARNING, "Données invalides", e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur technique : " + getRootCause(e));
        }
    }

    // --- Supprimer ---
    @FXML
    private void onSupprimerSeance() {
        Seance selected = seanceTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert(Alert.AlertType.INFORMATION, "Aucune sélection", "Veuillez sélectionner une séance.");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirmation");
        confirm.setHeaderText("Supprimer la séance");
        confirm.setContentText("Voulez-vous vraiment supprimer cette séance ?");
        confirm.showAndWait().ifPresent(result -> {
            if (result == ButtonType.OK) {
                try {
                    seanceService.supprimerSeance(selected.getId());
                    showAlert(Alert.AlertType.INFORMATION, "Succès", "Séance supprimée.");
                    chargerSeances();
                    clearForm();
                } catch (Exception e) {
                    e.printStackTrace();
                    showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur technique : " + getRootCause(e));
                }
            }
        });
    }

    // --- Retour ---
    @FXML
    private void onRetourMenu() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/View/fxml/MainMenuView.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) filmCombo.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("CineFX - Menu principal");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible de revenir au menu : " + getRootCause(e));
        }
    }

    // --- Construction Seance ---
    private Seance construireSeanceDepuisFormulaire(Long idExistant) {
        Film film = filmCombo.getValue();
        Salle salle = salleCombo.getValue();
        LocalDate date = datePicker.getValue();
        String heureTxt = heureField.getText();
        String tarifTxt = tarifField.getText();

        if (film == null || salle == null || date == null ||
                heureTxt == null || heureTxt.isBlank() ||
                tarifTxt == null || tarifTxt.isBlank()) {
            throw new IllegalArgumentException("Tous les champs sont obligatoires.");
        }

        LocalTime heure;
        try {
            heure = LocalTime.parse(heureTxt.trim(), timeFormatter);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Format de l'heure invalide (attendu HH:mm, ex: 20:30).");
        }

        double prix;
        try {
            prix = Double.parseDouble(tarifTxt.trim());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Le tarif doit être un nombre (ex : 10.5).");
        }

        LocalDateTime dateHeure = LocalDateTime.of(date, heure);

        Seance seance = new Seance();
        if (idExistant != null) {
            seance.setId(idExistant); // nécessite setId(Long) dans Seance
        }

        seance.setFilm(film);
        seance.setSalle(salle);
        seance.setDateHeure(dateHeure);

        // ✅ IMPORTANT : on NE sauvegarde PAS le tarif ici.
        // Le tarif sera persisté automatiquement avec la séance (CascadeType.PERSIST dans Seance).
        Tarif tarif = new Tarif();
        tarif.setLibelle("Tarif saisi");
        tarif.setPrix(prix);
        seance.setTarif(tarif);

        return seance;
    }

    private void clearForm() {
        filmCombo.setValue(null);
        salleCombo.setValue(null);
        datePicker.setValue(null);
        heureField.clear();
        tarifField.clear();
        seanceTable.getSelectionModel().clearSelection();
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private String getRootCause(Throwable e) {
        Throwable root = e;
        while (root.getCause() != null) root = root.getCause();
        return root.getClass().getSimpleName() + " : " + root.getMessage();
    }
}
