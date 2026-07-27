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
        words.add(new Word("BANANA", "Fruit", "Yellow curved tropical fruit", Difficulty.EASY));
        words.add(new Word("BUTTERFLY", "Animals", "Colorful insect with wings", Difficulty.EASY));
        words.add(new Word("ELEPHANT", "Animals", "Largest land animal with a trunk", Difficulty.EASY));
        words.add(new Word("KEYBOARD", "Instrument", "Device with keys for typing", Difficulty.EASY));
        words.add(new Word("MOUNTAIN", "Geography", "High peak of land", Difficulty.EASY));
        words.add(new Word("OCEAN", "Geography", "Large body of saltwater", Difficulty.EASY));
        words.add(new Word("PENGUIN", "Animals", "Black and white bird from Antarctica", Difficulty.EASY));

        // --- MEDIUM WORDS (8 Lives) ---
        words.add(new Word("JAVAFX", "Programming", "GUI toolkit for Java developers", Difficulty.MEDIUM));
        words.add(new Word("PYRAMID", "History", "Ancient structure found in Egypt", Difficulty.MEDIUM));
        words.add(new Word("VOLCANO", "Geography", "Mountain that erupts with lava", Difficulty.MEDIUM));
        words.add(new Word("COLONY", "Movie", "A horror and action movie", Difficulty.MEDIUM));
        words.add(new Word("ADVENTURE", "Movies", "Exciting journey or experience", Difficulty.MEDIUM));
        words.add(new Word("BASKETBALL", "Sports", "Team sport played on a court", Difficulty.MEDIUM));
        words.add(new Word("CELEBRATION", "Event", "Festive occasion marking something", Difficulty.MEDIUM));
        words.add(new Word("DOLPHIN", "Animals", "Intelligent marine mammal", Difficulty.MEDIUM));
        words.add(new Word("FESTIVAL", "Event", "Gathering with entertainment and food", Difficulty.MEDIUM));
        words.add(new Word("GALAXY", "Astronomy", "System of billions of stars", Difficulty.MEDIUM));
        words.add(new Word("HARMONY", "Music", "Pleasant combination of sounds", Difficulty.MEDIUM));

        // --- HARD WORDS (6 Lives) ---
        words.add(new Word("ALGORITHM", "Computer Science", "Step-by-step procedure for solving a problem", Difficulty.HARD));
        words.add(new Word("CRYPTOGRAPHY", "Security", "The art of writing or solving codes", Difficulty.HARD));
        words.add(new Word("RHYTHM", "Music", "A strong, regular repeated pattern of movement or sound", Difficulty.HARD));
        words.add(new Word("HOATZIN", "Bird", "This South American oddity is famous for being a biological anomaly", Difficulty.HARD));
        words.add(new Word("PHENOMENON", "Science", "Remarkable or unusual occurrence", Difficulty.HARD));
        words.add(new Word("ONOMATOPOEIA", "Language", "Word that imitates the sound it represents", Difficulty.HARD));
        words.add(new Word("ENTREPRENEUR", "Business", "Person who starts a business venture", Difficulty.HARD));
        words.add(new Word("BUREAUCRACY", "Government", "System of complex administrative procedures", Difficulty.HARD));
        words.add(new Word("DYSTOPIAN", "Literature", "Imagined state of future society", Difficulty.HARD));
        words.add(new Word("SERENDIPITY", "Life", "Finding something good by luck", Difficulty.HARD));

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