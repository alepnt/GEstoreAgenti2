package com.example.client.service;

import com.example.common.dto.ContractDTO;
import com.example.common.dto.DocumentHistoryDTO;
import com.example.common.dto.InvoiceDTO;
import com.example.common.dto.InvoicePaymentRequest;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.List;

/**
 * Gateway REST minimale verso il backend Spring Boot.
 */
public class BackendGateway {

    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;
    private final String baseUrl;

    public BackendGateway() {
        this("http://localhost:8080");
    }

    public BackendGateway(String baseUrl) {
        this.baseUrl = baseUrl;
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(5))
                .build();
        this.objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.findAndRegisterModules();
        objectMapper.configure(com.fasterxml.jackson.databind.SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
    }

    public List<InvoiceDTO> listInvoices() {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri("/api/invoices"))
                .GET()
                .build();
        return send(request, new TypeReference<>() {
        });
    }

    public InvoiceDTO createInvoice(InvoiceDTO invoice) {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri("/api/invoices"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(write(invoice), StandardCharsets.UTF_8))
                .build();
        return send(request, new TypeReference<>() {
        });
    }

    public InvoiceDTO updateInvoice(Long id, InvoiceDTO invoice) {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri("/api/invoices/" + id))
                .header("Content-Type", "application/json")
                .PUT(HttpRequest.BodyPublishers.ofString(write(invoice), StandardCharsets.UTF_8))
                .build();
        return send(request, new TypeReference<>() {
        });
    }

    public void deleteInvoice(Long id) {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri("/api/invoices/" + id))
                .DELETE()
                .build();
        send(request, new TypeReference<Void>() {
        });
    }

    public InvoiceDTO registerInvoicePayment(Long id, InvoicePaymentRequest paymentRequest) {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri("/api/invoices/" + id + "/payments"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(write(paymentRequest), StandardCharsets.UTF_8))
                .build();
        return send(request, new TypeReference<>() {
        });
    }

    public List<DocumentHistoryDTO> invoiceHistory(Long id) {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri("/api/invoices/" + id + "/history"))
                .GET()
                .build();
        return send(request, new TypeReference<>() {
        });
    }

    public List<ContractDTO> listContracts() {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri("/api/contracts"))
                .GET()
                .build();
        return send(request, new TypeReference<>() {
        });
    }

    public ContractDTO createContract(ContractDTO contract) {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri("/api/contracts"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(write(contract), StandardCharsets.UTF_8))
                .build();
        return send(request, new TypeReference<>() {
        });
    }

    public ContractDTO updateContract(Long id, ContractDTO contract) {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri("/api/contracts/" + id))
                .header("Content-Type", "application/json")
                .PUT(HttpRequest.BodyPublishers.ofString(write(contract), StandardCharsets.UTF_8))
                .build();
        return send(request, new TypeReference<>() {
        });
    }

    public void deleteContract(Long id) {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri("/api/contracts/" + id))
                .DELETE()
                .build();
        send(request, new TypeReference<Void>() {
        });
    }

    public List<DocumentHistoryDTO> contractHistory(Long id) {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri("/api/contracts/" + id + "/history"))
                .GET()
                .build();
        return send(request, new TypeReference<>() {
        });
    }

    private URI uri(String path) {
        return URI.create(baseUrl + path);
    }

    private String write(Object value) {
        try {
            return objectMapper.writeValueAsString(value);
        } catch (IOException e) {
            throw new IllegalStateException("Impossibile serializzare la richiesta", e);
        }
    }

    private <T> T send(HttpRequest request, TypeReference<T> typeReference) {
        try {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            int statusCode = response.statusCode();
            if (statusCode >= 200 && statusCode < 300) {
                if (typeReference.getType() == Void.class || response.body() == null || response.body().isBlank()) {
                    return null;
                }
                return objectMapper.readValue(response.body(), typeReference);
            }
            throw new IllegalStateException("Errore chiamata API: " + statusCode + " - " + response.body());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Chiamata interrotta", e);
        } catch (IOException e) {
            throw new IllegalStateException("Errore di comunicazione con il backend", e);
        }
    }
}
