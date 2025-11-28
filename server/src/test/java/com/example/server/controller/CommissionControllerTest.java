package com.example.server.controller;

import com.example.common.dto.CommissionDTO;
import com.example.server.service.CommissionService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.jdbc.core.mapping.JdbcMappingContext;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.Instant;
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

@WebMvcTest(CommissionController.class)
class CommissionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CommissionService commissionService;

    @MockBean
    private JdbcMappingContext jdbcMappingContext;

    @MockBean(name = "jdbcAuditingHandler")
    private Object jdbcAuditingHandler;

    @Test
    @DisplayName("List commissions returns collection payload")
    void listCommissions() throws Exception {
        List<CommissionDTO> commissions = List.of(
                new CommissionDTO(1L, 10L, 100L, BigDecimal.TEN, Instant.EPOCH, Instant.EPOCH),
                new CommissionDTO(2L, 11L, 101L, BigDecimal.ONE, Instant.EPOCH, Instant.EPOCH)
        );
        when(commissionService.findAll()).thenReturn(commissions);

        mockMvc.perform(get("/api/commissions"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].amount").value(10))
                .andExpect(jsonPath("$[1].agentId").value(11));
    }

    @Test
    @DisplayName("Create commission returns persisted entity")
    void createCommission() throws Exception {
        CommissionDTO request = new CommissionDTO(null, 10L, 100L, BigDecimal.ONE, Instant.EPOCH, Instant.EPOCH);
        CommissionDTO saved = new CommissionDTO(5L, 10L, 100L, BigDecimal.ONE, Instant.EPOCH, Instant.EPOCH);
        when(commissionService.create(any(CommissionDTO.class))).thenReturn(saved);

        mockMvc.perform(post("/api/commissions")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(5))
                .andExpect(jsonPath("$.amount").value(1));
    }

    @Test
    @DisplayName("Update commission returns 404 when missing")
    void updateCommissionNotFound() throws Exception {
        when(commissionService.update(eq(3L), any(CommissionDTO.class))).thenReturn(Optional.empty());

        CommissionDTO request = new CommissionDTO(null, 12L, 120L, BigDecimal.TWO, Instant.EPOCH, Instant.EPOCH);

        mockMvc.perform(put("/api/commissions/3")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Delete commission returns 404 when service reports failure")
    void deleteCommissionNotFound() throws Exception {
        when(commissionService.delete(7L)).thenReturn(false);

        mockMvc.perform(delete("/api/commissions/7"))
                .andExpect(status().isNotFound());
    }
}
