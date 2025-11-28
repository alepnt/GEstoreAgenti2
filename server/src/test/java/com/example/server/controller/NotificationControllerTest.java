package com.example.server.controller;

import com.example.common.dto.NotificationDTO;
import com.example.common.dto.NotificationSubscriptionDTO;
import com.example.server.dto.NotificationSubscribeRequest;
import com.example.server.service.NotificationService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.jdbc.core.mapping.JdbcMappingContext;
import org.springframework.test.web.servlet.MockMvc;

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

@WebMvcTest(NotificationController.class)
class NotificationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private NotificationService notificationService;

    @MockBean
    private JdbcMappingContext jdbcMappingContext;

    @MockBean(name = "jdbcAuditingHandler")
    private Object jdbcAuditingHandler;

    @Test
    @DisplayName("List notifications returns collection payload")
    void listNotifications() throws Exception {
        List<NotificationDTO> notifications = List.of(
                new NotificationDTO(1L, 10L, null, "Title 1", "Message 1", false, Instant.EPOCH),
                new NotificationDTO(2L, 10L, null, "Title 2", "Message 2", true, Instant.EPOCH)
        );
        when(notificationService.findNotifications(10L, null)).thenReturn(notifications);

        mockMvc.perform(get("/api/notifications").param("userId", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("Title 1"))
                .andExpect(jsonPath("$[1].read").value(true));
    }

    @Test
    @DisplayName("Create notification validates payload")
    void createNotificationValidation() throws Exception {
        NotificationDTO invalid = new NotificationDTO(1L, 10L, null, "", "", false, Instant.EPOCH);

        mockMvc.perform(post("/api/notifications")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalid)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Update notification returns 404 when missing")
    void updateNotificationNotFound() throws Exception {
        NotificationDTO request = new NotificationDTO(1L, 10L, null, "Title", "Body", false, Instant.EPOCH);
        when(notificationService.updateNotification(eq(99L), any(NotificationDTO.class))).thenReturn(Optional.empty());

        mockMvc.perform(put("/api/notifications/99")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Delete notification translates service errors to 404")
    void deleteNotificationNotFound() throws Exception {
        NotificationDTO existing = new NotificationDTO(1L, 10L, null, "Title", "Body", false, Instant.EPOCH);
        when(notificationService.deleteNotification(5L)).thenThrow(new IllegalArgumentException("Missing"));

        mockMvc.perform(delete("/api/notifications/5"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Subscribe channel delegates to service")
    void subscribeChannel() throws Exception {
        NotificationSubscribeRequest request = new NotificationSubscribeRequest("token", "mobile");
        NotificationSubscriptionDTO response = new NotificationSubscriptionDTO(1L, 10L, "token", "mobile", Instant.EPOCH);
        when(notificationService.subscribe(any(NotificationSubscribeRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/notifications/subscribe")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.channel").value("mobile"));
    }
}
