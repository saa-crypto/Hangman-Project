package fr.quentincillierre.hangman.model;

public enum Difficulty {
    EASY(10, "Easy", 100),
    MEDIUM(8, "Medium", 200),
    HARD(6, "Hard", 300);

    private final int maxLives;
    private final String displayName;
    private final int basePoints;

    Difficulty(int maxLives, String displayName, int basePoints) {
        this.maxLives = maxLives;
        this.displayName = displayName;
        this.basePoints = basePoints;
    }

    public int getMaxLives() {
        return maxLives;
    }

    public String getDisplayName() {
        return displayName;
    }

    public int getBasePoints() {
        return basePoints;
    }

    @Override
    public String toString() {
        return displayName;
    }
}