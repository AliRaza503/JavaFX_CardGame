package com.example.spacca.data;

/**
 * First player details accessible from anywhere in the application.
 */
public class FirstPlayer {
    public static String name = "Player 1";
    public static String code;
    public static String[] cards;
    public static int remainingCards = Cards.cardsPerPlayer;
    public static int points = 0;

    // Initialize the fields of the FirstPlayer class
    public static void initFields(String s) {
        code = s;
        try {
            int idx = Players.allPlayerCodes.indexOf(s);
            name = Players.allPlayers.get(idx);
            remainingCards = Cards.cardsPerPlayer;
            points = Players.allPlayerPoints.get(idx);
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
}
