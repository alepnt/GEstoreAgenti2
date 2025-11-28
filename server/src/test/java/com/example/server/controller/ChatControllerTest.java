package com.example.server.controller;

import com.example.common.dto.ChatConversationDTO;
import com.example.common.dto.ChatMessageDTO;
import com.example.common.dto.ChatMessageRequest;
import com.example.server.service.ChatService;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ChatController.class)
class ChatControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ChatService chatService;

    @MockBean
    private JdbcMappingContext jdbcMappingContext;

    @MockBean(name = "jdbcAuditingHandler")
    private Object jdbcAuditingHandler;

    @Test
    @DisplayName("List conversations returns payload")
    void listConversations() throws Exception {
        List<ChatConversationDTO> conversations = List.of(
                new ChatConversationDTO("c1", 1L, "Alice", Instant.EPOCH),
                new ChatConversationDTO("c2", 2L, "Bob", Instant.EPOCH)
        );
        when(chatService.listConversations(10L)).thenReturn(conversations);

        mockMvc.perform(get("/api/chat/conversations").param("userId", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].conversationId").value("c1"))
                .andExpect(jsonPath("$[1].lastMessageSenderId").value(2));
    }

    @Test
    @DisplayName("Send message validates request body")
    void sendMessageValidationError() throws Exception {
        ChatMessageRequest invalidRequest = new ChatMessageRequest(null, "", "");

        mockMvc.perform(post("/api/chat/messages")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Send message returns persisted payload")
    void sendMessage() throws Exception {
        ChatMessageRequest request = new ChatMessageRequest(5L, "conv-1", "Hello");
        ChatMessageDTO response = new ChatMessageDTO(9L, "conv-1", 5L, 3L, "Hello", Instant.EPOCH);
        when(chatService.sendMessage(any(ChatMessageRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/chat/messages")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(9))
                .andExpect(jsonPath("$.body").value("Hello"));
    }
}
