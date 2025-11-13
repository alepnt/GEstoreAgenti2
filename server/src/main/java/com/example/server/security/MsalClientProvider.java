package com.example.server.security;

import com.microsoft.aad.msal4j.ConfidentialClientApplication;
import com.microsoft.aad.msal4j.IClientCredential;
import com.microsoft.aad.msal4j.ClientCredentialFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.net.MalformedURLException;
import java.util.concurrent.ExecutionException;

/**
 * Factory centralizzata per l'integrazione con Microsoft Identity Platform.
 */
@Component
public class MsalClientProvider {

    private final String clientId;
    private final String authority;
    private final String clientSecret;

    public MsalClientProvider(
            @Value("${security.azure.client-id}") String clientId,
            @Value("${security.azure.authority}") String authority,
            @Value("${security.azure.client-secret}") String clientSecret) {
        this.clientId = clientId;
        this.authority = authority;
        this.clientSecret = clientSecret;
    }

    public ConfidentialClientApplication createClient() throws MalformedURLException {
        IClientCredential credential = ClientCredentialFactory.createFromSecret(clientSecret);
        return ConfidentialClientApplication.builder(clientId, credential)
                .authority(authority)
                .build();
    }

    public String acquireTokenForScope(String scope) throws ExecutionException, InterruptedException, MalformedURLException {
        return createClient()
                .acquireToken(com.microsoft.aad.msal4j.ClientCredentialParameters
                        .builder(java.util.Set.of(scope))
                        .build())
                .get()
                .accessToken();
    }
}
