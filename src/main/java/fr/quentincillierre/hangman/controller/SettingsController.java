package fr.quentincillierre.hangman.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Slider;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public class SettingsController {

    @FXML private Pane settingsPane;
    @FXML private CheckBox soundCheckBox;
    @FXML private Slider difficultySlider;
    @FXML private CheckBox animationsCheckBox;
    @FXML private Button backButton;

    @FXML
    public void initialize() {
        // Initialize settings with default values
        soundCheckBox.setSelected(true);
        animationsCheckBox.setSelected(true);
        difficultySlider.setValue(50);

        backButton.setOnAction(event -> handleBack());
    }

    private void handleBack() {
        Stage stage = (Stage) backButton.getScene().getWindow();
        stage.close();
    }

    // Getters for settings
    public boolean isSoundEnabled() {
        return soundCheckBox.isSelected();
    }

    public boolean isAnimationsEnabled() {
        return animationsCheckBox.isSelected();
    }

    public double getDifficultyModifier() {
        return difficultySlider.getValue() / 100.0;
    }
}
