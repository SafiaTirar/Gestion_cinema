package ma.cinefx.Controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.io.IOException;

public class MainMenuController {

    @FXML
    private Button btnSeances;

    @FXML
    private Button btnReservations;

    // Ouvrir l'écran de gestion des films
    @FXML
    private void openFilmView() {
        switchScene("/View/fxml/FilmView.fxml", "CineFX - Gestion des films");
    }

    // Ouvrir l'écran de gestion des salles
    @FXML
    private void openSalleView() {
        switchScene("/View/fxml/SalleView.fxml", "CineFX - Gestion des salles");
    }

    // Ouvrir l'écran de gestion des séances
    @FXML
    private void openSeanceView() {
        switchScene("/View/fxml/SeanceView.fxml", "CineFX - Gestion des séances");
    }

    // Ouvrir l'écran de gestion des réservations
    @FXML
    private void openReservationView() {
        switchScene("/View/fxml/ReservationView.fxml", "CineFX - Gestion des réservations");
    }
    // Ouvrir l'écran de recherche (film/date/genre)
    @FXML
    private void openRechercheView() {
        switchScene("/View/fxml/RechercheView.fxml", "CineFX - Recherche");
    }

    // Ouvrir l'écran rapport (recettes)
    @FXML
    private void openRapportView() {
        switchScene("/View/fxml/RapportView.fxml", "CineFX - Rapport / Recettes");
    }


    // Quitter l'application
    @FXML
    private void onQuitter() {
        Stage stage = (Stage) btnSeances.getScene().getWindow();
        stage.close();
    }

    // Méthode utilitaire pour changer de scène
    private void switchScene(String fxmlPath, String title) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();

            Stage stage = (Stage) btnSeances.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle(title);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            // Tu peux remplacer par une Alert si tu veux
        }
    }
}
