package com.example.spacca.data;

/**
 * Second player details accessible from anywhere in the application.
 */
public class SecondPlayer {
    public static String name = "Player 2";
    public static String code;
    public static String[] cards;
    public static int remainingCards = Cards.cardsPerPlayer;

    public static boolean robotHasValidMoves = true;
    public static int points = 0;

    public static void initFields(String s) {
        code = s;
        try {
            int idx = Players.allPlayerCodes.indexOf(s);
            name = Players.allPlayers.get(idx);
            remainingCards = Cards.cardsPerPlayer;
            points = Players.allPlayerPoints.get(idx);
        } catch(Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
}
