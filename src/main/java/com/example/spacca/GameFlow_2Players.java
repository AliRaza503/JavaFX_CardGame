package com.example.spacca;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Screen;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

public class GameFlow_2Players extends Application {
    private static boolean started = false;
    @Override
    public void start(Stage primaryStage) throws Exception {

        try {
            Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("GameFlow_2Players.fxml")));
            primaryStage.setTitle("SPACCA: Game");
            // Get the screen size and set that size as window size
            Screen screen = Screen.getPrimary();
            double screenWidth = screen.getBounds().getWidth();
            double screenHeight = screen.getBounds().getHeight();
            Scene gameFlowScn = new Scene(root, screenWidth, screenHeight);
            primaryStage.setScene(gameFlowScn);
            primaryStage.setMinHeight(400);
            primaryStage.setMinWidth(400);
            // On close, exit the application
            primaryStage.setOnCloseRequest(e -> System.exit(0));
            // Set the window started flag to true
            started = true;
            primaryStage.show();
        } catch (IOException e) {
            System.out.println("Error: " + e.getMessage());
            throw e;
        }
    }

    /**
     * Checks if the window is already open
     * @return true if the window is already open, false otherwise
     */
    public boolean isShowing() {
        return GameFlow_2Players.started;
    }
}
