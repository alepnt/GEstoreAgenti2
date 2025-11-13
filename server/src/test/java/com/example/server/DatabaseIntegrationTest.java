package com.example.server;

import com.zaxxer.hikari.HikariDataSource;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.jdbc.core.JdbcTemplate;
import org.testcontainers.containers.MSSQLServerContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import javax.sql.DataSource;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Testcontainers
class DatabaseIntegrationTest {

    @Container
    @ServiceConnection
    static final MSSQLServerContainer<?> database =
            new MSSQLServerContainer<>("mcr.microsoft.com/mssql/server:2022-latest")
                    .acceptLicense();

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    void shouldSeedDefaultRolesAndTeams() {
        List<String> roleNames = jdbcTemplate.queryForList("SELECT name FROM roles", String.class);
        List<String> teamNames = jdbcTemplate.queryForList("SELECT name FROM teams", String.class);

        assertThat(roleNames)
                .contains("Agent", "Amministratore", "Responsabile Team", "Back Office");
        assertThat(teamNames)
                .contains("Vendite", "Vendite Nord", "Vendite Sud", "Supporto Clienti", "Marketing");
    }

    @Test
    void shouldConfigureHikariConnectionPool() {
        DataSource dataSource = jdbcTemplate.getDataSource();
        assertThat(dataSource).isInstanceOf(HikariDataSource.class);

        HikariDataSource hikariDataSource = (HikariDataSource) dataSource;
        assertThat(hikariDataSource.getPoolName()).isEqualTo("GestoreAgentiPool");
        assertThat(hikariDataSource.getMaximumPoolSize()).isEqualTo(10);
    }
}
