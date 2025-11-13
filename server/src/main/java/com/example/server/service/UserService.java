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
        String delegatedToken = acquireDelegatedToken(request.accessToken());

        User savedUser = userRepository.findByAzureId(request.azureId())
                .map(user -> user.updateFromAzure(request.displayName(), request.email()))
                .orElseGet(() -> registerAzureUser(request));

        savedUser = userRepository.save(savedUser);

        Instant expiresAt = Instant.now(clock).plusSeconds(3600);
        return new AuthResponse(delegatedToken, "Bearer", expiresAt, toSummary(savedUser));
    }

    @Transactional
    public UserSummary register(RegisterRequest request) {
        Long roleId = resolveRoleId(Optional.ofNullable(request.roleName()).filter(name -> !name.isBlank()).orElse(DEFAULT_ROLE));
        Long teamId = resolveTeamId(Optional.ofNullable(request.teamName()).filter(name -> !name.isBlank()).orElse(DEFAULT_TEAM));

        User user = userRepository.findByAzureId(request.azureId())
                .map(existing -> existing.updateFromAzure(request.displayName(), request.email()))
                .orElseGet(() -> User.newAzureUser(request.azureId(), request.email(), request.displayName(), roleId, teamId))
                .withRoleAndTeam(roleId, teamId);

        if (request.password() != null && !request.password().isBlank()) {
            user = user.withPasswordHash(hashPassword(request.password()));
        }

        User saved = userRepository.save(user);

        if (request.agentCode() != null && !request.agentCode().isBlank()) {
            Agent agent = agentRepository.findByUserId(saved.getId())
                    .map(existing -> new Agent(existing.getId(), existing.getUserId(), request.agentCode(), existing.getTeamRole()))
                    .orElse(Agent.forUser(saved.getId(), request.agentCode(), "Member"));
            agentRepository.save(agent);
        }

        return toSummary(saved);
    }

    private User registerAzureUser(LoginRequest request) {
        Long roleId = resolveRoleId(DEFAULT_ROLE);
        Long teamId = resolveTeamId(DEFAULT_TEAM);
        return User.newAzureUser(request.azureId(), request.email(), request.displayName(), roleId, teamId);
    }

    private Long resolveRoleId(String name) {
        return roleRepository.findByName(name)
                .map(Role::getId)
                .orElseGet(() -> roleRepository.save(new Role(null, name)).getId());
    }

    private Long resolveTeamId(String name) {
        return teamRepository.findByName(name)
                .map(Team::getId)
                .orElseGet(() -> teamRepository.save(new Team(null, name)).getId());
    }

    private UserSummary toSummary(User user) {
        return new UserSummary(user.getId(), user.getEmail(), user.getDisplayName(), user.getAzureId(), user.getRoleId(), user.getTeamId());
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
