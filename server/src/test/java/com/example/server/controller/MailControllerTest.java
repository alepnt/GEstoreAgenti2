package com.example.server.controller;

import com.example.common.dto.MailRequest;
import com.example.server.service.MailService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.jdbc.core.mapping.JdbcMappingContext;
import org.springframework.http.HttpHeaders;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(MailController.class)
class MailControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private MailService mailService;

    @MockBean
    private JdbcMappingContext jdbcMappingContext;

    @MockBean(name = "jdbcAuditingHandler")
    private Object jdbcAuditingHandler;

    @Test
    @DisplayName("Send mail returns 202 and delegates to service")
    void sendMail() throws Exception {
        MailRequest request = new MailRequest("Subject", "Body", List.of("to@example.com"), List.of(), List.of(), List.of());

        mockMvc.perform(post("/api/mail/send")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer token-123")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isAccepted());

        verify(mailService).sendMail(eq("token-123"), any(MailRequest.class));
    }

    @Test
    @DisplayName("Validation errors result in 400 responses")
    void sendMailValidationError() throws Exception {
        MailRequest invalidRequest = new MailRequest("", "", List.of(), List.of(), List.of(), List.of());

        mockMvc.perform(post("/api/mail/send")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer token-123")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }
}
