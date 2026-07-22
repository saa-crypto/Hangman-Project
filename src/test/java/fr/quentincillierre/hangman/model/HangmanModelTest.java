package fr.quentincillierre.hangman.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class HangmanModelTest {

    private HangmanModel model;

    @BeforeEach
void setUp() {
    // Pass all 4 required parameters: text, category, hint, difficulty
    model = new HangmanModel(new Word("java", "Programming", "Popular language", Difficulty.EASY));
}

    @Test
    void testConstructor() {
        assertEquals("JAVA", model.getWordToGuess().toUpperCase());
        assertEquals(0, model.getCurrentWrongs());
        assertTrue(model.getGuessedLetters().isEmpty());
    }

    @Test
    void testTryLetterWithCorrectLetter() {
        model.tryLetter('j');
        assertEquals(0, model.getCurrentWrongs());
        assertTrue(model.getGuessedLetters().contains('J') || model.getGuessedLetters().contains('j'));
        assertEquals("J _ _ _", model.getHiddenWord().toUpperCase());
    }

    @Test
    void testTryLetterWithIncorrectLetter() {
        boolean result = model.tryLetter('z');
        assertFalse(result);
        assertEquals(1, model.getCurrentWrongs());
        assertTrue(model.getGuessedLetters().contains('Z'));
        assertEquals("_ _ _ _", model.getHiddenWord().toUpperCase());
    }
}