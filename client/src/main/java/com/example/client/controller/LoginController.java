package com.example.client.controller;

import com.example.client.service.AuthApiClient;
import com.example.client.service.AuthSession;
import com.example.client.service.LoginForm;
import com.example.client.session.SessionStore;
import com.example.client.validation.CompositeValidator;
import com.example.client.validation.EmailValidationStrategy;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Optional;

public class LoginController {

    @FXML
    private TextField emailField;
    @FXML
    private TextField displayNameField;
    @FXML
    private TextField azureIdField;
    @FXML
    private TextArea accessTokenArea;
    @FXML
    private Label statusLabel;
    @FXML
    private Button loginButton;
    @FXML
    private Hyperlink registerLink;

    private final SessionStore sessionStore;
    private final AuthApiClient authApiClient;
    private final CompositeValidator emailValidator = new CompositeValidator().addStrategy(new EmailValidationStrategy());

    public static LoginController create(SessionStore sessionStore) {
        return new LoginController(sessionStore, new AuthApiClient());
    }

    public static LoginController create(SessionStore sessionStore, AuthApiClient client) {
        return new LoginController(sessionStore, client);
    }

    private LoginController(SessionStore sessionStore, AuthApiClient authApiClient) {
        this.sessionStore = sessionStore;
        this.authApiClient = authApiClient;
    }

    @FXML
    public void initialize() {
        Platform.runLater(() -> sessionStore.load().ifPresent(session -> {
            if (session.isExpired()) {
                try {
                    sessionStore.clear();
                } catch (IOException ignored) {
                    // ignora errori di pulizia
                }
                statusLabel.setText("La sessione salvata è scaduta. Effettua nuovamente il login.");
            } else {
                statusLabel.setText("Sessione attiva per " + session.user().displayName());
            }
        }));
    }

    @FXML
    public void handleLogin(ActionEvent event) {
        Optional<String> validationError = emailValidator.validate(emailField.getText());
        if (validationError.isPresent()) {
            statusLabel.setText(validationError.get());
            return;
        }
        if (accessTokenArea.getText() == null || accessTokenArea.getText().isBlank()) {
            statusLabel.setText("Inserire un access token Microsoft valido");
            return;
        }
        if (azureIdField.getText() == null || azureIdField.getText().isBlank()) {
            statusLabel.setText("Inserire l'identificativo Azure dell'utente");
            return;
        }

        LoginForm form = new LoginForm(accessTokenArea.getText().trim(), emailField.getText().trim(), displayNameField.getText().trim(), azureIdField.getText().trim());
        try {
            AuthSession session = authApiClient.login(form);
            sessionStore.save(session);
            statusLabel.setText("Autenticazione riuscita. Bentornato " + session.user().displayName() + "!");
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            statusLabel.setText("Operazione interrotta: " + e.getMessage());
        } catch (IOException e) {
            statusLabel.setText("Autenticazione fallita: " + e.getMessage());
        }
    }

    @FXML
    public void openRegister(ActionEvent event) {
        navigate("/com/example/client/view/RegisterView.fxml", type -> {
            if (type == RegisterController.class) {
                return RegisterController.create(sessionStore, authApiClient);
            }
            throw new IllegalStateException("Controller non supportato: " + type.getName());
        }, "Gestore Agenti - Registrazione");
    }

    private void navigate(String fxmlPath, ControllerFactory factory, String title) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            loader.setControllerFactory(factory::create);
            Parent root = loader.load();
            Stage stage = (Stage) loginButton.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle(title);
        } catch (IOException e) {
            statusLabel.setText("Impossibile aprire la vista richiesta: " + e.getMessage());
        }
    }

    @FunctionalInterface
    private interface ControllerFactory {
        Object create(Class<?> type);
    }
}
