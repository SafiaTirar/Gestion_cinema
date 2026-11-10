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
import ma.cinefx.Model.Film;
import ma.cinefx.Service.FilmService;

import java.io.IOException;
import java.util.List;

public class FilmController {

    @FXML
    private TextField titreField;
    @FXML
    private TextField genreField;
    @FXML
    private TextField dureeField;

    @FXML
    private TableView<Film> filmTable;
    @FXML
    private TableColumn<Film, Long> colId;
    @FXML
    private TableColumn<Film, String> colTitre;
    @FXML
    private TableColumn<Film, String> colGenre;
    @FXML
    private TableColumn<Film, Integer> colDuree;

    private final FilmService filmService = new FilmService();
    private final ObservableList<Film> filmData = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        // Colonnes
        colId.setCellValueFactory(cd ->
                new SimpleLongProperty(cd.getValue().getId()).asObject());

        colTitre.setCellValueFactory(cd ->
                new SimpleStringProperty(cd.getValue().getTitre()));

        colGenre.setCellValueFactory(cd ->
                new SimpleStringProperty(cd.getValue().getGenre()));

        colDuree.setCellValueFactory(cd ->
                new SimpleIntegerProperty(cd.getValue().getDuree()).asObject());

        // Charger les films
        chargerFilms();

        filmTable.setItems(filmData);

        // Quand on sélectionne un film, remplir le formulaire
        filmTable.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldSel, newSel) -> afficherFilmDansFormulaire(newSel)
        );
    }

    private void chargerFilms() {
        try {
            List<Film> films = filmService.listerTousLesFilms();
            filmData.setAll(films);
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors du chargement des films : " + e.getMessage());
        }
    }

    private void afficherFilmDansFormulaire(Film film) {
        if (film == null) {
            clearForm();
            return;
        }

        titreField.setText(film.getTitre());
        genreField.setText(film.getGenre());
        dureeField.setText(String.valueOf(film.getDuree()));
    }

    @FXML
    private void onCreerFilm() {
        try {
            Film film = construireFilmDepuisFormulaire(null);
            filmService.creerFilm(film);
            showAlert(Alert.AlertType.INFORMATION, "Succès", "Film créé avec succès.");
            chargerFilms();
            clearForm();
        } catch (IllegalArgumentException e) {
            showAlert(Alert.AlertType.WARNING, "Données invalides", e.getMessage());
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur technique : " + e.getMessage());
        }
    }

    @FXML
    private void onModifierFilm() {
        Film selected = filmTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert(Alert.AlertType.INFORMATION, "Aucune sélection", "Veuillez sélectionner un film.");
            return;
        }

        try {
            Film film = construireFilmDepuisFormulaire(selected.getId());
            filmService.modifierFilm(film);
            showAlert(Alert.AlertType.INFORMATION, "Succès", "Film modifié avec succès.");
            chargerFilms();
            clearForm();
        } catch (IllegalArgumentException e) {
            showAlert(Alert.AlertType.WARNING, "Données invalides", e.getMessage());
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur technique : " + e.getMessage());
        }
    }

    @FXML
    private void onSupprimerFilm() {
        Film selected = filmTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert(Alert.AlertType.INFORMATION, "Aucune sélection", "Veuillez sélectionner un film.");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirmation");
        confirm.setHeaderText("Suppression du film");
        confirm.setContentText("Voulez-vous vraiment supprimer ce film ?");
        confirm.showAndWait().ifPresent(result -> {
            if (result == ButtonType.OK) {
                try {
                    filmService.supprimerFilm(selected.getId());
                    showAlert(Alert.AlertType.INFORMATION, "Succès", "Film supprimé.");
                    chargerFilms();
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
            Stage stage = (Stage) filmTable.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("CineFX - Menu principal");
            stage.show();
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible de revenir au menu : " + e.getMessage());
        }
    }

    private Film construireFilmDepuisFormulaire(Long idExistant) {
        String titre = titreField.getText();
        String genre = genreField.getText();
        String dureeTxt = dureeField.getText();

        if (titre == null || titre.isBlank()
                || genre == null || genre.isBlank()
                || dureeTxt == null || dureeTxt.isBlank()) {
            throw new IllegalArgumentException("Tous les champs sont obligatoires.");
        }

        int duree;
        try {
            duree = Integer.parseInt(dureeTxt);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("La durée doit être un entier (ex: 120).");
        }

        Film film = new Film();
        if (idExistant != null) {
            film.setId(idExistant);
        }
        film.setTitre(titre);
        film.setGenre(genre);
        film.setDuree(duree);

        return film;
    }

    private void clearForm() {
        titreField.clear();
        genreField.clear();
        dureeField.clear();
        filmTable.getSelectionModel().clearSelection();
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
