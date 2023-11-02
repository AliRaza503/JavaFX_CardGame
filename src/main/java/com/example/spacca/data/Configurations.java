package com.example.spacca.data;

/**
 * This class contains the configurations for the game.
 * It is used to store the number of players, the number of cards per player, and the game mode.
 */
public class Configurations {
    // This is the unique code for the session
    public static String uniqueSessionCode;


    /**
     * Convert the game mode string to the respective symbol
     * @param newValue The game mode string
     * @return Respective symbol
     */
    public static String getVarName(String newValue) {
        switch (newValue) {
            case "Tournament" -> {
                return "T";
            }
            case "Robot" -> {
                return "R";
            }
        }
        return "D";
    }

    // Enum to store the game mode
    public enum GameMode {
        Duel,
        Tournament,
        Robot,
    }

    // Static reference to the current game's mode loaded from the configs file.
    public static GameMode gameMode;

    /**
     * Convert the game mode to the respective string
     * @return Respective string
     */
    public static String gameModeString() {
        switch (gameMode) {
            case Tournament -> {
                return "Tournament";
            }
            case Robot -> {
                return "Robot";
            }
        }
        gameMode = GameMode.Duel;
        return "Duel";
    }
    // Static references to the number of players and number of cards loaded from the configs file.
    public static int numberOfPlayers;
    public static int numberOfCardsPerPlayer = 8;

}

