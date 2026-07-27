package fr.quentincillierre.hangman.controller;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import fr.quentincillierre.hangman.model.Difficulty;
import fr.quentincillierre.hangman.model.GameStats;
import fr.quentincillierre.hangman.model.HangmanModel;
import fr.quentincillierre.hangman.model.Word;
import fr.quentincillierre.hangman.model.WordRepository;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.animation.TranslateTransition;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.BackgroundSize;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;

public class GameController {

    // --- Main Layout Container ---
    @FXML private Pane rootPane;

    // --- Header & Stats Controls ---
    @FXML private Label streakLabel;
    @FXML private Label scoreLabel;
    @FXML private Label timerLabel;
    @FXML private ComboBox<Difficulty> difficultyComboBox;

    // --- Core Game UI ---
    @FXML private Label categoryLabel;
    @FXML private Label hintLabel;
    @FXML private Label wordLabel;
    @FXML private Label resultLabel;
    @FXML private HBox livesBox;
    @FXML private ImageView hangmanImageView;
    
    @FXML private VBox keyboardBox;
    private final Map<Character, Button> keyboardButtons = new HashMap<>();

    // --- Action Buttons ---
    @FXML private Button backButton;
    @FXML private Button hintButton;
    @FXML private Button restartButton;

    // --- Game Logic & Persistence ---
    private HangmanModel model;
    private final WordRepository wordRepository = new WordRepository();
    private final GameStats gameStats = GameStats.getInstance();

    private int winStreak = 0;
    private int score = 0;
    private int highScore = 0;
    private int lastRoundScore = 0;

    // --- Round Timer ---
    private Timeline roundTimer;
    private int secondsRemaining = 60;
    private boolean isContinuingTimer = false;

    /**
     * Sets the difficulty before the game starts.
     * Called from MenuController when the player selects a difficulty and starts the game.
     */
    public void setDifficulty(Difficulty difficulty) {
        if (difficultyComboBox != null) {
            difficultyComboBox.setValue(difficulty);
        }
    }

    @FXML
    public void initialize() {
        // Load background image safely
        setBackgroundImage();

        // Setup difficulty dropdown
        difficultyComboBox.setItems(FXCollections.observableArrayList(Difficulty.values()));
        difficultyComboBox.setValue(Difficulty.MEDIUM);
        
        difficultyComboBox.setOnAction(e -> {
            isContinuingTimer = false;
            prepareNewRound();
        });

        // Load high score from stats
        highScore = gameStats.getHighScore();

        // Setup back button
        backButton.setOnAction(event -> handleBackToMenu());

        setupTimer();
        prepareNewRound();
    }

    /**
     * Attempts to load the background image safely from resources with CSS fallback.
     */
    private void setBackgroundImage() {
        try {
            var imageStream = getClass().getResourceAsStream("/pictures/hangman.png");
            if (imageStream == null) {
                imageStream = getClass().getResourceAsStream("/hangman.png");
            }

            if (imageStream != null && rootPane != null) {
                Image bgImage = new Image(imageStream);

                BackgroundSize backgroundSize = new BackgroundSize(
                    100, 100, true, true, false, true
                );

                BackgroundImage backgroundImage = new BackgroundImage(
                    bgImage,
                    BackgroundRepeat.NO_REPEAT,
                    BackgroundRepeat.NO_REPEAT,
                    BackgroundPosition.CENTER,
                    backgroundSize
                );

                rootPane.setBackground(new Background(backgroundImage));
            }
        } catch (Exception e) {
            System.err.println("Could not load background image: " + e.getMessage());
        }
    }

    @FXML
    public void onStartClicked() {
        keyboardBox.setDisable(false);
        
        if (model.getRemainingLives() > 1) {
            hintButton.setDisable(false);
        }

        if (roundTimer != null) {
            roundTimer.play();
        }
    }

    @FXML
    public void onRestartClicked() {
        prepareNewRound();
    }

    @FXML
    public void onHintClicked() {
        if (model == null || model.isWin() || model.isLose()) return;

        Character revealedLetter = model.useHint();

        if (revealedLetter != null) {
            disableButtonForLetter(revealedLetter);
            refreshUI();
        } else {
            hintButton.setDisable(true);
        }
    }

    private void prepareNewRound() {
        Difficulty selectedDiff = difficultyComboBox.getValue();
        Word newWord = wordRepository.getRandomWord(selectedDiff);

        if (model != null && model.isWin()) {
            // Continue with the same remaining lives
            this.model = new HangmanModel(
                    newWord,
                    model.getWrongGuesses(),
                    model.getHintsUsed());
        } else {
            // New game after losing or first launch
            this.model = new HangmanModel(newWord);
        }

        if (!isContinuingTimer || secondsRemaining <= 0) {
            secondsRemaining = 60;
            timerLabel.setText("⏱️ " + secondsRemaining + "s");
        }

        if (roundTimer != null) {
            roundTimer.pause();
        }

        resultLabel.setText("");
        resultLabel.setOpacity(0);
        restartButton.setVisible(false);
        keyboardBox.setDisable(true);
        hintButton.setDisable(true);

        generateKeyboard();
        refreshUI();
        
        // Auto-start the game
        onStartClicked();
    }

    private void refreshUI() {
        streakLabel.setText("🔥 Streak: " + winStreak);
        scoreLabel.setText("🏆 Score: " + score + " (High: " + highScore + ")");

        categoryLabel.setText("Category: " + model.getCurrentWord().category());
        hintLabel.setText("💡 " + model.getCurrentWord().hint());
        wordLabel.setText(model.getHiddenWord());

        // Render Hearts (Active vs Lost)
        livesBox.getChildren().clear();
        int activeLives = model.getRemainingLives();

        for (int i = 0; i < activeLives; i++) {
            Label heart = new Label("\u2665");
            heart.setStyle("-fx-text-fill: #91001b; -fx-font-size: 22px;");
            livesBox.getChildren().add(heart);
        }

        int lostLives = model.getCurrentWrongs();
        for (int i = 0; i < lostLives; i++) {
            Label heart = new Label("\u2665");
            heart.setStyle("-fx-text-fill: #3a384d; -fx-font-size: 22px;");
            livesBox.getChildren().add(heart);
        }

        // Hangman Stage Image Resolution
        try {
            int imageIndex;
            if (model.isLose()) {
                imageIndex = 10;
            } else {
                imageIndex = (int) Math.round((double) model.getWrongGuesses() / model.getDifficulty().getMaxLives() * 10.0);
                imageIndex = Math.min(imageIndex, 10);
            }

            String imagePath = "/pictures/%s-hangman.png".formatted(imageIndex);
            var resource = getClass().getResource(imagePath);
            if (resource != null) {
                hangmanImageView.setImage(new Image(resource.toExternalForm()));
            }
        } catch (Exception e) {
            System.err.println("Could not load hangman stage image resource.");
        }

        if (model.isLose() || model.isWin()) {
            handleGameEnd();
        }
    }

    private void handleGameEnd() {
        if (roundTimer != null) {
            roundTimer.pause();
        }

        keyboardBox.setDisable(true);
        hintButton.setDisable(true);
        wordLabel.setText(model.getWordToGuess());
        resultLabel.setOpacity(1);
        resultLabel.setAlignment(Pos.CENTER);
        restartButton.setVisible(true);

        if (model.isWin()) {
            winStreak++;
            int roundScore;
            
            // Calculate points: base points doubled for each win in streak
            if (winStreak == 1) {
                roundScore = model.getDifficulty().getBasePoints();
            } else {
                roundScore = lastRoundScore * 2;
            }
            
            lastRoundScore = roundScore;
            score += roundScore;
            if (score > highScore) highScore = score;

            // Track win in stats
            gameStats.recordGameResult(true, score);

            resultLabel.setText("Victory ! +" + roundScore + " pts");
            resultLabel.setStyle("-fx-text-fill: #b0e8b6;");
            restartButton.setText("Next Round ▶");

            isContinuingTimer = true;
        } else {
            winStreak = 0;
            lastRoundScore = 0; // Reset streak multiplier on loss
            
            // Track loss in stats
            gameStats.recordGameResult(false, score);
            
            resultLabel.setText("Game Over !");
            resultLabel.setStyle("-fx-text-fill: #f38ba8;");
            restartButton.setText("Play Again");

            isContinuingTimer = false;
        }

        streakLabel.setText("🔥 Streak: " + winStreak);
        scoreLabel.setText("🏆 Score: " + score + " (High: " + highScore + ")");
    }

    private void generateKeyboard() {
        keyboardBox.getChildren().clear();
        keyboardButtons.clear();

        String[] rows = {
            "QWERTYUIOP",
            "ASDFGHJKL",
            "ZXCVBNM"
        };
        
        // Right-side water letters
        String waterLetters = "YUIHBJKNMPOL";

        for (String rowLetters : rows) {
            HBox rowBox = new HBox(8); // 8px horizontal spacing between keys
            rowBox.setAlignment(Pos.CENTER);

            for (char c : rowLetters.toCharArray()) {
                Button letterButton = new Button(String.valueOf(c));
                
                // Apply water style if letter is in right-side set
                if (waterLetters.indexOf(c) >= 0) {
                    letterButton.getStyleClass().add("keyboard-button-water");
                } else {
                    letterButton.getStyleClass().add("keyboard-button");
                }
                
                letterButton.setFocusTraversable(false);
                letterButton.setPrefSize(44, 44);
                letterButton.setMinSize(44, 44);
                letterButton.setMaxSize(44, 44);

                letterButton.setOnAction(event -> handleKeyboardInput(letterButton.getText()));

                rowBox.getChildren().add(letterButton);
                keyboardButtons.put(c, letterButton); // Cache the button
            }
            keyboardBox.getChildren().add(rowBox);
        }
    }

    public void handleKeyboardInput(String character) {
        if (model.isWin() || model.isLose() || keyboardBox.isDisabled()) return;

        if (character != null && character.length() == 1) {
            char letter = Character.toUpperCase(character.charAt(0));

            if ('A' <= letter && letter <= 'Z') {
                boolean correct = model.getWordToGuess()
                        .toUpperCase()
                        .contains(String.valueOf(letter));

                disableButtonForLetter(letter);
                model.tryLetter(letter);

                if (!correct) {
                    shakeKeyboard();
                }

                refreshUI();
            }
        }
    }

    private void shakeKeyboard() {
        TranslateTransition shake = new TranslateTransition(Duration.millis(50), keyboardBox);
        shake.setFromX(0);
        shake.setByX(12);
        shake.setCycleCount(6);
        shake.setAutoReverse(true);
        shake.play();
    }

    private void disableButtonForLetter(char letter) {
        boolean isCorrect = model.getWordToGuess().toUpperCase().contains(String.valueOf(letter));
        
        Button btn = keyboardButtons.get(Character.toUpperCase(letter));
        if (btn != null) {
            btn.setDisable(true);
            if (isCorrect) {
                // Soft Pastel Green
                btn.setStyle("-fx-background-color: #b0e8b6 !important; -fx-text-fill: #1e1e2e !important; -fx-opacity: 1.0 !important;");
            } else {
                // Dimmed Soft Magenta / Red
                btn.setStyle("-fx-background-color: rgba(120, 60, 80, 0.45) !important; -fx-text-fill: rgba(255, 255, 255, 0.3) !important; -fx-opacity: 0.6 !important;");
            }
        }
    }

    private void setupTimer() {
        roundTimer = new Timeline(new KeyFrame(Duration.seconds(1), event -> {
            secondsRemaining--;
            timerLabel.setText("⏱️ " + secondsRemaining + "s");

            if (secondsRemaining <= 0) {
                roundTimer.stop();
                isContinuingTimer = false;
                
                while (!model.isLose()) {
                    model.tryLetter('$');
                }
                refreshUI();
            }
        }));
        roundTimer.setCycleCount(Timeline.INDEFINITE);
    }

    private void handleBackToMenu() {
        try {
            // Load the menu view
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fr/quentincillierre/hangman/application/menu-view.fxml"));
            Parent menuView = loader.load();

            Scene scene = new Scene(menuView, 1000, 700);
            String css = getClass().getResource("/fr/quentincillierre/hangman/application/style.css").toExternalForm();
            scene.getStylesheets().add(css);

            Stage stage = (Stage) backButton.getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Failed to go back to menu");
            alert.setContentText(e.getMessage());
            alert.showAndWait();
        }
    }
}