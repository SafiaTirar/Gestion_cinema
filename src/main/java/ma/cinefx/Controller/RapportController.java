package ma.cinefx.Controller;

import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import ma.cinefx.Dto.RecetteSeanceDTO;
import ma.cinefx.Model.Film;
import ma.cinefx.Model.Seance;
import ma.cinefx.Service.FilmService;
import ma.cinefx.Service.ReservationService;
import ma.cinefx.Service.SeanceService;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class RapportController {

    @FXML private DatePicker dpStart;
    @FXML private DatePicker dpEnd;

    @FXML private ComboBox<Film> filmCombo;
    @FXML private ComboBox<Seance> seanceCombo;

    @FXML private TableView<RecetteSeanceDTO> tableRecettes;
    @FXML private TableColumn<RecetteSeanceDTO, Long> colSeanceId;
    @FXML private TableColumn<RecetteSeanceDTO, String> colFilm;
    @FXML private TableColumn<RecetteSeanceDTO, String> colGenre;
    @FXML private TableColumn<RecetteSeanceDTO, String> colSalle;
    @FXML private TableColumn<RecetteSeanceDTO, String> colDateHeure;
    @FXML private TableColumn<RecetteSeanceDTO, String> colPrix;
    @FXML private TableColumn<RecetteSeanceDTO, String> colPlaces;
    @FXML private TableColumn<RecetteSeanceDTO, String> colCalcul;
    @FXML private TableColumn<RecetteSeanceDTO, String> colRecette;

    @FXML private Label lblTotal;
    @FXML private Label lblInfo;

    private final ReservationService reservationService = new ReservationService();
    private final FilmService filmService = new FilmService();
    private final SeanceService seanceService = new SeanceService();

    private final DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    @FXML
    public void initialize() {

        // Table columns
        colSeanceId.setCellValueFactory(c -> new SimpleObjectProperty<>(c.getValue().getSeanceId()));
        colFilm.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getFilmTitre()));
        colGenre.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getGenre()));
        colSalle.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getSalleNom()));
        colDateHeure.setCellValueFactory(c -> new SimpleStringProperty(
                c.getValue().getDateHeure() != null ? c.getValue().getDateHeure().format(fmt) : ""
        ));

        colPrix.setCellValueFactory(c ->
                new SimpleStringProperty(String.format("%.2f", c.getValue().getPrixTarif()))
        );
        colPlaces.setCellValueFactory(c ->
                new SimpleStringProperty(String.valueOf(c.getValue().getNbPlacesTotal()))
        );

        colCalcul.setCellValueFactory(c -> new SimpleStringProperty(
                c.getValue().getNbPlacesTotal() + " × " +
                        String.format("%.2f", c.getValue().getPrixTarif()) +
                        " = " + String.format("%.2f", c.getValue().getRecette())
        ));

        colRecette.setCellValueFactory(c ->
                new SimpleStringProperty(String.format("%.2f", c.getValue().getRecette()))
        );

        tableRecettes.setItems(FXCollections.observableArrayList());
        lblTotal.setText("0.0 €");

        // Combo Film
        filmCombo.setConverter(new StringConverter<>() {
            @Override public String toString(Film f) { return f != null ? f.getTitre() : ""; }
            @Override public Film fromString(String s) { return null; }
        });

        // Combo Séance (affichage lisible)
        seanceCombo.setConverter(new StringConverter<>() {
            @Override public String toString(Seance s) {
                if (s == null) return "";
                String film = (s.getFilm() != null) ? s.getFilm().getTitre() : "Film?";
                String date = (s.getDateHeure() != null) ? s.getDateHeure().format(fmt) : "Date?";
                return "ID " + s.getId() + " - " + film + " - " + date;
            }
            @Override public Seance fromString(String s) { return null; }
        });

        chargerFilms();
        chargerSeances();

        // Astuce UX : si tu choisis une séance, ça enlève film (pas obligatoire mais clair)
        seanceCombo.valueProperty().addListener((obs, oldV, newV) -> {
            if (newV != null) filmCombo.setValue(null);
        });
    }

    private void chargerFilms() {
        List<Film> films = filmService.listerTousLesFilms();
        filmCombo.setItems(FXCollections.observableArrayList(films));
    }

    private void chargerSeances() {
        List<Seance> seances = seanceService.listerToutesLesSeances();
        seanceCombo.setItems(FXCollections.observableArrayList(seances));
    }

    @FXML
    public void onCalculer() {
        try {
            LocalDateTime start = null;
            LocalDateTime end = null;

            // Période optionnelle
            if (dpStart.getValue() != null && dpEnd.getValue() != null) {
                start = dpStart.getValue().atStartOfDay();
                end = dpEnd.getValue().plusDays(1).atStartOfDay().minusSeconds(1);
                if (end.isBefore(start)) {
                    showAlert("Période invalide", "La date de fin doit être après la date de début.");
                    return;
                }
            }

            Seance seanceSel = seanceCombo.getValue();
            Film filmSel = filmCombo.getValue();

            // ✅ MODE 1 : Par séance (prioritaire)
            if (seanceSel != null) {
                long seanceId = seanceSel.getId();
                RecetteSeanceDTO dto = reservationService.getRecetteBySeance(seanceId);

                tableRecettes.setItems(FXCollections.observableArrayList(dto));
                lblTotal.setText(String.format("%.2f €", dto.getRecette()));
                lblInfo.setText("Recette de la séance ID " + seanceId);
                return;
            }

            // ✅ MODE 2 : Par film (avec période si fournie)
            if (filmSel != null) {
                Long filmId = filmSel.getId();
                List<RecetteSeanceDTO> rows = reservationService.getRecettesParFilm(filmId, start, end);
                tableRecettes.setItems(FXCollections.observableArrayList(rows));

                double total = rows.stream().mapToDouble(RecetteSeanceDTO::getRecette).sum();
                lblTotal.setText(String.format("%.2f €", total));

                if (start != null) {
                    lblInfo.setText("Film: " + filmSel.getTitre() + " | Période filtrée | Séances: " + rows.size());
                } else {
                    lblInfo.setText("Film: " + filmSel.getTitre() + " | Toutes périodes | Séances: " + rows.size());
                }
                return;
            }

            // ✅ MODE 3 : Par période (toutes séances)
            if (start == null || end == null) {
                showAlert("Choix manquant", "Choisis une période, ou bien un film, ou bien une séance.");
                return;
            }

            List<RecetteSeanceDTO> rows = reservationService.getRecettesParSeance(start, end);
            tableRecettes.setItems(FXCollections.observableArrayList(rows));

            double total = rows.stream().mapToDouble(RecetteSeanceDTO::getRecette).sum();
            lblTotal.setText(String.format("%.2f €", total));

            lblInfo.setText("Période: séances trouvées = " + rows.size());

        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Erreur technique", getRootCause(e));
        }
    }

    @FXML
    public void onReset() {
        dpStart.setValue(null);
        dpEnd.setValue(null);
        filmCombo.setValue(null);
        seanceCombo.setValue(null);
        tableRecettes.setItems(FXCollections.observableArrayList());
        lblTotal.setText("0.0 €");
        lblInfo.setText("Choisis un filtre puis clique sur Calculer.");
    }

    @FXML
    public void onBack() {
        switchScene("/View/fxml/MainMenuView.fxml", "CineFX - Menu principal");
    }

    private void switchScene(String fxmlPath, String title) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource(fxmlPath));
            Stage stage = (Stage) tableRecettes.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle(title);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Erreur", "Impossible de charger la vue : " + e.getMessage());
        }
    }

    private void showAlert(String title, String msg) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }

    private String getRootCause(Throwable e) {
        Throwable root = e;
        while (root.getCause() != null) root = root.getCause();
        return root.getClass().getSimpleName() + " : " + root.getMessage();
    }
}
