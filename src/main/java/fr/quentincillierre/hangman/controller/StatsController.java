package fr.quentincillierre.hangman.controller;

import fr.quentincillierre.hangman.model.GameStats;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public class StatsController {

    @FXML private Pane statsPane;
    @FXML private Label highScoreLabel;
    @FXML private Label totalGamesLabel;
    @FXML private Label winsLabel;
    @FXML private Label winRateLabel;
    @FXML private Label currentStreakLabel;
    @FXML private Label bestStreakLabel;
    @FXML private Button backButton;
    @FXML private Button resetButton;

    private GameStats stats;

    @FXML
    public void initialize() {
        stats = GameStats.getInstance();
        refreshStats();

        backButton.setOnAction(event -> handleBack());
        resetButton.setOnAction(event -> handleReset());
    }

    private void refreshStats() {
        highScoreLabel.setText("🏆 High Score: " + stats.getHighScore());
        totalGamesLabel.setText("🎮 Total Games: " + stats.getTotalGames());
        winsLabel.setText("✅ Wins: " + stats.getTotalWins() + " | ❌ Losses: " + stats.getTotalLosses());
        winRateLabel.setText("📊 Win Rate: " + String.format("%.1f%%", stats.getWinRate()));
        currentStreakLabel.setText("🔥 Current Streak: " + stats.getCurrentStreak());
        bestStreakLabel.setText("⭐ Best Streak: " + stats.getBestStreak());
    }

    private void handleBack() {
        Stage stage = (Stage) backButton.getScene().getWindow();
        stage.close();
    }

    private void handleReset() {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Reset Statistics");
        confirm.setHeaderText("Are you sure?");
        confirm.setContentText("This will reset all your statistics. This action cannot be undone.");
        
        if (confirm.showAndWait().isPresent()) {
            stats.resetStats();
            refreshStats();
            Alert success = new Alert(Alert.AlertType.INFORMATION);
            success.setTitle("Success");
            success.setHeaderText(null);
            success.setContentText("Statistics have been reset!");
            success.showAndWait();
        }
    }
}
