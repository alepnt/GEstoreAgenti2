package com.example.server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

/**
 * Entry point Spring Boot per il modulo server.
 */
@SpringBootApplication
@EnableCaching
public class GestoreAgentiServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(GestoreAgentiServerApplication.class, args);
    }
}
