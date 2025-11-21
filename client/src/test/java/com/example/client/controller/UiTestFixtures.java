package com.example.client.controller;

import com.example.client.auth.MsalAccount;
import com.example.client.auth.MsalAuthenticationException;
import com.example.client.auth.MsalAuthenticationResult;
import com.example.client.auth.TokenProvider;
import com.example.client.service.AuthApiClient;
import com.example.client.service.AuthSession;
import com.example.client.service.UserSummary;
import com.example.client.session.SessionStore;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.testfx.api.FxToolkit;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.util.ArrayDeque;
import java.util.Optional;
import java.util.Queue;

final class UiTestFixtures {

    static void enableHeadlessMode() {
        System.setProperty("testfx.robot", "glass");
        System.setProperty("testfx.headless", "true");
        System.setProperty("prism.order", "sw");
        System.setProperty("prism.text", "t2k");
        System.setProperty("glass.platform", "Monocle");
        System.setProperty("monocle.platform", "Headless");
        System.setProperty("java.awt.headless", "true");
        System.setProperty("monocle.screen.width", "1280");
        System.setProperty("monocle.screen.height", "720");
        System.setProperty("quantum.multithreaded", "false");
    }

    static SessionStore newTemporarySessionStore() throws IOException {
        Path sessionDir = Files.createTempDirectory("gestore-agenti-session");
        sessionDir.toFile().deleteOnExit();
        return new SessionStore(sessionDir);
    }

    static AuthSession demoSession() {
        return new AuthSession(
                "dummy-token",
                "Bearer",
                Instant.now().plusSeconds(3600),
                new UserSummary(1L, "demo@example.com", "Demo User", "azure-1", 1L, 1L),
                "https://login.microsoftonline.com/mock",
                "refresh-token"
        );
    }

    static void cleanupStages() throws Exception {
        FxToolkit.hideStage();
        FxToolkit.cleanupStages();
    }

    static class StubAuthApiClient extends AuthApiClient {
        private final java.util.function.Function<com.example.client.service.LoginForm, AuthSession> loginResolver;
        private final java.util.function.Supplier<UserSummary> registerSupplier;

        StubAuthApiClient(AuthSession session) {
            this(form -> session, session != null ? session::user : () -> null);
        }

        StubAuthApiClient(java.util.function.Function<com.example.client.service.LoginForm, AuthSession> loginResolver) {
            this(loginResolver, () -> null);
        }

        StubAuthApiClient(java.util.function.Function<com.example.client.service.LoginForm, AuthSession> loginResolver,
                          java.util.function.Supplier<UserSummary> registerSupplier) {
            this.loginResolver = loginResolver;
            this.registerSupplier = registerSupplier;
        }

        @Override
        public AuthSession login(com.example.client.service.LoginForm form) {
            AuthSession session = loginResolver.apply(form);
            if (session == null) {
                throw new IllegalStateException("Nessuna sessione di test disponibile per " + form.azureId());
            }
            return session;
        }

        @Override
        public UserSummary register(com.example.client.service.RegisterForm form) {
            UserSummary summary = registerSupplier.get();
            if (summary == null) {
                throw new IllegalStateException("Nessun utente di test disponibile per la registrazione");
            }
            return summary;
        }
    }

    static class StubMainViewController extends MainViewController {
        private final Stage fallbackStage;

        StubMainViewController(AuthSession session, SessionStore sessionStore, Stage fallbackStage) {
            super(session, sessionStore);
            this.fallbackStage = fallbackStage;
        }

        @Override
        public void refreshData() {
            // Evita chiamate al backend durante i test UI.
        }

        @Override
        protected void performLogoutNavigation() {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/client/view/LoginView.fxml"));
                StubTokenProvider tokenProvider = new StubTokenProvider();
                loader.setControllerFactory(param -> {
                    if (param == LoginController.class) {
                        return LoginController.create(
                                sessionStore,
                                new StubAuthApiClient(sessionStore.load().orElse(null)),
                                tokenProvider,
                                MainViewController.LOGOUT_STATUS_MESSAGE,
                                MainViewController.LOGOUT_STATUS_STYLE
                        );
                    }
                    throw new IllegalStateException("Controller non gestito: " + param.getName());
                });

                Parent root = loader.load();
                String theme = getClass().getResource("/com/example/client/style/theme.css").toExternalForm();
                Stage targetStage = determineStage();
                Scene targetScene = targetStage.getScene();

                if (targetScene == null) {
                    Scene scene = new Scene(root);
                    scene.getStylesheets().add(theme);
                    targetStage.setScene(scene);
                } else {
                    if (!targetScene.getStylesheets().contains(theme)) {
                        targetScene.getStylesheets().add(theme);
                    }
                    targetScene.setRoot(root);
                }

                targetStage.setTitle("Gestore Agenti - Login");
                targetStage.show();
            } catch (IOException e) {
                throw new RuntimeException("Impossibile aprire la schermata di login di test", e);
            }
        }

        private Stage determineStage() {
            Stage stage = getCurrentStage();
            if (stage == null) {
                stage = fallbackStage;
            }
            if (stage == null) {
                try {
                    stage = FxToolkit.registerPrimaryStage();
                } catch (Exception e) {
                    throw new IllegalStateException("Impossibile determinare lo stage di test per il logout", e);
                }
            }
            return stage;
        }
    }

    static class StubMainViewFactory implements LoginController.MainViewFactory {
        private Stage fallbackStage;

        StubMainViewFactory() {
            this(null);
        }

        StubMainViewFactory(Stage fallbackStage) {
            this.fallbackStage = fallbackStage;
        }

        void setFallbackStage(Stage fallbackStage) {
            this.fallbackStage = fallbackStage;
        }

        @Override
        public MainViewController create(AuthSession session, SessionStore sessionStore) {
            return new StubMainViewController(session, sessionStore, fallbackStage);
        }
    }

    static class StubTokenProvider implements TokenProvider {
        private final Queue<Optional<MsalAuthenticationResult>> silentResponses = new ArrayDeque<>();
        private final Queue<Object> interactiveResponses = new ArrayDeque<>();

        void enqueueSilent(Optional<MsalAuthenticationResult> result) {
            silentResponses.add(result);
        }

        void enqueueInteractiveSuccess(MsalAuthenticationResult result) {
            interactiveResponses.add(result);
        }

        void enqueueInteractiveFailure(MsalAuthenticationException exception) {
            interactiveResponses.add(exception);
        }

        void reset() {
            silentResponses.clear();
            interactiveResponses.clear();
        }

        @Override
        public Optional<MsalAuthenticationResult> acquireTokenSilently() throws MsalAuthenticationException {
            if (silentResponses.isEmpty()) {
                return Optional.empty();
            }
            Optional<MsalAuthenticationResult> result = silentResponses.remove();
            if (result == null) {
                throw new MsalAuthenticationException("Risposta silenziosa MSAL non valida");
            }
            return result;
        }

        @Override
        public MsalAuthenticationResult acquireTokenInteractive() throws MsalAuthenticationException {
            if (interactiveResponses.isEmpty()) {
                throw new MsalAuthenticationException("Nessuna risposta interattiva MSAL configurata");
            }
            Object response = interactiveResponses.remove();
            if (response instanceof MsalAuthenticationException exception) {
                throw exception;
            }
            return (MsalAuthenticationResult) response;
        }
    }

    static MsalAuthenticationResult msalResultFor(AuthSession session) {
        MsalAccount account = new MsalAccount(
                session.user().email(),
                session.user().displayName(),
                session.user().azureId(),
                "test-tenant",
                session.user().azureId()
        );
        return new MsalAuthenticationResult(
                session.accessToken(),
                session.refreshToken(),
                session.expiresAt(),
                session.authority(),
                account
        );
    }
}
