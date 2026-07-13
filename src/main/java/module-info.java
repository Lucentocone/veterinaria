module com.unam {
    requires javafx.controls;
    requires javafx.fxml;

    requires transitive javafx.graphics;

    opens com.unam.controller to javafx.fxml;
    exports com.unam.controller;
}