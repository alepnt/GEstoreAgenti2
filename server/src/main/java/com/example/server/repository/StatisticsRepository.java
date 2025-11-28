package com.example.server.repository;                                 // Package che contiene i repository personalizzati del backend.

import org.springframework.jdbc.core.RowMapper;                        // Interfaccia per mappare le righe del ResultSet in oggetti Java.
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource; // Classe per fornire parametri nominati nelle query SQL.
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate; // Template JDBC che supporta parametri nominati.
import org.springframework.stereotype.Repository;                      // Indica che la classe è un componente di accesso ai dati.

import java.math.BigDecimal;                                           // Rappresenta valori numerici con precisione arbitraria.
import java.util.List;                                                 // Supporta la gestione di liste di risultati.

@Repository                                                             // Rende la classe un bean Spring di tipo repository.
public class StatisticsRepository {                                    // Repository custom per aggregazioni statistiche.

    private final NamedParameterJdbcTemplate jdbcTemplate;             // Template centralizzato per eseguire query SQL con parametri nominati.

    public StatisticsRepository(NamedParameterJdbcTemplate jdbcTemplate) { // Costruttore con iniezione del template JDBC.
        this.jdbcTemplate = jdbcTemplate;                              // Assegna il template al campo interno.
    }

    public List<Integer> findAvailableYears(String paidStatus) {       // Restituisce gli anni disponibili in base allo stato di pagamento.
        String sql = """
                SELECT DISTINCT EXTRACT(YEAR FROM i."payment_date") AS payment_year
                FROM "invoices" i
                WHERE i."status" = :paidStatus AND i."payment_date" IS NOT NULL
                ORDER BY payment_year
                """;                                                   // Query SQL multi-linea per estrarre anni distinti dai pagamenti.
        MapSqlParameterSource params = new MapSqlParameterSource("paidStatus", paidStatus); // Parametro nominato per lo stato.
        return jdbcTemplate.query(sql, params, (rs, rowNum) -> rs.getInt("payment_year"));  // Esegue la query e mappa l'anno come intero.
    }

    public List<MonthlyAggregate> findMonthlyTotals(int year, String paidStatus) { // Aggrega i totali mensili di un anno.
        String sql = """
                SELECT EXTRACT(YEAR FROM i."payment_date") AS payment_year,
                       EXTRACT(MONTH FROM i."payment_date") AS payment_month,
                       SUM(i."amount") AS total_amount
                FROM "invoices" i
                WHERE i."status" = :paidStatus AND i."payment_date" IS NOT NULL AND EXTRACT(YEAR FROM i."payment_date") = :year
                GROUP BY EXTRACT(YEAR FROM i."payment_date"), EXTRACT(MONTH FROM i."payment_date")
                ORDER BY payment_month
                """;                                                   // Query che somma gli importi per mese e anno dei pagamenti.
        MapSqlParameterSource params = new MapSqlParameterSource()     // Collezione di parametri SQL nominati.
                .addValue("paidStatus", paidStatus)                    // Aggiunge filtro stato.
                .addValue("year", year);                               // Aggiunge filtro anno.

        RowMapper<MonthlyAggregate> mapper = (rs, rowNum) -> new MonthlyAggregate( // Mapper che costruisce un record MonthlyAggregate.
                rs.getInt("payment_year"),                            // Estrae l'anno.
                rs.getInt("payment_month"),                           // Estrae il mese.
                rs.getBigDecimal("total_amount")                      // Estrae il totale.
        );
        return jdbcTemplate.query(sql, params, mapper);               // Esegue la query con mappatura personalizzata.
    }

    public List<AgentAggregate> findAgentTotals(int year, String paidStatus) { // Aggrega i totali per agente e team.
        String sql = """
                SELECT a."id" AS agent_id,
                       u."display_name" AS agent_name,
                       t."id" AS team_id,
                       t."name" AS team_name,
                       SUM(i."amount") AS total_amount
                FROM "invoices" i
                         JOIN "contracts" c ON i."contract_id" = c."id"
                         JOIN "agents" a ON c."agent_id" = a."id"
                         JOIN "users" u ON a."user_id" = u."id"
                         JOIN "teams" t ON u."team_id" = t."id"
                WHERE i."status" = :paidStatus AND i."payment_date" IS NOT NULL AND EXTRACT(YEAR FROM i."payment_date") = :year
                GROUP BY a."id", u."display_name", t."id", t."name"
                ORDER BY total_amount DESC
                """;                                                   // Query che unisce invoice, contracts, agents, users e teams.

        MapSqlParameterSource params = new MapSqlParameterSource()     // Parametri nominati per filtrare anno e stato.
                .addValue("year", year)
                .addValue("paidStatus", paidStatus);

        RowMapper<AgentAggregate> mapper = (rs, rowNum) -> new AgentAggregate( // Mapper custom per agente.
                rs.getLong("agent_id"),                             // ID agente.
                rs.getString("agent_name"),                         // Nome agente.
                rs.getLong("team_id"),                              // ID team.
                rs.getString("team_name"),                          // Nome team.
                rs.getBigDecimal("total_amount")                    // Totale importi associati all'agente.
        );
        return jdbcTemplate.query(sql, params, mapper);              // Restituisce lista ordinata per totale.
    }

    public List<TeamAggregate> findTeamTotals(int year, String paidStatus) { // Aggrega i totali dei team.
        String sql = """
                SELECT t."id" AS team_id,
                       t."name" AS team_name,
                       SUM(i."amount") AS total_amount
                FROM "invoices" i
                         JOIN "contracts" c ON i."contract_id" = c."id"
                         JOIN "agents" a ON c."agent_id" = a."id"
                         JOIN "users" u ON a."user_id" = u."id"
                         JOIN "teams" t ON u."team_id" = t."id"
                WHERE i."status" = :paidStatus AND i."payment_date" IS NOT NULL AND EXTRACT(YEAR FROM i."payment_date") = :year
                GROUP BY t."id", t."name"
                ORDER BY total_amount DESC
                """;                                                   // Query che somma gli importi raggruppati per team.

        MapSqlParameterSource params = new MapSqlParameterSource()     // Parametri della query.
                .addValue("year", year)
                .addValue("paidStatus", paidStatus);

        RowMapper<TeamAggregate> mapper = (rs, rowNum) -> new TeamAggregate( // Mapper custom per team.
                rs.getLong("team_id"),                             // ID team.
                rs.getString("team_name"),                         // Nome team.
                rs.getBigDecimal("total_amount")                   // Totale importi del team.
        );
        return jdbcTemplate.query(sql, params, mapper);              // Restituisce aggregazioni team.
    }

    // Record che rappresenta l'aggregazione mensile.
    public record MonthlyAggregate(Integer paymentYear, Integer paymentMonth, BigDecimal totalAmount) {
        public Integer getYear() {                                    // Getter standardizzato dell'anno.
            return paymentYear;
        }
        public Integer getMonth() {                                   // Getter standardizzato del mese.
            return paymentMonth;
        }
        public BigDecimal getTotalAmount() {                          // Getter del totale.
            return totalAmount;
        }
    }

    // Record che rappresenta l'aggregazione per agente.
    public record AgentAggregate(Long agentId, String agentName, Long teamId, String teamName, BigDecimal totalAmount) {
        public Long getAgentId() {                                    // Getter ID agente.
            return agentId;
        }
        public String getAgentName() {                                // Getter nome agente.
            return agentName;
        }
        public Long getTeamId() {                                     // Getter ID team.
            return teamId;
        }
        public String getTeamName() {                                 // Getter nome team.
            return teamName;
        }
        public BigDecimal getTotalAmount() {                          // Getter totale importi.
            return totalAmount;
        }
    }

    // Record che rappresenta l'aggregazione per team.
    public record TeamAggregate(Long teamId, String teamName, BigDecimal totalAmount) {
        public Long getTeamId() {                                     // Getter ID team.
            return teamId;
        }
        public String getTeamName() {                                 // Getter nome team.
            return teamName;
        }
        public BigDecimal getTotalAmount() {                          // Getter totale importi.
            return totalAmount;
        }
    }
} // Fine classe StatisticsRepository.
