package com.example.spacca.data;

import javafx.util.Pair;

import java.util.ArrayList;
import java.util.List;

public class TournamentPlayers {
    private static TournamentPlayers instance;

    public ArrayList<String> tournamentPlayerCodes;
    public List<Pair<Integer, Integer>> matches;
    int currentMatchIdx;
    private static int currentPlayersCount = Configurations.numberOfPlayers;
    // list of codes of winners of the previous round
    private static ArrayList<String> winners;

    /**
     * private constructor for singleton class
     */
    private TournamentPlayers() {
        tournamentPlayerCodes = new ArrayList<>();
        matches = new ArrayList<>();
        currentMatchIdx = 0;
        if (winners != null) {
            tournamentPlayerCodes.addAll(winners);
        }

        winners = new ArrayList<>();
        // make a plan for matches.
        makeMatchesPlan(currentPlayersCount);
    }

    /**
     * makes a plan for matches
     *
     * @param numPlayers number of players in the tournament
     */
    private void makeMatchesPlan(int numPlayers) {
        // For 8 players
        if (numPlayers == 8) {
            matches.add(new Pair<>(0, 1));
            matches.add(new Pair<>(2, 3));
            matches.add(new Pair<>(4, 5));
            matches.add(new Pair<>(6, 7));
        }
        // For 4 players
        else if (numPlayers == 4) {
            matches.add(new Pair<>(0, 1));
            matches.add(new Pair<>(2, 3));
        }
        // For 2 players
        else if (numPlayers == 2) {
            matches.add(new Pair<>(0, 1));
        }
    }

    /**
     * @return the instance of the singleton class
     */
    public static TournamentPlayers getInstance() {
        if (instance == null) {
            instance = new TournamentPlayers();
        }
        return instance;
    }

    public void initFSPlayers() {
        System.out.println(tournamentPlayerCodes.get(matches.get(currentMatchIdx).getKey()) + " Size: " + tournamentPlayerCodes.size());
        FirstPlayer.initFields(tournamentPlayerCodes.get(matches.get(currentMatchIdx).getKey()));
        SecondPlayer.initFields(tournamentPlayerCodes.get(matches.get(currentMatchIdx).getValue()));
        System.out.println(tournamentPlayerCodes.get(matches.get(currentMatchIdx).getKey()));
    }

    /**
     * starts the next match in the tournament
     *
     * @param winner code of the winning player
     * @return true if the prev match was the last one. false otherwise.
     */
    public boolean initNextMatch(String winner) {
        winners.add(winner);
        currentMatchIdx++;
        if (currentMatchIdx >= matches.size()) {
            currentMatchIdx = 0;
            if (currentPlayersCount == 2)
                return true;
            // reset the instance
            instance = null;
            // update the number of players for the next round
            currentPlayersCount /= 2;
            // create a new instance
            getInstance();
        }
        // init the players for the next match
        initFSPlayers();
        return false;
    }
}
