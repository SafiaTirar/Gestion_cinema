package ma.cinefx.Controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.StringConverter;
import ma.cinefx.Model.Seance;
import ma.cinefx.Model.Film;
import ma.cinefx.Model.Salle;
import ma.cinefx.Model.Tarif;
import ma.cinefx.Service.SeanceService;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;


public class PlanningController {

    @FXML
    private ComboBox<Film> cbFilm;

    @FXML
    private ComboBox<Salle> cbSalle;

    @FXML
    private DatePicker dpDate;

    @FXML
    private Button btnFiltrer;

    @FXML
    private TableView<Seance> tableSeances;

    @FXML
    private TableColumn<Seance, Long> colId;

    @FXML
    private TableColumn<Seance, String> colFilm;

    @FXML
    private TableColumn<Seance, String> colSalle;

    @FXML
    private TableColumn<Seance, String> colDateHeure;

    @FXML
    private TableColumn<Seance, String> colTarif;

    private final SeanceService seanceService = new SeanceService();

    private final ObservableList<Seance> seancesObservable =
            FXCollections.observableArrayList();

    private final ObservableList<Seance> seancesFiltrees =
            FXCollections.observableArrayList();

    private final ObservableList<Film> filmsObservable =
            FXCollections.observableArrayList();

    private final ObservableList<Salle> sallesObservable =
            FXCollections.observableArrayList();

    private final DateTimeFormatter dateTimeFormatter =
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    @FXML
    public void initialize() {
        // Colonnes
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));

        colFilm.setCellValueFactory(cellData -> {
            Seance s = cellData.getValue();
            Film f = s != null ? s.getFilm() : null;
            String titre = (f != null) ? f.getTitre() : "";
            return new javafx.beans.property.SimpleStringProperty(titre);
        });

        colSalle.setCellValueFactory(cellData -> {
            Seance s = cellData.getValue();
            Salle salle = s != null ? s.getSalle() : null;
            String nom = (salle != null) ? salle.getNom() : "";
            return new javafx.beans.property.SimpleStringProperty(nom);
        });

        colDateHeure.setCellValueFactory(cellData -> {
            Seance s = cellData.getValue();
            String txt = (s != null && s.getDateHeure() != null)
                    ? s.getDateHeure().format(dateTimeFormatter)
                    : "";
            return new javafx.beans.property.SimpleStringProperty(txt);
        });

        colTarif.setCellValueFactory(cellData -> {
            Seance s = cellData.getValue();
            Tarif t = s != null ? s.getTarif() : null;
            String lib = "";
            if (t != null) {
                lib = t.getLibelle() + " (" + t.getPrix() + "€)";
            }
            return new javafx.beans.property.SimpleStringProperty(lib);
        });

        tableSeances.setItems(seancesFiltrees);

        // Converters pour les ComboBox Film / Salle
        cbFilm.setConverter(new StringConverter<>() {
            @Override
            public String toString(Film film) {
                return film != null ? film.getTitre() : "";
            }

            @Override
            public Film fromString(String s) {
                return null;
            }
        });

        cbSalle.setConverter(new StringConverter<>() {
            @Override
            public String toString(Salle salle) {
                return salle != null ? salle.getNom() : "";
            }

            @Override
            public Salle fromString(String s) {
                return null;
            }
        });

        chargerSeancesEtFiltres();
    }

    private void chargerSeancesEtFiltres() {
        try {
            List<Seance> seances = seanceService.listerToutesLesSeances();
            seancesObservable.setAll(seances);
            seancesFiltrees.setAll(seances);

            // Extraire les films/salles distincts à partir des séances
            Set<Film> films = seances.stream()
                    .map(Seance::getFilm)
                    .filter(f -> f != null)
                    .collect(Collectors.toSet());
            filmsObservable.setAll(films);
            cbFilm.getItems().setAll(filmsObservable);

            Set<Salle> salles = seances.stream()
                    .map(Seance::getSalle)
                    .filter(s -> s != null)
                    .collect(Collectors.toSet());
            sallesObservable.setAll(salles);
            cbSalle.getItems().setAll(sallesObservable);

        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur");
            alert.setHeaderText("Erreur lors du chargement du planning");
            alert.setContentText(e.getMessage());
            alert.showAndWait();
        }
    }

    @FXML
    private void onFiltrer() {
        Film filmSel = cbFilm.getValue();
        Salle salleSel = cbSalle.getValue();
        LocalDate dateSel = dpDate.getValue();

        List<Seance> resultat = seancesObservable.stream()
                .filter(s -> filmSel == null || (s.getFilm() != null && s.getFilm().getId().equals(filmSel.getId())))
                .filter(s -> salleSel == null || (s.getSalle() != null && s.getSalle().getId().equals(salleSel.getId())))
                .filter(s -> {
                    if (dateSel == null || s.getDateHeure() == null) return true;
                    return s.getDateHeure().toLocalDate().isEqual(dateSel);
                })
                .collect(Collectors.toList());

        seancesFiltrees.setAll(resultat);
    }

    @FXML
    private void onReinitialiser() {
        cbFilm.setValue(null);
        cbSalle.setValue(null);
        dpDate.setValue(null);
        seancesFiltrees.setAll(seancesObservable);
    }
}
