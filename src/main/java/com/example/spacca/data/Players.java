package com.example.spacca.data;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class Players {
    public static List<String> allPlayers = new ArrayList<>();
    public static List<String> allPlayerCodes = new ArrayList<>();
    public static List<Integer> allPlayerPoints = new ArrayList<>();
    private static final String FILE_NAME = "players_data.csv";

    //For adding the players from the application
    public static void addPlayer(String pName) {
        allPlayers.add(pName);
        allPlayerCodes.add(generatePlayerCode());
        allPlayerPoints.add(0);
        writePlayerDataToFile();
    }

    //For adding the players from the file
    public static void addPlayer(String pName, String pCode, int pts) {
        allPlayers.add(pName);
        allPlayerCodes.add(pCode);
        allPlayerPoints.add(pts);
    }

    //To generate the code for the player
    private static String generatePlayerCode() {
        // Generate a random 4-digit code
        int code = (int) (Math.random() * 9000) + 1000;
        String playerCode = String.valueOf(code);
        // Check if the code already exists
        if (allPlayerCodes.contains(playerCode)) {
            // If the code already exists, generate a new code
            return generatePlayerCode();
        }
        return playerCode;
    }

    /**
     * Creates a new list for the players
     */
    public static void makeList() {
        allPlayers = new ArrayList<>();
        allPlayerCodes = new ArrayList<>();
        allPlayerPoints = new ArrayList<>();
    }

    /**
     * Writes the player data to the file
     */
    public static void writePlayerDataToFile() {
        // Write the data to the file
        BufferedWriter writer;
        try {
            writer = new BufferedWriter(new FileWriter(FILE_NAME));
            for (int i = 0; i < allPlayers.size(); i++) {
                writer.write(allPlayers.get(i) + "," + allPlayerCodes.get(i) + "," + allPlayerPoints.get(i));
                writer.newLine();
            }
            writer.close();
        } catch (IOException e) {
            System.err.println("Error writing to the file: " + e.getMessage());
        }
    }

    /**
     * Reads the player data from the file <br>
     * If the file does not exist, create a new file
     */
    public static void readPlayerDataFromFile() {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(FILE_NAME));
            String line;
            while ((line = reader.readLine()) != null) {
                String[] data = line.split(",");
                // Add the player to the list
                addPlayer(data[0], data[1], Integer.parseInt(data[2]));
            }
            reader.close();
        } catch (IOException e) {
            System.err.println("Error reading the file: " + e.getMessage());
            // If the file does not exist, create a new file
            File file = new File(FILE_NAME);
            try {
                if (file.createNewFile()) {
                    System.out.println("File created: " + file.getName());
                }
            } catch (IOException ex) {
                System.err.println("Error creating the file: " + ex.getMessage());
            }
        }
    }

    /**
     * Updates the points and write to the file
     * @param points to be updated
     * @param code place of update
     */
    public static void updatePPoints(int points, String code) {
        int idx = allPlayerCodes.indexOf(code);
        allPlayerPoints.set(idx, points);
        writePlayerDataToFile();
    }

    /**
     * Updates the name and write to the file
     * @param oldValue the old name
     * @param newValue modified name
     */
    public static void updatePName(String oldValue, String newValue) {
        allPlayers.set(allPlayers.indexOf(oldValue), newValue);
        // Use threads to write data to the file
        Thread writingDataThread = new Thread(Players::writePlayerDataToFile);
        writingDataThread.start();
    }
}
