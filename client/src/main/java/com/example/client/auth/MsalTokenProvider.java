package com.example.client.auth;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.microsoft.aad.msal4j.IAccount;
import com.microsoft.aad.msal4j.IAuthenticationResult;
import com.microsoft.aad.msal4j.ITokenCacheAccessAspect;
import com.microsoft.aad.msal4j.ITokenCacheAccessContext;
import com.microsoft.aad.msal4j.InteractiveRequestParameters;
import com.microsoft.aad.msal4j.MsalClientException;
import com.microsoft.aad.msal4j.MsalException;
import com.microsoft.aad.msal4j.MsalInteractionRequiredException;
import com.microsoft.aad.msal4j.MsalServiceException;
import com.microsoft.aad.msal4j.PublicClientApplication;
import com.microsoft.aad.msal4j.SilentParameters;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.time.Instant;
import java.util.Base64;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletionException;

public class MsalTokenProvider implements TokenProvider {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private final PublicClientApplication application;
    private final URI redirectUri;
    private final String authority;
    private final Set<String> scopes;
    private final TokenCache cache;
    private volatile IAuthenticationResult lastResult;

    public MsalTokenProvider(MsalConfiguration configuration) {
        this.redirectUri = configuration.redirectUri();
        this.scopes = Set.copyOf(configuration.scopes());
        this.authority = configuration.authority();
        this.cache = new TokenCache();
        try {
            this.application = PublicClientApplication.builder(configuration.clientId())
                    .authority(configuration.authority())
                    .setTokenCacheAccessAspect(cache)
                    .build();
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException("Authority MSAL non valida: " + configuration.authority(), e);
        }
    }

    public static TokenProvider fromEnvironment() {
        return new MsalTokenProvider(MsalConfiguration.fromEnvironment());
    }

    public static TokenProvider disabled(String reason) {
        return new DisabledTokenProvider(reason);
    }

    @Override
    public synchronized Optional<MsalAuthenticationResult> acquireTokenSilently() throws MsalAuthenticationException {
        if (lastResult != null && !MsalTokenProvider.this.isExpired(lastResult)) {
            return Optional.of(mapResult(lastResult));
        }
        try {
            Set<IAccount> accounts = application.getAccounts().join();
            if (accounts == null || accounts.isEmpty()) {
                return Optional.empty();
            }
            IAccount account = accounts.iterator().next();
            SilentParameters parameters = SilentParameters.builder(scopes, account).build();
            lastResult = acquireTokenSilently(parameters);
            return Optional.of(mapResult(lastResult));
        } catch (CompletionException ex) {
            Throwable root = unwrap(ex);
            if (root instanceof MsalInteractionRequiredException) {
                return Optional.empty();
            }
            throw asAuthenticationException("acquisizione silenziosa", root);
        } catch (MsalException ex) {
            throw asAuthenticationException("acquisizione silenziosa", ex);
        } catch (MalformedURLException ex) {
            throw new MsalAuthenticationException("Configurazione MSAL non valida: " + ex.getMessage(), ex);
        }
    }

    @Override
    public synchronized MsalAuthenticationResult acquireTokenInteractive() throws MsalAuthenticationException {
        try {
            InteractiveRequestParameters parameters = InteractiveRequestParameters
                    .builder(redirectUri)
                    .scopes(scopes)
                    .build();
            lastResult = application.acquireToken(parameters).join();
            return mapResult(lastResult);
        } catch (CompletionException ex) {
            throw asAuthenticationException("acquisizione interattiva", unwrap(ex));
        } catch (MsalException ex) {
            throw asAuthenticationException("acquisizione interattiva", ex);
        }
    }

    private boolean isExpired(IAuthenticationResult result) {
        return result == null || result.expiresOnDate() == null
                || result.expiresOnDate().toInstant().isBefore(Instant.now());
    }

    private IAuthenticationResult acquireTokenSilently(SilentParameters parameters) throws MsalAuthenticationException {
        try {
            return application.acquireTokenSilently(parameters).join();
        } catch (MalformedURLException ex) {
            throw asAuthenticationException("acquisizione silenziosa", ex);
        }
    }

    private MsalAuthenticationResult mapResult(IAuthenticationResult result) {
        if (result == null) {
            return null;
        }
        IAccount account = result.account();
        Map<String, Object> claims = extractIdTokenClaims(result);
        String displayName = firstNonBlank(
                (String) claims.get("name"),
                account != null ? account.username() : null
        );
        String objectId = firstNonBlank(
                (String) claims.get("oid"),
                account != null ? account.homeAccountId() : null
        );
        String tenantId = (String) claims.get("tid");
        MsalAccount msalAccount = new MsalAccount(
                account != null ? account.username() : null,
                displayName,
                objectId,
                tenantId,
                account != null ? account.homeAccountId() : null
        );
        Instant expiresOn = result.expiresOnDate() != null ? result.expiresOnDate().toInstant() : null;
        return new MsalAuthenticationResult(result.accessToken(), null, expiresOn, authority, msalAccount);
    }

    private Map<String, Object> extractIdTokenClaims(IAuthenticationResult result) {
        try {
            String idToken = result.idToken();
            if (idToken == null || idToken.isBlank()) {
                return Collections.emptyMap();
            }
            String[] parts = idToken.split("\\.");
            if (parts.length < 2) {
                return Collections.emptyMap();
            }
            byte[] payload = Base64.getUrlDecoder().decode(parts[1]);
            return OBJECT_MAPPER.readValue(payload, new TypeReference<>() {
            });
        } catch (IllegalArgumentException | IOException e) {
            return Collections.emptyMap();
        }
    }

    private String firstNonBlank(String... values) {
        if (values == null) {
            return null;
        }
        for (String value : values) {
            if (value != null && !value.isBlank()) {
                return value;
            }
        }
        return null;
    }

    private MsalAuthenticationException asAuthenticationException(String phase, Throwable error) {
        if (error instanceof MsalInteractionRequiredException interactionRequired) {
            return new MsalAuthenticationException("È richiesta un'interazione utente per completare il login Microsoft", interactionRequired);
        }
        if (error instanceof MsalServiceException serviceException) {
            return new MsalAuthenticationException(
                    "Errore del servizio Microsoft durante " + phase + ": " + serviceException.errorCode(), serviceException);
        }
        if (error instanceof MsalClientException clientException) {
            return new MsalAuthenticationException(
                    "Errore di configurazione MSAL durante " + phase + ": " + clientException.errorCode(), clientException);
        }
        String message = error != null ? error.getMessage() : "errore sconosciuto";
        return new MsalAuthenticationException("Errore durante " + phase + ": " + message, error);
    }

    private Throwable unwrap(Throwable throwable) {
        Throwable current = throwable;
        while (current instanceof CompletionException && current.getCause() != null) {
            current = current.getCause();
        }
        return current;
    }

    private static final class TokenCache implements ITokenCacheAccessAspect {
        private String cacheData;

        @Override
        public synchronized void beforeCacheAccess(ITokenCacheAccessContext context) {
            if (cacheData != null) {
                context.tokenCache().deserialize(cacheData);
            }
        }

        @Override
        public synchronized void afterCacheAccess(ITokenCacheAccessContext context) {
            cacheData = context.tokenCache().serialize();
        }
    }

    private static final class DisabledTokenProvider implements TokenProvider {
        private final String reason;

        private DisabledTokenProvider(String reason) {
            this.reason = reason == null ? "Servizio MSAL non configurato" : reason;
        }

        @Override
        public Optional<MsalAuthenticationResult> acquireTokenSilently() throws MsalAuthenticationException {
            throw new MsalAuthenticationException(reason);
        }

        @Override
        public MsalAuthenticationResult acquireTokenInteractive() throws MsalAuthenticationException {
            throw new MsalAuthenticationException(reason);
        }
    }
}
