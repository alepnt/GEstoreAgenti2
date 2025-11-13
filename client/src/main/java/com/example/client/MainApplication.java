package com.example.client;

import com.example.client.view.ViewLoader;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Entry point JavaFX del client Gestore Agenti.
 */
public class MainApplication extends Application {

    @Override
    public void start(Stage primaryStage) {
        Scene scene = ViewLoader.loadMainView();
        primaryStage.setTitle("Gestore Agenti");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
