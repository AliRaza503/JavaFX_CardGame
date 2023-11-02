package com.example.spacca.controller;

import com.example.spacca.EntryPoint;
import com.example.spacca.data.*;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.*;
import java.util.ArrayList;
import java.util.Optional;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

import static com.example.spacca.data.Players.*;

public class AdminUIController {
    private static final int MINIMUM_NUMBER_OF_CARDS = 4;
    private static final int MAXIMUM_NUMBER_OF_CARDS = 24;

    @FXML
    private ImageView configBtn;
    @FXML
    private ScrollPane playerDetailsContainer;
    @FXML
    private VBox rootBox;
    @FXML
    private TextField playerNameField;
    private String gMode = null;
    private static final Logger logger = Logger.getLogger(EntryPoint.class.getName());


    private static final String COFIG_FILE_NAME = "config_data.csv";


    /**
     * Initializes the controller class. <br>
     * Calls the helper methods to read the player data and load the configurations.
     */
    @FXML
    private void initialize() {
        // reset the player object
        Players.makeList();
        readPlayerDataFromFile();
        showUpdatedPlayersList();
        loadConfig();
        configBtn.setOnMouseClicked(event -> displayConfigurationsDialog());
    }

    /**
     * Constructs and displays the configurations dialog. <br>
     * The dialog contains the following fields: <br>
     * 1. Game Mode <br>
     * 2. Number of Players <br>
     * 3. Number of Cards per Player <br>
     * The dialog also contains two buttons: <br>
     * 1. Apply
     * 2. Cancel <br>
     * The Apply button is used to apply the changes (and save to the file) and the Cancel button is used to cancel the changes. <br>
     * The field values are validated before applying the changes. <br>
     * The Game Mode field is a ComboBox with the following options: <br>
     * 1. Duel 2. Tournament 3. Robot <br>
     * The Number of Players field is a TextField with the following options: <br>
     * 2, 4, 8 <br>
     * The Number of Cards per Player field is a TextField which can contain any number between 4 and 24. <br>
     */

    private void displayConfigurationsDialog() {
        Dialog<String> dialog = new Dialog<>();
        dialog.setTitle("Configurations");
        // Set the button types (OK and Cancel)
        ButtonType applyButtonType = new ButtonType("Apply", ButtonBar.ButtonData.APPLY);
        dialog.getDialogPane().getButtonTypes().addAll(applyButtonType, ButtonType.CANCEL);
        VBox content = new VBox();
        content.setSpacing(10);
        // Game Mode
        Label gameModeLabel = new Label("Game Mode");
        // gameMode ComboBox
        ComboBox<String> gameModeComboBox = new ComboBox<>();
        gameModeComboBox.getItems().addAll("Duel", "Tournament", "Robot"); // Add more game modes if needed
        gameModeComboBox.setValue(Configurations.gameModeString());
        content.getChildren().addAll(gameModeLabel, gameModeComboBox);
        // Number of Players
        Label numPlayersLabel = new Label("Number of Players");
        TextField numPlayersField = new TextField(String.valueOf(Configurations.numberOfPlayers));
        if (Configurations.gameModeString().contains("Duel")) {
            numPlayersField.setDisable(true);
        }
        numPlayersField.setPromptText("Number of players 4, 8");
        content.getChildren().addAll(numPlayersLabel, numPlayersField);
        // Number of Cards per Player
        Label numCardsLabel = new Label("Number of Cards per Player");
        TextField numCardsField = new TextField(String.valueOf(Configurations.numberOfCardsPerPlayer));
        numCardsField.setPromptText("Number of cards from 8 to 20");
        content.getChildren().addAll(numCardsLabel, numCardsField);
        dialog.getDialogPane().setContent(content);
        // Add a listener to the gameMode ComboBox to handle the validation
        gameModeComboBox.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.contains("Duel")) {
                numPlayersField.setText("2");
                numPlayersField.setDisable(true);
            } else if (newValue.contains("Tournament")) {
                numPlayersField.setDisable(false);
            }
            gMode = Configurations.getVarName(newValue);
        });
        // Set the result converter to return the edited values when the Apply button is clicked
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == applyButtonType) {
                int numCards = Integer.parseInt(numCardsField.getText());
                if (numCards < MINIMUM_NUMBER_OF_CARDS || numCards > MAXIMUM_NUMBER_OF_CARDS) {
                    numCardsField.setText(String.valueOf(Configurations.numberOfCardsPerPlayer));
                    // Do not exit dialog
                    return null;
                }
                int numPs = Integer.parseInt(numPlayersField.getText());
                if (numPs != 2 && numPs != 4 && numPs != 8) {
                    numPlayersField.setText(String.valueOf(Configurations.numberOfPlayers));
                    // Do not exit the dialog
                    return null;
                }
                // Return comma separated values gameMode, numPlayers, numCards
                return gMode + "," + numPlayersField.getText() + "," + numCardsField.getText();
            }
            return null;
        });
        // Show the dialog and wait for a response
        Optional<String> result = dialog.showAndWait();
        if (result.isPresent()) {
            // Split the result and set the configurations
            String[] values = result.get().split(",");
            String gameMode = values[0];
            String numPlayers = values[1];
            String numCards = values[2];
            // Set the configurations based on the game mode
            switch (gameMode) {
                case "T" -> {
                    Configurations.gameMode = Configurations.GameMode.Tournament;
                    int numPs = Integer.parseInt(numPlayers);
                    if (numPs < 4) {
                        numPs = 4;
                    }
                    Configurations.numberOfPlayers = numPs;
                }
                case "R" -> {
                    Configurations.gameMode = Configurations.GameMode.Robot;
                    Configurations.numberOfPlayers = 2;
                }
                default -> {
                    Configurations.gameMode = Configurations.GameMode.Duel;
                    Configurations.numberOfPlayers = 2;
                }
            }
            // Set the number of cards
            Configurations.numberOfCardsPerPlayer = Integer.parseInt(numCards);
            // Save the configurations to the file
            addConfigs();
            System.out.println("Game Mode: " + gameMode);
            System.out.println("Number of Players: " + numPlayers);
        }
    }

    /**
     * Reads the configurations data from the file. <br>
     * If the file does not exist, create a new file and write the default configurations to the file. <br>
     * The file stores the configurations in the following format: <br>
     * GameMode,D\n <br>
     * NumberOfPlayers,2\n <br>
     * NumberOfCardsPerPlayer,8\n <br>
     */
    private void loadConfig() {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(COFIG_FILE_NAME));
            String line;
            while ((line = reader.readLine()) != null) {
                String[] data = line.split(",");
                if (data[0].equals("GameMode")) {
                    // D, T, RPD, RPT
                    switch (data[1]) {
                        // Tournament
                        case "T" -> Configurations.gameMode = Configurations.GameMode.Tournament;
                        // Robot Player Duel
                        case "R" -> Configurations.gameMode = Configurations.GameMode.Robot;
                        default -> Configurations.gameMode = Configurations.GameMode.Duel;
                    }
                }
                // Next line is the number of players
                if (data[0].equals("NumberOfPlayers")) {
                    Configurations.numberOfPlayers = Integer.parseInt(data[1]);
                }
                // Next line is the number of cards per player
                if (data[0].equals("NumberOfCardsPerPlayer")) {
                    Configurations.numberOfCardsPerPlayer = Integer.parseInt(data[1]);
                }
            }
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Error reading the file: " + e);   // Log the error
            // If the file does not exist, create a new file
            File file = new File(COFIG_FILE_NAME);
            try {
                if (file.createNewFile()) {
                    // Write the default configurations to the file
                    logger.log(Level.INFO, "File created: " + file.getName());   // Log the info
                    Configurations.gameMode = Configurations.GameMode.Duel;
                    Configurations.numberOfPlayers = 2;
                    Configurations.numberOfCardsPerPlayer = 12;
                    addConfigs();
                }
            } catch (IOException ex) {
                logger.log(Level.SEVERE, "Error creating the file: " + ex);   // Log the error
            }
        }
    }

    /**
     * Writes the configurations data to the file. <br>
     * The file name is stored in CONFIG_FILE_NAME constant.
     */
    private void addConfigs() {
        BufferedWriter writer;
        try {
            writer = new BufferedWriter(new FileWriter(COFIG_FILE_NAME));
            writer.write("GameMode,");
            switch (Configurations.gameMode) {
                case Duel -> writer.write("D");
                case Tournament -> writer.write("T");
                case Robot -> writer.write("R");
            }
            writer.newLine();
            writer.write("NumberOfPlayers,");
            writer.write(String.valueOf(Configurations.numberOfPlayers));
            writer.newLine();
            writer.write("NumberOfCardsPerPlayer,");
            writer.write(String.valueOf(Configurations.numberOfCardsPerPlayer));
            writer.close();
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Error writing to the file: " + e);   // Log the error
        }
    }

    /**
     * Add player button action listener <br>
     * Adds the player to the list of players and updates the UI
     */
    @FXML
    private void addPlayer() {
        String playerName = playerNameField.getText().trim();
        if (!playerName.isEmpty()) {
            // Updating the list
            Players.addPlayer(playerName);
            // Clearing the UI
            playerNameField.clear();
            // Updating the UI
            showUpdatedPlayersList();
        }
    }

    /**
     * Show updated players list in the UI <br>
     * This method is called whenever the player list is updated
     * and the UI needs to be updated<br>
     * This method creates a VBox for each player and adds it to the ScrollPane.<br>
     * Then in each VBox there are three VBoxes for player name, player code and player points.<br>
     * Only the player name can be edited.<br>
     */
    @FXML
    private void showUpdatedPlayersList() {
        VBox outerMostContainer = new VBox();
        for (int i = 0; i < allPlayers.size(); i++) {
            HBox container = new HBox();
            container.setStyle("-fx-background-color: #fff; -fx-border-color: #999;");
            // Add three VBoxes to the container

            // Player name box
            VBox pNameBox = new VBox();
            TextField pNameTF = new TextField();
            pNameTF.setText(allPlayers.get(i));
            pNameBox.getChildren().add(pNameTF);
            pNameTF.setStyle("-fx-alignment: CENTER;");
            // On edit listener
            pNameTF.textProperty().addListener(((observable, oldValue, newValue) -> Players.updatePName(oldValue, newValue)));

            //Player code box
            VBox pCodeBox = new VBox();
            TextField pCodeTF = new TextField();
            pCodeTF.setText(allPlayerCodes.get(i));
            // Making it non editable
            pCodeTF.setEditable(false);
            pCodeTF.setStyle("-fx-alignment: CENTER;");
            pCodeBox.getChildren().add(pCodeTF);

            // Player points box
            VBox pPointsBox = new VBox();
            TextField pPointsTF = new TextField();
            pPointsTF.setText(String.valueOf(allPlayerPoints.get(i)));
            pPointsTF.setStyle("-fx-alignment: CENTER;");
            pPointsTF.setEditable(false);
            pPointsBox.getChildren().add(pPointsTF);
            container.getChildren().addAll(pNameBox, pCodeBox, pPointsBox);
            outerMostContainer.getChildren().add(container);
        }
        playerDetailsContainer.setContent(outerMostContainer);
    }

    /**
     * Creates a new game session.<br>
     * This method is called when the admin clicks on the "Create Game Session" button.<br>
     * This method does the following:<br>
     * 1. If the game mode is Tournament, prompt the admin to enter the player names. Until all the player names are entered.<br>
     * 2. If the game mode is Duel, prompt the admin to enter the player names. Until both the player names are entered.<br>
     * 3. If the game mode is Robot, prompt the admin to enter the player name. Until the player name is entered.<br>
     * 4. Generate a unique session code and display it to the admin.<br>
     * 5. Close the window.<br>
     * If at any point the admin clicks on the cancel button, or do not enter the valid player code, the method returns without doing anything.<br>
     */
    @FXML
    private void createGameSession() {
        // Prompt admin to enter the player names
        if (Configurations.gameMode == Configurations.GameMode.Tournament) {
            TournamentPlayers players = TournamentPlayers.getInstance();
            players.tournamentPlayerCodes = new ArrayList<>();
            // Init all the players
            for (int i = 0; i < Configurations.numberOfPlayers; i++) {
                Optional<String> res = promptForPlayerCode(i + 1);
                if (res.isEmpty() || isInvalidCode(res.get())) {
                    players.tournamentPlayerCodes = new ArrayList<>();
                    return;
                }
                players.tournamentPlayerCodes.add(res.get());
            }
            // Init the First and second players
            players.initFSPlayers();

        } else if (Configurations.gameMode == Configurations.GameMode.Duel) {
            if (!getBothPlayers()) {
                return;
            }
        } else if (Configurations.gameMode == Configurations.GameMode.Robot) {
            // Player 2 is robot
            if (!firstPlayerInit()) {
                return;
            }
            SecondPlayer.code = "XXXX";
            SecondPlayer.name = "Robot";
        }

        // Generate a unique session code and display it to the admin
        generateCodeAndDisplay();

        // Close the window
        Stage stage = (Stage) rootBox.getScene().getWindow();
        stage.close();
    }

    /**
     * Generates a random string of length 6 and displays it to the admin.<br>
     * Without this string the players cannot join the game session.<br>
     */
    private void generateCodeAndDisplay() {
        Configurations.uniqueSessionCode = generateRandomString(6);
        // Create and configure an alert dialog
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Session Creation");
        alert.setHeaderText("Success!");
        alert.setContentText("Here is your unique code for joining the session. " + Configurations.uniqueSessionCode);

        // Show the dialog
        alert.showAndWait();
    }

    /**
     * helper method to generate a random string of given length
     * @param length length of the string
     * @return random string of given length
     */
    private String generateRandomString(int length) {
        // Function to generate a random string of given length
        String characters = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        Random random = new Random();
        StringBuilder stringBuilder = new StringBuilder();

        for (int i = 0; i < length; i++) {
            int randomIndex = random.nextInt(characters.length());
            char randomChar = characters.charAt(randomIndex);
            stringBuilder.append(randomChar);
        }

        return stringBuilder.toString();
    }

    /**
     * Checks if the given code is valid or not
     * @param s code to check
     * @return true if the code is invalid, false otherwise
     */
    private boolean isInvalidCode(String s) {
        for (String allPlayerCode : allPlayerCodes) {
            if (s.equals(allPlayerCode)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Prompts the admin to enter the player code
     * @param i player number
     * @return the player code entered by the admin
     */
    private Optional<String> promptForPlayerCode(int i) {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Player's code");
        String headerText = "Enter code for " + i;
        if (i == 1) headerText += "st player";
        else if (i == 2) headerText += "nd player";
        else if (i == 3) headerText += "rd player";
        else headerText += "th player";
        dialog.setHeaderText(headerText);
        dialog.setContentText("Code: ");
        return dialog.showAndWait();
    }

    /**
     * Prompts the admin to enter the player codes for both the players
     * @return true if the player codes are entered valid, false otherwise
     */
    private boolean getBothPlayers() {
        boolean isAdded = firstPlayerInit();
        int idx;
        Optional<String> res = promptForPlayerCode(2);
        if (res.isEmpty() || isInvalidCode(res.get())) {
            return false;
        }
        SecondPlayer.code = res.get();
        idx = allPlayerCodes.indexOf(SecondPlayer.code);
        if (idx == -1) {
            SecondPlayer.code = null;
            return false;
        }
        SecondPlayer.name = allPlayers.get(idx);
        SecondPlayer.points = allPlayerPoints.get(idx);
        return isAdded;
    }

    /**
     * Prompts the admin to enter the player code for the first player
     * @return true if the player code is entered valid, false otherwise
     */
    private boolean firstPlayerInit() {
        Optional<String> res = promptForPlayerCode(1);
        if (res.isEmpty() || isInvalidCode(res.get()))
            return false;
        // Init the first player
        FirstPlayer.code = res.get();
        int idx = allPlayerCodes.indexOf(FirstPlayer.code);
        if (idx == -1) {
            // Invalid code entered by the admin
            FirstPlayer.code = null;
            return false;
        }
        FirstPlayer.name = allPlayers.get(idx);
        FirstPlayer.points = allPlayerPoints.get(idx);
        return true;
    }

    /**
     * discard button action listener <br>
     */
    @FXML
    public void discardPlayersData() {
        Players.makeList();
        Players.writePlayerDataToFile();
    }
}
