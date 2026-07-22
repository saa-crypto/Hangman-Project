package fr.quentincillierre.hangman.model;

import java.util.HashSet;
import java.util.Set;

public class HangmanModel {

    private final Word currentWord;
    private final Difficulty difficulty;
    private final Set<Character> guessedLetters = new HashSet<>();
    private int wrongGuesses = 0;
    private int hintsUsed = 0;

    public HangmanModel(Word word) {
        this.currentWord = word;
        this.difficulty = word.difficulty();
    }

    public boolean tryLetter(char letter) {
        letter = Character.toUpperCase(letter);
        guessedLetters.add(letter);

        if (!currentWord.text().toUpperCase().contains(String.valueOf(letter))) {
            wrongGuesses++;
            return false;
        }
        return true;
    }

    // Uncovers 1 unrevealed letter in exchange for 1 life (does not count as a wrong guess)
    public Character useHint() {
        if (getRemainingLives() <= 1) return null; // Prevent self-elimination via hint

        for (char c : currentWord.text().toUpperCase().toCharArray()) {
            if (!guessedLetters.contains(c)) {
                guessedLetters.add(c);
                hintsUsed++; // Deducts 1 life without advancing hangman drawing
                return c;
            }
        }
        return null;
    }

    public String getHiddenWord() {
        StringBuilder builder = new StringBuilder();
        for (char c : currentWord.text().toCharArray()) {
            if (guessedLetters.contains(Character.toUpperCase(c))) {
                builder.append(c).append(" ");
            } else {
                builder.append("_ ");
            }
        }
        return builder.toString().trim();
    }

    public boolean isWin() {
        for (char c : currentWord.text().toUpperCase().toCharArray()) {
            if (!guessedLetters.contains(c)) {
                return false;
            }
        }
        return true;
    }

    public boolean isLose() {
        return (wrongGuesses + hintsUsed) >= difficulty.getMaxLives();
    }

    public int getRemainingLives() {
        return Math.max(0, difficulty.getMaxLives() - (wrongGuesses + hintsUsed));
    }

    public Word getCurrentWord() { return currentWord; }
    public Difficulty getDifficulty() { return difficulty; }
    public int getWrongGuesses() { return wrongGuesses; }
    public int getCurrentWrongs() { return wrongGuesses + hintsUsed; }
    public int getMaxWrongs() { return difficulty.getMaxLives(); }
    public String getWordToGuess() { return currentWord.text(); }

    // Added getter method for guessed letters
    public Set<Character> getGuessedLetters() {
        return guessedLetters;
    }
}