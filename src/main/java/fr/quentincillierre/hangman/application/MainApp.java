package fr.quentincillierre.hangman.application;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainApp extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        // Load the main menu
        FXMLLoader loader = new FXMLLoader(getClass().getResource("menu-view.fxml"));
        Parent root = loader.load();

        Scene scene = new Scene(root, 1000, 700);

        // --- Add the CSS Stylesheet here ---
        String css = getClass().getResource("style.css").toExternalForm();
        scene.getStylesheets().add(css);

        primaryStage.setTitle("HangMan");
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }

}