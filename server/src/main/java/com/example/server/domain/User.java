package com.example.server.domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Rappresenta l'utente autenticato tramite Microsoft Identity Platform.
 */
@Table("users")
public class User {

    @Id
    private Long id;

    @Column("azure_id")
    private String azureId;

    private String email;

    @Column("display_name")
    private String displayName;

    @Column("password_hash")
    private String passwordHash;

    @Column("role_id")
    private Long roleId;

    @Column("team_id")
    private Long teamId;

    private Boolean active;

    @Column("created_at")
    private LocalDateTime createdAt;

    public User(Long id,
                String azureId,
                String email,
                String displayName,
                String passwordHash,
                Long roleId,
                Long teamId,
                Boolean active,
                LocalDateTime createdAt) {
        this.id = id;
        this.azureId = azureId;
        this.email = email;
        this.displayName = displayName;
        this.passwordHash = passwordHash;
        this.roleId = roleId;
        this.teamId = teamId;
        this.active = active;
        this.createdAt = createdAt;
    }

    public static User newAzureUser(String azureId,
                                    String email,
                                    String displayName,
                                    Long roleId,
                                    Long teamId) {
        return new User(null, azureId, email, displayName, null, roleId, teamId, Boolean.TRUE, LocalDateTime.now());
    }

    public Long getId() {
        return id;
    }

    public String getAzureId() {
        return azureId;
    }

    public String getEmail() {
        return email;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public Long getRoleId() {
        return roleId;
    }

    public Long getTeamId() {
        return teamId;
    }

    public Boolean getActive() {
        return active;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public User withId(Long id) {
        return new User(id, azureId, email, displayName, passwordHash, roleId, teamId, active, createdAt);
    }

    public User updateFromAzure(String displayName, String email) {
        return new User(id, azureId, email, displayName, passwordHash, roleId, teamId, active, createdAt);
    }

    public User withPasswordHash(String passwordHash) {
        return new User(id, azureId, email, displayName, passwordHash, roleId, teamId, active, createdAt);
    }

    public User withRoleAndTeam(Long roleId, Long teamId) {
        return new User(id, azureId, email, displayName, passwordHash, roleId, teamId, active, createdAt);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof User user)) return false;
        return Objects.equals(id, user.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
