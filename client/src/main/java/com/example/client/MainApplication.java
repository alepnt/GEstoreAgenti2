package com.example.client;

import com.example.client.controller.LoginController;
import com.example.client.service.AuthApiClient;
import com.example.client.session.SessionStore;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class MainApplication extends Application {

    private final SessionStore sessionStore = new SessionStore();
    private final AuthApiClient authApiClient = new AuthApiClient();

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/client/view/LoginView.fxml"));
        loader.setControllerFactory(param -> {
            if (param == LoginController.class) {
                return LoginController.create(sessionStore, authApiClient);
            }
            throw new IllegalStateException("Controller sconosciuto: " + param.getName());
        });

        Parent root = loader.load();
        Scene scene = new Scene(root);
        stage.setTitle("Gestore Agenti - Login");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
