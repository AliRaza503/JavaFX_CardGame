module com.example.spacca {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.logging;


    opens com.example.spacca to javafx.fxml;
    exports com.example.spacca;
    opens com.example.spacca.controller to javafx.fxml; // Open the package containing your controller class
    exports com.example.spacca.controller; // Export the package containing your controller class
    exports com.example.spacca.data;
    opens com.example.spacca.data to javafx.fxml;
}