package com.example.spacca;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.stage.Stage;

import java.util.Objects;

public class CardTable extends Application {

    /**
     * The main entry point for all JavaFX applications.
     * @param primaryStage the primary stage for this application, onto which
     * the application scene can be set.
     * Applications may create other stages, if needed, but they will not be
     * primary stages.
     * @throws Exception
     */
    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("CardTable.fxml")));
        primaryStage.setTitle("Card Table");
        primaryStage.setScene(new javafx.scene.Scene(root, 800, 600));
        primaryStage.setMinHeight(400);
        primaryStage.setMinWidth(400);
        // Disable the close button
        // Set an event handler for the close request
        // Consume the event to prevent the window from closing
//        primaryStage.setOnCloseRequest(Event::consume);
        primaryStage.showAndWait();
    }

}
