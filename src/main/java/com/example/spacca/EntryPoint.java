package com.example.spacca;

import com.example.spacca.data.Configurations;
import com.example.spacca.data.FirstPlayer;
import com.example.spacca.data.SecondPlayer;
import com.example.spacca.data.TournamentPlayers;
import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextInputDialog;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

import java.util.Objects;

public class EntryPoint extends Application {

    public Button playerBtn;
    public Button adminBtn;
    private static final Logger logger = Logger.getLogger(EntryPoint.class.getName());
    public ImageView helpBtn;

    /**
     * This method is called by the FXMLLoader when initialization is complete<br>
     * It sets the event handler for the help button<br>
     */
    @FXML
    public void initialize() {
        helpBtn.setOnMouseClicked(event -> {
            // Define the action you want to perform when the ImageView is clicked
            // For example, open a configuration window or perform any other action.
            System.out.println("Help button clicked");
            // Open a new window with the help information
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Help");
            alert.setHeaderText(null);
            // User guidance
            alert.setContentText("""
                    Here are the rules for the game:
                    1. The Administrator is responsible for initiating matches and providing unique codes to users.
                    2. Users must enter the assigned code to participate in the match.
                    3. The player who successfully depletes all the cards in their hand first will be declared the winner of the game.
                    4. Players are allowed to discard a card if it matches either in color, number, or symbol with the top card on the discard pile."""
            );
            alert.showAndWait();
        });
    }

    @Override
    public void start(Stage primaryStage) {
        try {
            Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("EntryPointDialog.fxml")));
            primaryStage.setTitle("SPACCA");
            primaryStage.setScene(new Scene(root, 400, 400));
            primaryStage.setMinHeight(400);
            primaryStage.setMinWidth(400);
            primaryStage.setOnCloseRequest(e -> System.exit(0));
            primaryStage.show();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }


    /**
     * Player button click event handler <br>
     * It checks if the game session is created and then starts the game<br>
     * If the game session is not created, it displays an error message<br>
     * If the game session is created, it prompts the user to enter the unique session code<br>
     * If the user enters the correct code, it starts the game<br>
     * If the window is already open, it does nothing<br>
     */
    @FXML
    private void playerBtnClickListener() {
        try {
            if (FirstPlayer.code == null || SecondPlayer.code == null || (
                    Configurations.gameMode == Configurations.GameMode.Tournament
                            && TournamentPlayers.getInstance().tournamentPlayerCodes.size() < Configurations.numberOfPlayers)) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText("Game session is not yet created!");
                alert.setContentText("Please wait for the administrator to start the game.");
                alert.showAndWait();
            } else {
                GameFlow_2Players gameFlow_2Players = new GameFlow_2Players();
                // Prompts the user to enter the unique session code
                TextInputDialog dialog = new TextInputDialog();
                dialog.setTitle("Session code prompt");
                dialog.setHeaderText("Enter the unique session code");
                dialog.setContentText("Enter the code or ask the admin to generate one for you");
                Optional<String> res = dialog.showAndWait();
                if (res.isPresent()) {
                    if (res.get().equals(Configurations.uniqueSessionCode)) {
                        // Check if the window is already open
                        if (!gameFlow_2Players.isShowing()) {
                            gameFlow_2Players.start(new Stage());
                        }
                    }
                }
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error occurred while handling player button click", e);
            // You can also display an error message to the user if needed
        }
    }

    /**
     * Admin button click event handler <br>
     * It starts the admin window<br>
     * If the admin window is already open, it does nothing<br>
     */
    @FXML
    private void adminBtnClickListener() {
        try {
            CardGameAdministrator cardGameAdministrator = new CardGameAdministrator();
            if (!cardGameAdministrator.isShowing()) {
                cardGameAdministrator.start(new Stage());
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error occurred while handling admin button click", e);
            // You can also display an error message to the user if needed
        }
    }

    /**
     * The main entry point for all JavaFX applications.
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
}
