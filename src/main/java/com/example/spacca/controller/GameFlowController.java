package com.example.spacca.controller;

import com.example.spacca.CardTable;
import com.example.spacca.data.*;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.Objects;

import static com.example.spacca.data.Cards.cardSelectedInLastTurn;
import static com.example.spacca.data.Cards.cardsPerPlayer;

public class GameFlowController {
    @FXML
    private Button pauseResumeButton;
    @FXML
    private BorderPane container;
    @FXML
    private Button endTurnPlayer1Button;
    @FXML
    private Button endTurnPlayer2Button;
    @FXML
    private VBox player2;
    @FXML
    private VBox player1;
    @FXML
    private Label player2Pts;
    @FXML
    private Label player1Pts;
    private int turnsEnded = 0;
    public static GameFlowController gameFlowController;
    public static Turn turn = Turn.FIRST_PLAYER;
    @FXML
    private Label player1NameLabel;
    @FXML
    private ImageView player1Cards;
    @FXML
    private Label player2NameLabel;
    @FXML
    private ImageView player2Cards;
    @FXML
    private Label playerName;

    @FXML
    private Label player1CardCountLabel;

    @FXML
    private Label player2CardCountLabel;

    @FXML
    private Button drawCardPlayer1Button;

    @FXML
    private Button drawCardPlayer2Button;
    @FXML
    private ImageView discardPileTopCard;

    // Variable to store the game state (either paused or resumed)
    private boolean statePaused = false;

    /**
     * Swap the turn of the players
     */
    public static void swapTurn() {
        turn = GameFlowController.turn == Turn.FIRST_PLAYER ? Turn.SECOND_PLAYER : Turn.FIRST_PLAYER;
        gameFlowController.turnsEnded = 0;
    }

    /**
     * Initialize the game flow controller <br>
     * Automatically called by JavaFX after loading the FXML file <br>
     * This function is used to initialize and re-initialize the game flow controller
     */
    public void initialize() {
        // Console printout for debugging
        System.out.println(turn);
        // Initializing the static reference to be used as singleton class.
        gameFlowController = this;
        if (turn == Turn.SECOND_PLAYER) {
            changeBehaviorWithTurn(player1Cards, player2, player1, drawCardPlayer1Button, endTurnPlayer1Button);
        } else if (turn == Turn.FIRST_PLAYER) {
            changeBehaviorWithTurn(player2Cards, player1, player2, drawCardPlayer2Button, endTurnPlayer2Button);
        }

        // Check the winner
        if (FirstPlayer.remainingCards == 0) {
            gameResults(1);
        } else if (SecondPlayer.remainingCards == 0) {
            gameResults(2);
        }

        // Set up event handlers for buttons, update labels, etc.
        setContents();

        // Set up event handler for draw card button of player 1
        drawCardPlayer1Button.setOnAction(event -> {
            // Handle drawing a card for Player 1
            // Prompt the user for input and process the card
            if (turn == Turn.FIRST_PLAYER) {
                CardTable cardTable = new CardTable();
                try {
                    cardTable.start(new Stage());

                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }
            }
        });

        // Randomly initialize the first player and second player cards
        initAllCards();

        // Set up event handler for draw card button of player 2
        drawCardPlayer2Button.setOnAction(event -> {
            // Handle drawing a card for Player 2
            // Prompt the user for input and process the card
            // Update player2CardCountLabel
            if (turn == Turn.SECOND_PLAYER) {
                CardTable cardTable = new CardTable();
                try {
                    cardTable.start(new Stage());
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }
            }
        });
        // End turn action listeners
        endTurnPlayer1Button.setOnAction(event -> endTurnPlayer1());
        endTurnPlayer2Button.setOnAction(event -> endTurnPlayer2());

        // If the second player is a robot player
        if (Configurations.gameMode == Configurations.GameMode.Robot && SecondPlayer.robotHasValidMoves) {
            // Wait here for 3 seconds
            if (turn == Turn.SECOND_PLAYER) {
                Task<Void> task = taskToPerformClick();

                new Thread(task).start(); // Start the task in a separate thread
            }
        } else if (!SecondPlayer.robotHasValidMoves) {
            endTurnPlayer2Button.fire();
        }
    }

    /**
     * Change the behavior of the game flow with the turn of the player <br>
     * This function is written just to avoid code repetitions. <br>
     * player1 here, is not necessarily the first player, but the player whose turn is to be set active and highlighted.
     *
     * @param player2Cards          the image view of the player 2 cards
     * @param player1               the vbox of the player 1
     * @param player2               the vbox of the player 2
     * @param drawCardPlayer2Button the draw card button of the player 2
     * @param endTurnPlayer2Button  the end turn button of the player 2
     */
    private void changeBehaviorWithTurn(ImageView player2Cards, VBox player1, VBox player2, Button drawCardPlayer2Button, Button endTurnPlayer2Button) {
        if (cardSelectedInLastTurn != null)
            player2Cards.setImage(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/com/example/spacca/cards/" + cardSelectedInLastTurn + ".png"))));
        player1.setStyle("-fx-alignment: CENTER; -fx-padding: 50; -fx-background-color: rgba(18,52,68,0.5)");
        player2.setStyle("-fx-alignment: CENTER; -fx-padding: 50; -fx-background-color: rgba(18,52,68,0.2)");
        enableButtons();
    }

    /**
     * Function to perform a click on the draw card button after 3 seconds <br>
     * Used by the robot to play the game and trigger events.
     *
     * @return the task to perform
     */
    private Task<Void> taskToPerformClick() {
        // Create a task to perform
        Task<Void> task = new Task<>() {
            @Override
            protected Void call() throws Exception {
                Thread.sleep(3000); // Wait for 3 seconds (3000 milliseconds)
                return null;
            }
        };
        // Set the action to perform after the delay
        task.setOnSucceeded(event -> {
            // This code will run after the delay
            Platform.runLater(() -> {
                // Robot player is always the second player (turn == Turn.SECOND_PLAYER)
                drawCardPlayer2Button.fire(); // Trigger the button click event
            });
        });
        return task;
    }

    /**
     * Function to set and update the contents of the labels
     * Calls helper functions to set points, discard pile image, player name, and player cards count
     */
    public void setContents() {
        player1NameLabel.setText(FirstPlayer.name);
        player2NameLabel.setText(SecondPlayer.name);
        setPoints();
        setDiscardPileImage();
        setPlayerName();
        setPlayerCardsCount();
    }

    /**
     * Set and update the points of the players
     */
    private void setPoints() {
        player1Pts.setText("Points: " + FirstPlayer.points);
        player2Pts.setText("Points: " + SecondPlayer.points);
    }

    /**
     * Set the name of the current player in the top-center of the screen
     */
    private void setPlayerName() {
        if (turn == Turn.FIRST_PLAYER) {
            playerName.setText(FirstPlayer.name);
        } else {
            playerName.setText(SecondPlayer.name);
        }
    }

    /**
     * Set and update the cards count of the players
     */
    private void setPlayerCardsCount() {
        player1CardCountLabel.setText("Cards Count: " + FirstPlayer.remainingCards);
        player2CardCountLabel.setText("Cards Count: " + SecondPlayer.remainingCards);
    }

    /**
     * Set the discard pile image (the top card of the discard pile)
     */
    private void setDiscardPileImage() {
        try {
//            String workingDirectory = System.getProperty("user.dir");
//            System.out.println("Current working directory: " + workingDirectory);
            // Load the image using a relative path
            String imagePath = "/com/example/spacca/cards/" + Cards.discardPileCard + ".png";
            Image image = new Image(Objects.requireNonNull(getClass().getResourceAsStream(imagePath)));
            discardPileTopCard.setImage(image);
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    /**
     * Use hyper threading to initialize the cards of the first player and second player
     */
    private void initAllCards() {
        if (FirstPlayer.cards != null && SecondPlayer.cards != null) {
            return;
        }
        // Using multiple threads to initialize the cards
        try {
            Thread initFirstPlayerCardsThread = new Thread(this::initFirstPlayerCards);
            Thread initSecondPlayerCardsThread = new Thread(this::initSecondPlayerCards);
            initFirstPlayerCardsThread.start();
            initSecondPlayerCardsThread.start();
        } catch (Exception e) {
            System.out.println("Error initializing cards: " + e.getMessage());
        }
    }

    /**
     * Initialize the cards of the first player randomly (Thread 1) <br>
     * The card repetition is allowed
     */
    private void initFirstPlayerCards() {
        // initialize all the n cards of first player
        FirstPlayer.cards = new String[cardsPerPlayer];
        for (int i = 0; i < cardsPerPlayer; i++) {
            // Get a random card name index from 0 to Cards.cardNames.length - 1
            int cardNameIdx = (int) (Math.random() * Cards.cardNames.length);
            // Get the card name from Cards.cardNames using the cardNameIdx
            FirstPlayer.cards[i] = Cards.cardNames[cardNameIdx];
        }
    }


    /**
     * Initialize the cards of the second player randomly (Thread 2)
     */
    private void initSecondPlayerCards() {
        // initialize all the n cards of second player
        SecondPlayer.cards = new String[cardsPerPlayer];
        for (int i = 0; i < cardsPerPlayer; i++) {
            // Get a random card name index from 0 to Cards.cardNames.length - 1
            int cardNameIdx = (int) (Math.random() * Cards.cardNames.length);
            // Get the card name from Cards.cardNames using the cardNameIdx
            SecondPlayer.cards[i] = Cards.cardNames[cardNameIdx];
        }
    }

    /**
     * End the turn of the first player (Click listener for first player's end turn button)
     */
    public void endTurnPlayer1() {
        if (turn == Turn.FIRST_PLAYER) {
            turn = Turn.SECOND_PLAYER;
            turnsEnded++;
            if (turnsEnded >= 4) {
                // Init again the discard pile card
                discardPileTopCard = null;
            }
            cardSelectedInLastTurn = null;
            initialize();
        }
    }

    /**
     * End the turn of the second player (Click listener for second player's end turn button)
     */
    public void endTurnPlayer2() {
        if (turn == Turn.SECOND_PLAYER) {
            turn = Turn.FIRST_PLAYER;
            turnsEnded++;
            if (turnsEnded >= 4) {
                // Init again the discard pile card
                discardPileTopCard = null;
            }
            cardSelectedInLastTurn = null;
            initialize();
        }
    }

    /**
     * Show the game results
     *
     * @param r the result of the game <br>
     *          r = 1 -> First player wins <br>
     *          r = 2 -> Second player wins
     */
    private void gameResults(int r) {
        // Construct a dialog box and wait
        Alert alert = getAlert(r);
        alert.showAndWait();
        // Save the data to the file
        saveToFile();
        if (Configurations.gameMode == Configurations.GameMode.Tournament) {
            String winnerCode = r == 1 ? FirstPlayer.code : SecondPlayer.code;
            if (TournamentPlayers.getInstance().initNextMatch(winnerCode)) {
                // Save the data in the file and close the window
                Stage stage = (Stage) container.getScene().getWindow();
                stage.close();
                System.exit(0);
            }
        } else {
            restart();
        }
    }


    /**
     * Get the alert dialog box
     *
     * @param r the result of the game
     * @return the alert dialog box
     */
    private static Alert getAlert(int r) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Leaderboard");
        alert.setHeaderText("Finished");
        String text = "Player 1: " + FirstPlayer.remainingCards + "\nPlayer 2: " + SecondPlayer.remainingCards;
        if (r == 1) {
            text += "\n\n" + FirstPlayer.name + " wins!";
        } else if (r == 2) {
            text += "\n\n" + SecondPlayer.name + " wins!";
        }
        alert.setContentText(text);
        return alert;
    }


    /**
     * Save the updated points to the file.
     */
    private void saveToFile() {
        Players.updatePPoints(FirstPlayer.points, FirstPlayer.code);
        if (Configurations.gameMode != Configurations.GameMode.Robot)
            Players.updatePPoints(SecondPlayer.points, SecondPlayer.code);
    }

    /**
     * Restart the game
     */
    private void restart() {
        FirstPlayer.remainingCards = cardsPerPlayer;
        SecondPlayer.remainingCards = cardsPerPlayer;
        FirstPlayer.cards = null;
        SecondPlayer.cards = null;
        cardSelectedInLastTurn = null;
        Cards.discardPileCard = Cards.cardNames[(int) (Math.random() * Cards.cardNames.length)];
        turnsEnded = 0;

    }

    /**
     * Pause or Resume Button click listener
     */
    @FXML
    private void PauseResumeGame() {
        statePaused = !statePaused;
        if (statePaused) {
            // Disable buttons
            disableButtons();
            pauseResumeButton.setText("Resume");
        } else {
            // enable buttons
            enableButtons();
            pauseResumeButton.setText("Pause");
        }
    }

    /**
     * Enable only the current player's buttons and disable the other player's buttons
     */
    private void enableButtons() {
        if (turn == Turn.FIRST_PLAYER) {
            endTurnPlayer1Button.setDisable(false);
            drawCardPlayer1Button.setDisable(false);
            endTurnPlayer2Button.setDisable(true);
            drawCardPlayer2Button.setDisable(true);
        } else if (turn == Turn.SECOND_PLAYER) {
            endTurnPlayer2Button.setDisable(false);
            drawCardPlayer2Button.setDisable(false);
            endTurnPlayer1Button.setDisable(true);
            drawCardPlayer1Button.setDisable(true);
        }
        container.setStyle("-fx-background-color: #f0f0f0;");

    }


    /**
     * Disable all the buttons
     */
    private void disableButtons() {
        endTurnPlayer1Button.setDisable(true);
        endTurnPlayer2Button.setDisable(true);
        drawCardPlayer1Button.setDisable(true);
        drawCardPlayer2Button.setDisable(true);
        container.setStyle("-fx-background-color: #333;");
    }
}
