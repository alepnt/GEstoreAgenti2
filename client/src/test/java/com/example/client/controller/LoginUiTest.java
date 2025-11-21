package com.example.client.controller;

import com.example.client.auth.MsalAuthenticationException;
import com.example.client.service.AuthSession;
import com.example.client.service.UserSummary;
import com.example.client.session.SessionStore;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testfx.api.FxToolkit;
import org.testfx.framework.junit5.ApplicationTest;
import org.testfx.matcher.base.NodeMatchers;
import org.testfx.matcher.control.LabeledMatchers;
import org.testfx.util.WaitForAsyncUtils;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.testfx.api.FxAssert.verifyThat;

class LoginUiTest extends ApplicationTest {

    private SessionStore sessionStore;
    private AuthSession session;
    private Map<String, AuthSession> availableSessions;
    private UiTestFixtures.StubAuthApiClient authApiClient;
    private UiTestFixtures.StubMainViewFactory mainViewFactory;
    private UiTestFixtures.StubTokenProvider tokenProvider;
    private Stage primaryStage;
    private AtomicInteger loginInvocations;

    @BeforeAll
    static void configureHeadless() {
        UiTestFixtures.enableHeadlessMode();
    }

    @BeforeEach
    void setUp() throws Exception {
        sessionStore = createSessionStore();
        session = UiTestFixtures.demoSession();
        availableSessions = new HashMap<>();
        availableSessions.put(session.user().azureId(), session);
        loginInvocations = new AtomicInteger();
        authApiClient = new UiTestFixtures.StubAuthApiClient(form -> {
            loginInvocations.incrementAndGet();
            return availableSessions.get(form.azureId());
        });
        mainViewFactory = new UiTestFixtures.StubMainViewFactory();
        tokenProvider = new UiTestFixtures.StubTokenProvider();
        FxToolkit.registerPrimaryStage();
        FxToolkit.setupFixture(() -> {
            tokenProvider.reset();
            try {
                loadLoginScene();
            } catch (IOException e) {
                throw new RuntimeException("Impossibile caricare la schermata di login di test", e);
            }
        });
    }

    @AfterEach
    void tearDown() throws Exception {
        UiTestFixtures.cleanupStages();
    }

    @Override
    public void start(Stage stage) throws IOException {
        this.primaryStage = stage;
        ensureDependenciesInitialized();
        mainViewFactory.setFallbackStage(primaryStage);
        loadLoginScene();
    }

    @Test
    void successfulLoginNavigatesToDashboard() {
        performLogin(session);
        verifyThat("#mainTabPane", NodeMatchers.isVisible());
    }

    @Test
    void loginsForDifferentAccountsDoNotOverrideSessions() throws Exception {
        AuthSession secondSession = new AuthSession(
                "second-token",
                "Bearer",
                session.expiresAt().plusSeconds(600),
                new UserSummary(2L, "second@example.com", "Second User", "azure-2", 2L, 2L),
                session.authority(),
                "refresh-second"
        );
        availableSessions.put(secondSession.user().azureId(), secondSession);

        performLogin(session);
        WaitForAsyncUtils.waitForFxEvents();

        relaunchLoginView();

        performLogin(secondSession);
        WaitForAsyncUtils.waitForFxEvents();

        var storedFirst = sessionStore.loadForUser(session.user().azureId());
        var storedSecond = sessionStore.loadForUser(secondSession.user().azureId());

        assertTrue(storedFirst.isPresent(), "La sessione del primo utente deve esistere");
        assertEquals(session.accessToken(), storedFirst.get().accessToken());
        assertTrue(storedSecond.isPresent(), "La sessione del secondo utente deve esistere");
        assertEquals(secondSession.accessToken(), storedSecond.get().accessToken());
    }

    @Test
    void msalFailureIsShownToUser() {
        tokenProvider.enqueueSilent(Optional.empty());
        tokenProvider.enqueueInteractiveFailure(new MsalAuthenticationException("Browser bloccato"));

        clickOn("#loginButton");
        WaitForAsyncUtils.waitForFxEvents();

        verifyThat("#statusLabel", LabeledMatchers.hasText("Browser bloccato"));
        assertEquals(0, loginInvocations.get());
    }

    private void loadLoginScene() throws IOException {
        if (primaryStage == null) {
            try {
                primaryStage = FxToolkit.registerPrimaryStage();
            } catch (Exception e) {
                throw new RuntimeException("Impossibile ottenere lo stage di test", e);
            }
        }
        mainViewFactory.setFallbackStage(primaryStage);
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/client/view/LoginView.fxml"));
        loader.setControllerFactory(type -> {
            if (type == LoginController.class) {
                return LoginController.create(sessionStore, authApiClient, tokenProvider, mainViewFactory);
            }
            throw new IllegalStateException("Controller non gestito: " + type.getName());
        });
        Parent root = loader.load();
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
    }

    private void relaunchLoginView() throws Exception {
        FxToolkit.setupFixture(() -> {
            try {
                loadLoginScene();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        WaitForAsyncUtils.waitForFxEvents();
    }

    private void performLogin(AuthSession sessionToUse) {
        tokenProvider.enqueueSilent(Optional.empty());
        tokenProvider.enqueueInteractiveSuccess(UiTestFixtures.msalResultFor(sessionToUse));

        clickOn("#loginButton");
        WaitForAsyncUtils.waitForFxEvents();
    }

    private void ensureDependenciesInitialized() {
        if (sessionStore == null) {
            sessionStore = createSessionStore();
        }
        if (session == null) {
            session = UiTestFixtures.demoSession();
        }
        if (availableSessions == null) {
            availableSessions = new HashMap<>();
            availableSessions.put(session.user().azureId(), session);
        }
        if (authApiClient == null) {
            loginInvocations = new AtomicInteger();
            authApiClient = new UiTestFixtures.StubAuthApiClient(form -> {
                loginInvocations.incrementAndGet();
                return availableSessions.get(form.azureId());
            });
        }
        if (mainViewFactory == null) {
            mainViewFactory = new UiTestFixtures.StubMainViewFactory();
        }
        mainViewFactory.setFallbackStage(primaryStage);
        if (tokenProvider == null) {
            tokenProvider = new UiTestFixtures.StubTokenProvider();
        }
    }

    private SessionStore createSessionStore() {
        try {
            return UiTestFixtures.newTemporarySessionStore();
        } catch (IOException e) {
            throw new RuntimeException("Impossibile creare un SessionStore temporaneo", e);
        }
    }
}
