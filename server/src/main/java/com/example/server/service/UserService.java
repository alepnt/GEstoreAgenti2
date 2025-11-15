package com.example.server.service;

import com.example.server.domain.Agent;
import com.example.server.domain.Role;
import com.example.server.domain.Team;
import com.example.server.domain.User;
import com.example.server.dto.AuthResponse;
import com.example.server.dto.LoginRequest;
import com.example.server.dto.RegisterRequest;
import com.example.server.dto.UserSummary;
import com.example.server.repository.AgentRepository;
import com.example.server.repository.RoleRepository;
import com.example.server.repository.TeamRepository;
import com.example.server.repository.UserRepository;
import com.example.server.security.MsalClientProvider;
import com.microsoft.aad.msal4j.ConfidentialClientApplication;
import com.microsoft.aad.msal4j.MsalException;
import com.microsoft.aad.msal4j.OnBehalfOfParameters;
import com.microsoft.aad.msal4j.UserAssertion;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.MalformedURLException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ExecutionException;

@Service
public class UserService {

    private static final String DEFAULT_ROLE = "Agent";
    private static final String DEFAULT_TEAM = "Vendite";

    private final MsalClientProvider msalClientProvider;
    private final UserRepository userRepository;
    private final AgentRepository agentRepository;
    private final RoleRepository roleRepository;
    private final TeamRepository teamRepository;
    private final Clock clock;
    private final Set<String> scopes;

    public UserService(MsalClientProvider msalClientProvider,
                       UserRepository userRepository,
                       AgentRepository agentRepository,
                       RoleRepository roleRepository,
                       TeamRepository teamRepository,
                       Clock clock,
                       @Value("${security.azure.default-scope:https://graph.microsoft.com/.default}") String defaultScope) {
        this.msalClientProvider = msalClientProvider;
        this.userRepository = userRepository;
        this.agentRepository = agentRepository;
        this.roleRepository = roleRepository;
        this.teamRepository = teamRepository;
        this.clock = clock;
        this.scopes = parseScopes(defaultScope);
    }

    public UserService(MsalClientProvider msalClientProvider,
                       UserRepository userRepository,
                       AgentRepository agentRepository,
                       RoleRepository roleRepository,
                       TeamRepository teamRepository) {
        this(msalClientProvider, userRepository, agentRepository, roleRepository, teamRepository, Clock.systemUTC(), "https://graph.microsoft.com/.default");
    }

    @Transactional
    public AuthResponse loginWithMicrosoft(LoginRequest request) {
        LoginRequest requiredRequest = Objects.requireNonNull(request, "request must not be null");
        String delegatedToken = acquireDelegatedToken(requiredRequest.accessToken());

        User savedUser = userRepository.findByAzureId(requiredRequest.azureId())
                .map(user -> user.updateFromAzure(requiredRequest.displayName(), requiredRequest.email()))
                .orElseGet(() -> registerAzureUser(requiredRequest));

        savedUser = userRepository.save(savedUser);

        Instant expiresAt = Instant.now(clock).plusSeconds(3600);
        return new AuthResponse(delegatedToken, "Bearer", expiresAt, toSummary(savedUser));
    }

    @Transactional
    public UserSummary register(RegisterRequest request) {
        RegisterRequest requiredRequest = Objects.requireNonNull(request, "request must not be null");
        Long roleId = resolveRoleId(Optional.ofNullable(requiredRequest.roleName()).filter(name -> !name.isBlank()).orElse(DEFAULT_ROLE));
        Long teamId = resolveTeamId(Optional.ofNullable(requiredRequest.teamName()).filter(name -> !name.isBlank()).orElse(DEFAULT_TEAM));

        User user = userRepository.findByAzureId(requiredRequest.azureId())
                .map(existing -> existing.updateFromAzure(requiredRequest.displayName(), requiredRequest.email()))
                .orElseGet(() -> User.newAzureUser(requiredRequest.azureId(), requiredRequest.email(), requiredRequest.displayName(), roleId, teamId))
                .withRoleAndTeam(roleId, teamId);

        if (requiredRequest.password() != null && !requiredRequest.password().isBlank()) {
            user = user.withPasswordHash(hashPassword(requiredRequest.password()));
        }

        User saved = userRepository.save(user);
        Long savedId = Objects.requireNonNull(saved.getId(), "user id must not be null");

        if (requiredRequest.agentCode() != null && !requiredRequest.agentCode().isBlank()) {
            Agent agent = agentRepository.findByUserId(savedId)
                    .map(existing -> new Agent(existing.getId(), existing.getUserId(), requiredRequest.agentCode(), existing.getTeamRole()))
                    .orElse(Agent.forUser(savedId, requiredRequest.agentCode(), "Member"));
            agentRepository.save(agent);
        }

        return toSummary(saved);
    }

    private User registerAzureUser(LoginRequest request) {
        LoginRequest requiredRequest = Objects.requireNonNull(request, "request must not be null");
        Long roleId = resolveRoleId(DEFAULT_ROLE);
        Long teamId = resolveTeamId(DEFAULT_TEAM);
        return User.newAzureUser(requiredRequest.azureId(), requiredRequest.email(), requiredRequest.displayName(), roleId, teamId);
    }

    private Long resolveRoleId(String name) {
        String requiredName = Objects.requireNonNull(name, "name must not be null");
        return roleRepository.findByName(requiredName)
                .map(Role::getId)
                .orElseGet(() -> roleRepository.save(new Role(null, requiredName)).getId());
    }

    private Long resolveTeamId(String name) {
        String requiredName = Objects.requireNonNull(name, "name must not be null");
        return teamRepository.findByName(requiredName)
                .map(Team::getId)
                .orElseGet(() -> teamRepository.save(new Team(null, requiredName)).getId());
    }

    private UserSummary toSummary(User user) {
        User requiredUser = Objects.requireNonNull(user, "user must not be null");
        return new UserSummary(requiredUser.getId(), requiredUser.getEmail(), requiredUser.getDisplayName(), requiredUser.getAzureId(), requiredUser.getRoleId(), requiredUser.getTeamId());
    }

    private String acquireDelegatedToken(String userAccessToken) {
        try {
            ConfidentialClientApplication client = msalClientProvider.createClient();
            UserAssertion assertion = new UserAssertion(userAccessToken);
            OnBehalfOfParameters parameters = OnBehalfOfParameters
                    .builder(scopes, assertion)
                    .build();
            return client.acquireToken(parameters).get().accessToken();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Interruzione durante l'autenticazione con Microsoft", e);
        } catch (MalformedURLException | ExecutionException | MsalException e) {
            throw new IllegalStateException("Impossibile completare l'autenticazione con Microsoft", e);
        }
    }

    private Set<String> parseScopes(String scopeExpression) {
        var tokens = java.util.Arrays.stream(scopeExpression.split("[\\s,]+"))
                .filter(token -> !token.isBlank())
                .toList();
        if (tokens.isEmpty()) {
            return Set.of("https://graph.microsoft.com/.default");
        }
        return Set.copyOf(tokens);
    }

    private String hashPassword(String rawPassword) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashed = digest.digest(rawPassword.getBytes(StandardCharsets.UTF_8));
            return java.util.HexFormat.of().formatHex(hashed);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("Algoritmo di hashing non disponibile", e);
        }
    }
}
