package com.example.spacca.controller;

import com.example.spacca.data.Configurations;
import com.example.spacca.data.Cards;
import com.example.spacca.data.FirstPlayer;
import com.example.spacca.data.SecondPlayer;
import com.example.spacca.data.Turn;
import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.stage.Stage;

import java.util.Objects;

public class CardTableController {
    @FXML
    private FlowPane container;
    // Will be used for setting the event listeners
    private ImageView[] cardImages;
    private String[] cardNames;

    /**
     * This method is called when the window is opened.<br>
     * It displays the cards of the current player.
     */
    public void initialize() {
        // Styling the container
        container.alignmentProperty().setValue(javafx.geometry.Pos.CENTER);
        container.setHgap(15);
        container.setVgap(10);
        // Checking the turn
        if (GameFlowController.turn == Turn.FIRST_PLAYER) {
            // Creating the card images array with the number of cards the first player has
            cardImages = new ImageView[FirstPlayer.remainingCards];
            cardNames = new String[FirstPlayer.remainingCards];
            // load the card images for the first player.
            for (int i = 0; i < FirstPlayer.remainingCards; i++) {
                String imagePath = "/com/example/spacca/cards/" + FirstPlayer.cards[i] + ".png";
                System.out.print(FirstPlayer.cards[i] + " ");
                // Initializing the array elements
                cardNames[i] = FirstPlayer.cards[i];
                // Setting the image
                setImage(i, imagePath);
            }
        } else {
            // Creating the card images array with the number of cards the second player has
            cardImages = new ImageView[SecondPlayer.remainingCards];
            cardNames = new String[SecondPlayer.remainingCards];
            // load the card images for the second player.
            for (int i = 0; i < SecondPlayer.remainingCards; i++) {
                String imagePath = "/com/example/spacca/cards/" + SecondPlayer.cards[i] + ".png";
                System.out.print(SecondPlayer.cards[i] + " ");
                // Initializing the array elements
                cardNames[i] = SecondPlayer.cards[i];
                // Setting the image
                setImage(i, imagePath);
            }
        }
        setClickListeners();
        // if it is a robot player mode click on the matching card
        if (Configurations.gameMode == Configurations.GameMode.Robot && GameFlowController.turn == Turn.SECOND_PLAYER) {
            // Click on the most appropriate card (INTELLIGENT ROBOT MOVES)
            int maxPointsIdx = 0, pts = 0;
            for (int i = 0; i < cardNames.length; i++) {
                Cards.cardSelectedInLastTurn = cardNames[i];
                int res = getPoints();
                if (res > pts) {
                    pts = res;
                    maxPointsIdx = i;
                }
            }
            // Calling the helper methods
            updatePoints(pts);
            userClicked(maxPointsIdx);
            // Force Close the window
            Stage stage = (Stage) container.getScene().getWindow();
            stage.close();
            SecondPlayer.robotHasValidMoves = false;
        }
    }

    /**
     * Helper method to set the image of the card
     * @param i index of the card.
     * @param imagePath path of the image.
     */
    private void setImage(int i, String imagePath) {
        Image img = new Image(Objects.requireNonNull(getClass().getResourceAsStream(imagePath)));
        ImageView imgV = new ImageView(img);
        // Setting the dimens of the image view
        imgV.setFitHeight(150);
        imgV.setFitWidth(100);
        // Preserve the ratio of the image in the image view
        imgV.preserveRatioProperty().setValue(true);
        cardImages[i] = imgV;
        container.getChildren().add(imgV);
    }

    /**
     * Helper method to set the click listeners on the cards.
     */
    private void setClickListeners() {
        for (int i = 0; i < cardImages.length; i++) {
            int finalI = i;
            cardImages[i].setOnMouseClicked(event -> {
                Cards.cardSelectedInLastTurn = cardNames[finalI];
                // Calling the helper methods
                updatePoints(getPoints());
                userClicked(finalI);
            });
        }
    }

    /**
     * Helper method to update the points of the current player.
     * @param points points to be added to the current player.
     */
    private void updatePoints(int points) {
        if (GameFlowController.turn == Turn.FIRST_PLAYER) {
            FirstPlayer.points += points;
        } else {
            SecondPlayer.points += points;
        }
    }

    /**
     * Helper method to handle the user click on the card.
     * @param finalI index of the card.
     */
    private void userClicked(int finalI) {
        removeCardFromList(finalI);
        Cards.discardPileCard = cardNames[finalI];
        // Change the turn
        GameFlowController.swapTurn();
        GameFlowController.gameFlowController.initialize();
        // Force Close the window
        Stage stage = (Stage) container.getScene().getWindow();
        stage.close();
    }

    /**
     * Helper method to remove the card from the list of the current player.
     * @param idx index of the card.
     */
    private void removeCardFromList(int idx) {
        if (GameFlowController.turn == Turn.FIRST_PLAYER) {
            FirstPlayer.remainingCards--;
            // Remove the card from the first player cards
            for (int j = idx; j < FirstPlayer.remainingCards; j++) {
                FirstPlayer.cards[j] = FirstPlayer.cards[j + 1];
            }
        } else {
            SecondPlayer.remainingCards--;
            // Remove the card from the second player cards
            for (int j = idx; j < SecondPlayer.remainingCards; j++) {
                SecondPlayer.cards[j] = SecondPlayer.cards[j + 1];
            }
        }
    }

    /**
     * Helper method to get the points of the current player.<br>
     * If the card's number matches with the discard pile card then add 1 point.<br>
     * If the card's color matches with the discard pile card then add 1 (more) point.<br>
     * If the card's symbol matches with the discard pile card then add 1 (more) point.<br>
     * @return points of the current player.
     */
    private int getPoints() {
        int points = 0;
        // Check what parameters match with that of the discard pile card
        if (Cards.cardSelectedInLastTurn.charAt(0) == Cards.discardPileCard.charAt(0)) {
            points++;
        }
        if (Cards.cardSelectedInLastTurn.charAt(1) == Cards.discardPileCard.charAt(1)) {
            points++;
        }
        if (Cards.cardSelectedInLastTurn.charAt(2) == Cards.discardPileCard.charAt(2)) {
            points++;
        }
        // If nothing matches then subtract 2 points
        if (Cards.cardSelectedInLastTurn.charAt(0) != Cards.discardPileCard.charAt(0)
                && Cards.cardSelectedInLastTurn.charAt(1) != Cards.discardPileCard.charAt(1)
                && Cards.cardSelectedInLastTurn.charAt(2) != Cards.discardPileCard.charAt(2)) {
            points -= 2;
        }
        return points;
    }
}
