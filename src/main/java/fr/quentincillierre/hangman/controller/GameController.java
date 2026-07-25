package fr.quentincillierre.hangman.controller;

import fr.quentincillierre.hangman.model.Difficulty;
import fr.quentincillierre.hangman.model.HangmanModel;
import fr.quentincillierre.hangman.model.WordRepository;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.animation.TranslateTransition;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Node;
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
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
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
    @FXML private Label wordLabel;
    @FXML private Label resultLabel;
    @FXML private HBox livesBox;
    @FXML private ImageView hangmanImageView;
    @FXML private GridPane keyboardGrid;

    // --- Action Buttons ---
    @FXML private Button startButton;
    @FXML private Button hintButton;
    @FXML private Button restartButton;

    // --- Game Logic & Persistence ---
    private HangmanModel model;
    private final WordRepository wordRepository = new WordRepository();

    private int winStreak = 0;
    private int score = 0;
    private int highScore = 0;

    // --- Round Timer ---
    private Timeline roundTimer;
    private int secondsRemaining = 60;
    private boolean isContinuingTimer = false;

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

        setupTimer();
        prepareNewRound();
    }

    /**
     * Attempts to load the background image safely from resources with CSS fallback.
     */
    private void setBackgroundImage() {
        try {
            var imageStream = getClass().getResourceAsStream("/Hangman.png");
            if (imageStream == null) {
                imageStream = getClass().getResourceAsStream("/pictures/background.png");
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
        startButton.setVisible(false);
        keyboardGrid.setDisable(false);
        
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
            
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Hint Used!");
            alert.setHeaderText("Category: " + model.getCurrentWord().category());
            alert.setContentText("Clue: " + model.getCurrentWord().hint());
            alert.show();

            refreshUI();
        } else {
            hintButton.setDisable(true);
        }
    }

    private void prepareNewRound() {
        Difficulty selectedDiff = difficultyComboBox.getValue();
        this.model = new HangmanModel(wordRepository.getRandomWord(selectedDiff));

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
        startButton.setVisible(true);
        keyboardGrid.setDisable(true);
        hintButton.setDisable(true);

        generateKeyboard();
        refreshUI();
    }

    private void refreshUI() {
        streakLabel.setText("🔥 Streak: " + winStreak);
        scoreLabel.setText("🏆 Score: " + score + " (High: " + highScore + ")");

        categoryLabel.setText("Category: " + model.getCurrentWord().category());
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

        keyboardGrid.setDisable(true);
        hintButton.setDisable(true);
        startButton.setVisible(false);
        wordLabel.setText(model.getWordToGuess());
        resultLabel.setOpacity(1);
        resultLabel.setAlignment(Pos.CENTER);
        restartButton.setVisible(true);

        if (model.isWin()) {
            winStreak++;
            int roundScore = model.getDifficulty().getBasePoints() + (secondsRemaining * 5);
            score += roundScore;
            if (score > highScore) highScore = score;

            resultLabel.setText("Victory ! +" + roundScore + " pts");
            resultLabel.setStyle("-fx-text-fill: #b0e8b6;");
            restartButton.setText("Next Round ▶");

            isContinuingTimer = true;
        } else {
            winStreak = 0;
            resultLabel.setText("Game Over !");
            resultLabel.setStyle("-fx-text-fill: #f38ba8;");
            restartButton.setText("Play Again");

            isContinuingTimer = false;
        }

        streakLabel.setText("🔥 Streak: " + winStreak);
        scoreLabel.setText("🏆 Score: " + score + " (High: " + highScore + ")");
    }

    private void generateKeyboard() {
        keyboardGrid.getChildren().clear();

        for (char c = 'A'; c <= 'Z'; c++) {
            Button letterButton = new Button(String.valueOf(c));
            letterButton.getStyleClass().add("keyboard-button");

            letterButton.setOnAction(event -> handleKeyboardInput(letterButton.getText()));

            int index = c - 'A';
            int col = index % 13;
            int row = index / 13;

            keyboardGrid.add(letterButton, col, row);
        }
    }

    public void handleKeyboardInput(String character) {
        if (model.isWin() || model.isLose() || keyboardGrid.isDisabled()) return;

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
        TranslateTransition shake = new TranslateTransition(Duration.millis(50), keyboardGrid);
        shake.setFromX(0);
        shake.setByX(12);
        shake.setCycleCount(6);
        shake.setAutoReverse(true);
        shake.play();
    }

    private void disableButtonForLetter(char letter) {
        boolean isCorrect = model.getWordToGuess().toUpperCase().contains(String.valueOf(letter));

        for (Node node : keyboardGrid.getChildren()) {
            if (node instanceof Button btn && btn.getText().equalsIgnoreCase(String.valueOf(letter))) {
                btn.setDisable(true);
                if (isCorrect) {
                    // Soft Pastel Green
                    btn.setStyle("-fx-background-color: #b0e8b6 !important; -fx-text-fill: #1e1e2e !important; -fx-opacity: 1.0 !important;");
                } else {
                    // Dimmed Soft Magenta / Red
                    btn.setStyle("-fx-background-color: rgba(120, 60, 80, 0.45) !important; -fx-text-fill: rgba(255, 255, 255, 0.3) !important; -fx-opacity: 0.6 !important;");
                }
                break;
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
}