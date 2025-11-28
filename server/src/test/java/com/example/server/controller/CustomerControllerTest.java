package com.example.server.controller;

import com.example.common.dto.CustomerDTO;
import com.example.server.service.CustomerService;
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

@WebMvcTest(CustomerController.class)
class CustomerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CustomerService customerService;

    @MockBean
    private JdbcMappingContext jdbcMappingContext;

    @MockBean(name = "jdbcAuditingHandler")
    private Object jdbcAuditingHandler;

    @Test
    @DisplayName("List customers returns collection payload")
    void listCustomers() throws Exception {
        List<CustomerDTO> customers = List.of(
                new CustomerDTO(1L, "Alpha", "IT000", "TC1", "alpha@example.com", "111", "Addr 1", Instant.EPOCH, Instant.EPOCH),
                new CustomerDTO(2L, "Beta", "IT001", "TC2", "beta@example.com", "222", "Addr 2", Instant.EPOCH, Instant.EPOCH)
        );
        when(customerService.findAll()).thenReturn(customers);

        mockMvc.perform(get("/api/customers"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Alpha"))
                .andExpect(jsonPath("$[1].vatNumber").value("IT001"));
    }

    @Test
    @DisplayName("Find customer by id returns payload")
    void findCustomerById() throws Exception {
        CustomerDTO customer = new CustomerDTO(5L, "Gamma", "IT777", "TC7", "gamma@example.com", "333", "Addr 3", Instant.EPOCH, Instant.EPOCH);
        when(customerService.findById(5L)).thenReturn(Optional.of(customer));

        mockMvc.perform(get("/api/customers/5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(5))
                .andExpect(jsonPath("$.name").value("Gamma"));
    }

    @Test
    @DisplayName("Create customer returns persisted entity")
    void createCustomer() throws Exception {
        CustomerDTO request = new CustomerDTO(null, "Delta", "IT888", "TC8", "delta@example.com", "444", "Addr 4", Instant.EPOCH, Instant.EPOCH);
        CustomerDTO saved = new CustomerDTO(9L, "Delta", "IT888", "TC8", "delta@example.com", "444", "Addr 4", Instant.EPOCH, Instant.EPOCH);
        when(customerService.create(any(CustomerDTO.class))).thenReturn(saved);

        mockMvc.perform(post("/api/customers")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(9))
                .andExpect(jsonPath("$.taxCode").value("TC8"));
    }

    @Test
    @DisplayName("Update customer returns 404 when missing")
    void updateCustomerNotFound() throws Exception {
        when(customerService.update(eq(12L), any(CustomerDTO.class))).thenReturn(Optional.empty());

        CustomerDTO request = new CustomerDTO(null, "Eta", "IT999", "TC9", "eta@example.com", "555", "Addr 5", Instant.EPOCH, Instant.EPOCH);

        mockMvc.perform(put("/api/customers/12")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Delete customer returns 404 when service reports failure")
    void deleteCustomerNotFound() throws Exception {
        when(customerService.delete(15L)).thenReturn(false);

        mockMvc.perform(delete("/api/customers/15"))
                .andExpect(status().isNotFound());
    }
}
