package com.example.server.controller;

import com.example.common.dto.RoleDTO;
import com.example.server.service.RoleService;
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

@WebMvcTest(RoleController.class)
class RoleControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private RoleService roleService;

    @MockBean
    private JdbcMappingContext jdbcMappingContext;

    @MockBean(name = "jdbcAuditingHandler")
    private Object jdbcAuditingHandler;

    @Test
    @DisplayName("List roles returns collection payload")
    void listRoles() throws Exception {
        List<RoleDTO> roles = List.of(
                new RoleDTO(1L, "Admin", "Administrator"),
                new RoleDTO(2L, "User", "Standard user")
        );
        when(roleService.findAll()).thenReturn(roles);

        mockMvc.perform(get("/api/roles"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Admin"))
                .andExpect(jsonPath("$[1].description").value("Standard user"));
    }

    @Test
    @DisplayName("Create role returns persisted entity")
    void createRole() throws Exception {
        RoleDTO request = new RoleDTO(null, "Auditor", "Reviews data");
        RoleDTO saved = new RoleDTO(5L, "Auditor", "Reviews data");
        when(roleService.create(any(RoleDTO.class))).thenReturn(saved);

        mockMvc.perform(post("/api/roles")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(5))
                .andExpect(jsonPath("$.name").value("Auditor"));
    }

    @Test
    @DisplayName("Update role returns 404 when missing")
    void updateRoleNotFound() throws Exception {
        when(roleService.update(eq(3L), any(RoleDTO.class))).thenReturn(Optional.empty());

        RoleDTO request = new RoleDTO(null, "Editor", "Edits data");

        mockMvc.perform(put("/api/roles/3")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Delete role returns 404 when service reports failure")
    void deleteRoleNotFound() throws Exception {
        when(roleService.delete(7L)).thenReturn(false);

        mockMvc.perform(delete("/api/roles/7"))
                .andExpect(status().isNotFound());
    }
}
