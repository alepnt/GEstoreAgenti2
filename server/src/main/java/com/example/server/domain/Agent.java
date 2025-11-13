package com.example.server.domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.util.Objects;

@Table("agents")
public class Agent {

    @Id
    private Long id;

    @Column("user_id")
    private Long userId;

    @Column("agent_code")
    private String agentCode;

    @Column("team_role")
    private String teamRole;

    public Agent(Long id, Long userId, String agentCode, String teamRole) {
        this.id = id;
        this.userId = userId;
        this.agentCode = agentCode;
        this.teamRole = teamRole;
    }

    public static Agent forUser(Long userId, String agentCode, String teamRole) {
        return new Agent(null, userId, agentCode, teamRole);
    }

    public Long getId() {
        return id;
    }

    public Long getUserId() {
        return userId;
    }

    public String getAgentCode() {
        return agentCode;
    }

    public String getTeamRole() {
        return teamRole;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Agent agent)) return false;
        return Objects.equals(id, agent.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
