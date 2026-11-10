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
import ma.cinefx.Model.Seance;
import ma.cinefx.Service.SeanceService;

import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class RechercheController {

    @FXML private TextField txtFilm;
    @FXML private TextField txtGenre;
    @FXML private DatePicker dpDate;

    @FXML private TableView<Seance> tableSeances;
    @FXML private TableColumn<Seance, Long> colId;
    @FXML private TableColumn<Seance, String> colFilm;
    @FXML private TableColumn<Seance, String> colGenre;
    @FXML private TableColumn<Seance, String> colSalle;
    @FXML private TableColumn<Seance, String> colDateHeure;
    @FXML private TableColumn<Seance, String> colTarif;

    @FXML private Label lblInfo;

    private final SeanceService seanceService = new SeanceService();
    private final DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    @FXML
    public void initialize() {
        colId.setCellValueFactory(c -> new SimpleObjectProperty<>(c.getValue().getId()));
        colFilm.setCellValueFactory(c -> new SimpleStringProperty(
                c.getValue().getFilm() != null ? c.getValue().getFilm().getTitre() : ""
        ));
        colGenre.setCellValueFactory(c -> new SimpleStringProperty(
                c.getValue().getFilm() != null ? c.getValue().getFilm().getGenre() : ""
        ));
        colSalle.setCellValueFactory(c -> new SimpleStringProperty(
                c.getValue().getSalle() != null ? c.getValue().getSalle().getNom() : ""
        ));
        colDateHeure.setCellValueFactory(c -> new SimpleStringProperty(
                c.getValue().getDateHeure() != null ? c.getValue().getDateHeure().format(fmt) : ""
        ));
        colTarif.setCellValueFactory(c -> new SimpleStringProperty(
                c.getValue().getTarif() != null
                        ? (c.getValue().getTarif().getLibelle() + " (" + c.getValue().getTarif().getPrix() + ")")
                        : ""
        ));

        onSearch(); // affiche tout au début
    }

    @FXML
    public void onSearch() {
        List<Seance> res = seanceService.searchSeances(
                txtFilm.getText(),
                dpDate.getValue(),
                txtGenre.getText()
        );

        tableSeances.setItems(FXCollections.observableArrayList(res));
        lblInfo.setText("Résultats : " + res.size());
    }

    @FXML
    public void onReset() {
        txtFilm.clear();
        txtGenre.clear();
        dpDate.setValue(null);
        onSearch();
    }

    @FXML
    public void onBack() {
        switchScene("/View/fxml/MainMenuView.fxml", "CineFX - Menu principal");
    }

    private void switchScene(String fxmlPath, String title) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource(fxmlPath));
            Stage stage = (Stage) tableSeances.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle(title);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
