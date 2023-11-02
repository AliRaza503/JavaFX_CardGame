package com.example.spacca;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;
import java.util.Optional;

public class CardGameAdministrator extends Application {
    private static final String ADMIN_USERNAME = "admin";
    private static final String ADMIN_PASSWORD = "password";
    private static boolean started = false;

    /**
     * The main entry point for all JavaFX applications. <br>
     * The start method is called after the init method has returned,<br>
     * and after the system is ready for the application to begin running.
     *
     * @param primaryStage the primary stage for this application, onto which
     *                     the application scene can be set.
     *                     Applications may create other stages, if needed, but they will not be
     *                     primary stages.
     */
    @Override
    public void start(Stage primaryStage) {
        boolean validLogin = false;
        while (!validLogin) {
            // Prompt for username and password
            Optional<String> usernameResult = showInputDialog("Enter Username:", "Administrator Login", "Username:", false);
            if (usernameResult.isEmpty()) {
                // User canceled the login, close the application
                return;
            }
            Optional<String> passwordResult = showInputDialog("Enter Password:", "Administrator Login", "Password:", true);
            if (passwordResult.isEmpty()) {
                // User canceled the login, close the application
                return;
            }
            // Validate the username and password here (e.g., compare with a predefined admin username and password).
            String username = usernameResult.get();
            String password = passwordResult.get();
            Parent root;
            if (isValidLogin(username, password)) {
                validLogin = true;
                // If valid login, load the admin interface
                try {
                    started = true;
                    root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("admin.fxml")));
                    primaryStage.setTitle("SPACCA: Administrator");
                    primaryStage.setScene(new Scene(root, 400, 400));
                    primaryStage.setMinHeight(400);
                    primaryStage.setMinWidth(400);

                    // On close request, set the started flag to false
                    primaryStage.setOnCloseRequest(e -> started = false);

                    primaryStage.show();
                } catch (IOException e) {
                    System.out.println("Error: " + e.getMessage());
                }

            } else {
                // Invalid login, show an error message
                showErrorMessage("Invalid Username or Password", "Login Error", "Please enter valid credentials.");
            }
        }
    }

    // Function to display a text input dialog
    private Optional<String> showInputDialog(String headerText, String title, String contentText, boolean isPassword) {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setHeaderText(headerText);
        dialog.setTitle(title);
        // Load and set your custom logo (logo.png) as the graphic in the dialog
        ImageView logoImageView = new ImageView(new Image(Objects.requireNonNull(getClass().getResourceAsStream("icons/logo.png"))));
        logoImageView.setFitHeight(50); // Adjust the height as needed
        logoImageView.setPreserveRatio(true);
        dialog.setGraphic(logoImageView);
        // For non-password input, use the standard TextInputDialog
        dialog.setContentText(contentText);
        if (isPassword) {
            // Create a PasswordField for password input
            PasswordField passwordField = new PasswordField();
            passwordField.setPromptText("Password");
            dialog.getDialogPane().setContent(passwordField);
            // Return the password input when the login button is clicked
            dialog.setResultConverter(dialogButton -> passwordField.getText());

        }
        return dialog.showAndWait();
    }

    // Function to display an error message dialog
    private void showErrorMessage(String message, String title, String contentText) {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(message);
        alert.setContentText(contentText);
        alert.showAndWait();
    }

    // Function to validate the username and password (modify as needed)
    private boolean isValidLogin(String username, String password) {
        // Replace this with your own logic to validate the username and password
        // For example, check if they match predefined admin credentials
        return username.equals(ADMIN_USERNAME) && password.equals(ADMIN_PASSWORD);
    }

    /**
     * Checks if the window is already open
     *
     * @return true if the window is already open, false otherwise
     */
    public boolean isShowing() {
        return CardGameAdministrator.started;
    }
}
