package fr.quentincillierre.hangman.controller;

import java.io.IOException;

import fr.quentincillierre.hangman.model.Difficulty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class MenuController {

    @FXML private Pane menuPane;
    @FXML private ComboBox<Difficulty> difficultyComboBox;
    @FXML private Button playButton;
    @FXML private Button rulesButton;
    @FXML private Button statsButton;
    @FXML private Button exitButton;

    @FXML
    public void initialize() {
        // Setup difficulty combo box
        difficultyComboBox.setItems(FXCollections.observableArrayList(Difficulty.values()));
        difficultyComboBox.setValue(Difficulty.MEDIUM);

        // Setup button actions
        playButton.setOnAction(event -> handlePlayGame());
        rulesButton.setOnAction(event -> handleRules());
        statsButton.setOnAction(event -> handleStats());
        exitButton.setOnAction(event -> handleExit());
    }

    private void handlePlayGame() {
        try {
            // Load the game view with correct resource path
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fr/quentincillierre/hangman/application/game-view.fxml"));
            Parent gameView = loader.load();

            // Get the controller and set the selected difficulty
            GameController gameController = loader.getController();
            gameController.setDifficulty(difficultyComboBox.getValue());

            // Create and display the game scene
            Scene scene = new Scene(gameView, 1000, 700);

            // Add CSS stylesheet
            String css = getClass().getResource("/fr/quentincillierre/hangman/application/style.css").toExternalForm();
            scene.getStylesheets().add(css);

            // Handle keyboard input
            scene.setOnKeyTyped(event -> {
                gameController.handleKeyboardInput(event.getCharacter());
            });

            // Switch to game scene
            Stage stage = (Stage) playButton.getScene().getWindow();
            stage.setScene(scene);
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error", "Failed to load game view", "Exception: " + e.getClass().getSimpleName() + "\n" + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error", "Failed to load game view", "Unexpected error: " + e.getClass().getSimpleName() + "\n" + e.getMessage());
        }
    }

    private void handleRules() {
        String rulesText = """
                HANGMAN GAME RULES:
                
                1. The computer picks a random word
                2. You must guess the word letter by letter
                3. You have a limited number of lives based on difficulty:
                   - Easy: 10 lives
                   - Medium: 8 lives
                   - Hard: 6 lives
                4. For each wrong guess, you lose a life
                5. Win by guessing all letters before running out of lives
                6. You can use the Hint button to reveal one letter
                
                SCORING:
                - Each correct guess: 1 point
                - Streak bonus: Extra points for consecutive correct answers
                - Difficulty multiplier: Harder difficulties give more points
                
                GOOD LUCK! 🍀
                """;

        showAlert("Game Rules", null, rulesText);
    }

    private void handleStats() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fr/quentincillierre/hangman/application/stats-view.fxml"));
            Parent statsView = loader.load();

            Scene scene = new Scene(statsView);
            String css = getClass().getResource("/fr/quentincillierre/hangman/application/style.css").toExternalForm();
            scene.getStylesheets().add(css);

            Stage stage = new Stage();
            stage.setTitle("Statistics");
            stage.setScene(scene);
            stage.initModality(Modality.WINDOW_MODAL);
            stage.initOwner(statsButton.getScene().getWindow());
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error", "Failed to load statistics", e.getMessage());
        }
    }

    private void handleExit() {
        Stage stage = (Stage) exitButton.getScene().getWindow();
        stage.close();
    }

    private void showAlert(String title, String header, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }
}