package fr.quentincillierre.hangman.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class WordRepository {

    private final List<Word> words = new ArrayList<>();
    private final Random random = new Random();

    public WordRepository() {
        // --- EASY WORDS (10 Lives) ---
        words.add(new Word("APPLE", "Fruit", "A common red or green fruit", Difficulty.EASY));
        words.add(new Word("GIRAFFE", "Animals", "Tallest land animal with a long neck", Difficulty.EASY));
        words.add(new Word("GUITAR", "Instrument", "Six-stringed musical instrument", Difficulty.EASY));
        words.add(new Word("VIOLET", "Color", "It's the last color in the rainbow", Difficulty.EASY));

        // --- MEDIUM WORDS (8 Lives) ---
        words.add(new Word("JAVAFX", "Programming", "GUI toolkit for Java developers", Difficulty.MEDIUM));
        words.add(new Word("PYRAMID", "History", "Ancient structure found in Egypt", Difficulty.MEDIUM));
        words.add(new Word("VOLCANO", "Geography", "Mountain that erupts with lava", Difficulty.MEDIUM));
        words.add(new Word("COLONY", "Movie", "A horror and action movie", Difficulty.MEDIUM));


        // --- HARD WORDS (6 Lives) ---
        words.add(new Word("ALGORITHM", "Computer Science", "Step-by-step procedure for solving a problem", Difficulty.HARD));
        words.add(new Word("CRYPTOGRAPHY", "Security", "The art of writing or solving codes", Difficulty.HARD));
        words.add(new Word("RHYTHM", "Music", "A strong, regular repeated pattern of movement or sound", Difficulty.HARD));
        words.add(new Word("HOATZIN", "Bird", "This South American oddity is famous for being a biological anomaly", Difficulty.HARD));

    }

    public Word getRandomWord(Difficulty difficulty) {
        List<Word> filtered = words.stream()
                .filter(w -> w.difficulty() == difficulty)
                .toList();

        if (filtered.isEmpty()) {
            return new Word("HANGMAN", "General", "Default game word", difficulty);
        }
        return filtered.get(random.nextInt(filtered.size()));
    }
}