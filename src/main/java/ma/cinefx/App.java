package ma.cinefx;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class App extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        // On charge le menu principal
        FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/View/fxml/MainMenuView.fxml")
        );

        Parent root = loader.load();

        Scene scene = new Scene(root);
        stage.setTitle("CineFX - Menu principal");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
