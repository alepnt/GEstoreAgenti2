package com.example.server.controller;

import com.example.common.dto.TeamDTO;
import com.example.server.service.TeamService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.jdbc.core.mapping.JdbcMappingContext;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TeamController.class)
class TeamControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private TeamService teamService;

    @MockBean
    private JdbcMappingContext jdbcMappingContext;

    @MockBean(name = "jdbcAuditingHandler")
    private Object jdbcAuditingHandler;

    @Test
    @DisplayName("List teams returns collection payload")
    void listTeams() throws Exception {
        List<TeamDTO> teams = List.of(
                new TeamDTO(1L, "North", "NRT"),
                new TeamDTO(2L, "South", "STH")
        );
        when(teamService.findAll()).thenReturn(teams);

        mockMvc.perform(get("/api/teams"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("North"))
                .andExpect(jsonPath("$[1].code").value("STH"));
    }

    @Test
    @DisplayName("Create team returns persisted entity")
    void createTeam() throws Exception {
        TeamDTO request = new TeamDTO(null, "East", "EST");
        TeamDTO saved = new TeamDTO(4L, "East", "EST");
        when(teamService.create(any(TeamDTO.class))).thenReturn(saved);

        mockMvc.perform(post("/api/teams")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(4))
                .andExpect(jsonPath("$.code").value("EST"));
    }

    @Test
    @DisplayName("Update team returns 404 when missing")
    void updateTeamNotFound() throws Exception {
        when(teamService.update(eq(6L), any(TeamDTO.class))).thenReturn(Optional.empty());

        TeamDTO request = new TeamDTO(null, "West", "WST");

        mockMvc.perform(put("/api/teams/6")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Delete team returns 404 when service reports failure")
    void deleteTeamNotFound() throws Exception {
        when(teamService.delete(8L)).thenReturn(false);

        mockMvc.perform(delete("/api/teams/8"))
                .andExpect(status().isNotFound());
    }
}
