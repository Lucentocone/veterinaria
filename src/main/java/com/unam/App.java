package com.unam;

import com.unam.init.DataInitializer;
import com.unam.persistence.PersistenceManager;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * Punto de entrada de la aplicación JavaFX.
 */
public class App extends Application {

    private static Scene scene;

    @Override
    public void start(Stage stage) throws IOException {
        DataInitializer.inicializarSiCorresponde();

        scene = new Scene(loadFXML("mainmenu"), 1100, 700);
        scene.getStylesheets().add(App.class.getResource("app.css").toExternalForm());
        stage.setTitle("Sistema de Gestión Veterinaria");
        stage.setScene(scene);
        stage.show();
    }

    @Override
    public void stop() {
        PersistenceManager.cerrar();
    }

    public static void setRoot(String fxml) throws IOException {
        scene.setRoot(loadFXML(fxml));
    }

    private static Parent loadFXML(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource(fxml + ".fxml"));
        return fxmlLoader.load();
    }

    public static void main(String[] args) {
        launch();
    }

}
