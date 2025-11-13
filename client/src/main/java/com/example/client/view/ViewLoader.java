package com.example.client.view;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;

import java.io.IOException;

/**
 * Utility per centralizzare il caricamento delle viste FXML.
 */
public final class ViewLoader {

    private ViewLoader() {
    }

    public static Scene loadMainView() {
        try {
            FXMLLoader loader = new FXMLLoader(ViewLoader.class.getResource("/com/example/client/view/MainView.fxml"));
            Parent root = loader.load();
            return new Scene(root);
        } catch (IOException e) {
            throw new IllegalStateException("Impossibile caricare la vista principale", e);
        }
    }
}
