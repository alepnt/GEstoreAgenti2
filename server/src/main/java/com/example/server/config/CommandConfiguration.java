package com.example.server.config;

import com.example.common.command.CommandBus;
import com.example.common.command.CommandContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configurazione condivisa del CommandBus per orchestrare i comandi CRUD.
 */
@Configuration
public class CommandConfiguration {

    @Bean
    public CommandContext commandContext() {
        return new CommandContext();
    }

    @Bean
    public CommandBus commandBus(CommandContext context) {
        return new CommandBus(context);
    }

    @Bean
    public java.time.Clock systemClock() {
        return java.time.Clock.systemUTC();
    }
}
