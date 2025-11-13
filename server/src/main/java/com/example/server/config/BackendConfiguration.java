package com.example.server.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Clock;

@Configuration
public class BackendConfiguration {

    @Bean
    public Clock systemClock() {
        return Clock.systemUTC();
    }
}
