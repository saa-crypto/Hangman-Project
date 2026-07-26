package fr.quentincillierre.hangman.model;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Manages game statistics and high scores persistence
 */
public class GameStats {
    private static final String STATS_FILE = "hangman_stats.dat";
    private static final Path STATS_PATH = Paths.get(System.getProperty("user.home"), ".hangman", STATS_FILE);

    private int highScore = 0;
    private int totalGames = 0;
    private int totalWins = 0;
    private int totalLosses = 0;
    private int currentStreak = 0;
    private int bestStreak = 0;

    // Singleton instance
    private static GameStats instance;

    private GameStats() {
        loadStats();
    }

    public static GameStats getInstance() {
        if (instance == null) {
            instance = new GameStats();
        }
        return instance;
    }

    /**
     * Load stats from file
     */
    private void loadStats() {
        try {
            if (Files.exists(STATS_PATH)) {
                ObjectInputStream ois = new ObjectInputStream(Files.newInputStream(STATS_PATH));
                highScore = ois.readInt();
                totalGames = ois.readInt();
                totalWins = ois.readInt();
                totalLosses = ois.readInt();
                currentStreak = ois.readInt();
                bestStreak = ois.readInt();
                ois.close();
            }
        } catch (IOException e) {
            System.err.println("Could not load stats: " + e.getMessage());
        }
    }

    /**
     * Save stats to file
     */
    public void saveStats() {
        try {
            Files.createDirectories(STATS_PATH.getParent());
            ObjectOutputStream oos = new ObjectOutputStream(Files.newOutputStream(STATS_PATH));
            oos.writeInt(highScore);
            oos.writeInt(totalGames);
            oos.writeInt(totalWins);
            oos.writeInt(totalLosses);
            oos.writeInt(currentStreak);
            oos.writeInt(bestStreak);
            oos.close();
        } catch (IOException e) {
            System.err.println("Could not save stats: " + e.getMessage());
        }
    }

    /**
     * Record a game result
     */
    public void recordGameResult(boolean won, int score) {
        totalGames++;
        if (won) {
            totalWins++;
            currentStreak++;
            if (currentStreak > bestStreak) {
                bestStreak = currentStreak;
            }
            if (score > highScore) {
                highScore = score;
            }
        } else {
            totalLosses++;
            currentStreak = 0;
        }
        saveStats();
    }

    // Getters
    public int getHighScore() { return highScore; }
    public int getTotalGames() { return totalGames; }
    public int getTotalWins() { return totalWins; }
    public int getTotalLosses() { return totalLosses; }
    public int getCurrentStreak() { return currentStreak; }
    public int getBestStreak() { return bestStreak; }

    public double getWinRate() {
        if (totalGames == 0) return 0;
        return (double) totalWins / totalGames * 100;
    }

    // Setters
    public void resetStats() {
        highScore = 0;
        totalGames = 0;
        totalWins = 0;
        totalLosses = 0;
        currentStreak = 0;
        bestStreak = 0;
        saveStats();
    }
}
