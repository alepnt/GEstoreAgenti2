package com.example.server.service;

import com.example.common.dto.AgentStatisticsDTO;
import com.example.common.dto.TeamStatisticsDTO;
import com.example.server.repository.StatisticsRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class StatisticsServiceTest {

    @Mock
    private StatisticsRepository statisticsRepository;

    @Mock
    private CommissionService commissionService;

    private StatisticsService statisticsService;

    @BeforeEach
    void setUp() {
        statisticsService = InstantiationService.statisticsService(statisticsRepository, commissionService);
    }

    @Test
    void agentStatisticsUsesCurrentYearWhenDatabaseIsEmpty() {
        when(statisticsRepository.findAvailableYears("PAID")).thenReturn(List.of());

        AgentStatisticsDTO result = statisticsService.agentStatistics(null);

        int currentYear = result.year();
        assertThat(result.years()).containsExactly(currentYear);
        assertThat(result.monthlyTotals()).isEmpty();
        assertThat(result.agentTotals()).isEmpty();
    }

    @Test
    void agentStatisticsCachesComputedResultsPerYearAndYearsList() {
        List<Integer> availableYears = List.of(2021, 2022);
        StatisticsRepository.MonthlyAggregate january = new StatisticsRepository.MonthlyAggregate(2021, 1, new BigDecimal("100"));
        StatisticsRepository.MonthlyAggregate march = new StatisticsRepository.MonthlyAggregate(2021, 3, new BigDecimal("200"));
        StatisticsRepository.AgentAggregate agent = new StatisticsRepository.AgentAggregate(7L, "Mario", 3L, "Nord", new BigDecimal("300"));

        when(statisticsRepository.findAvailableYears("PAID")).thenReturn(availableYears);
        when(statisticsRepository.findMonthlyTotals(2021, "PAID")).thenReturn(List.of(march, january));
        when(statisticsRepository.findAgentTotals(2021, "PAID")).thenReturn(List.of(agent));
        when(commissionService.applyDefaultCommissionRate(any())).thenAnswer(invocation -> ((BigDecimal) invocation.getArgument(0))
                .multiply(new BigDecimal("0.10")).setScale(2));
        when(commissionService.calculateAgentCommission(eq(3L), eq(7L), any())).thenReturn(new BigDecimal("33.33"));

        AgentStatisticsDTO firstCall = statisticsService.agentStatistics(2021);
        AgentStatisticsDTO secondCall = statisticsService.agentStatistics(2021);

        assertThat(firstCall.year()).isEqualTo(2021);
        assertThat(firstCall.years()).containsExactlyElementsOf(availableYears);
        assertThat(firstCall.monthlyTotals()).extracting("month", "commission")
                .containsExactly(tuple(1, new BigDecimal("10.00")), tuple(3, new BigDecimal("20.00")));
        assertThat(firstCall.agentTotals()).extracting("agentId", "commission")
                .containsExactly(tuple(7L, new BigDecimal("33.33")));
        assertThat(secondCall).isSameAs(firstCall);

        verify(statisticsRepository, times(2)).findAvailableYears("PAID");
        verify(statisticsRepository, times(1)).findMonthlyTotals(2021, "PAID");
        verify(statisticsRepository, times(1)).findAgentTotals(2021, "PAID");
    }

    @Test
    void teamStatisticsFallsBackToLatestYearAndResetsCache() {
        List<Integer> availableYears = List.of(2019, 2020);
        StatisticsRepository.TeamAggregate aggregate = new StatisticsRepository.TeamAggregate(5L, "Centro", new BigDecimal("500"));

        when(statisticsRepository.findAvailableYears("PAID")).thenReturn(availableYears);
        when(statisticsRepository.findTeamTotals(2020, "PAID")).thenReturn(List.of(aggregate));
        when(commissionService.calculateTeamCommission(5L, new BigDecimal("500"))).thenReturn(new BigDecimal("60.00"));

        TeamStatisticsDTO firstCall = statisticsService.teamStatistics(null);
        TeamStatisticsDTO secondCall = statisticsService.teamStatistics(2018);

        assertThat(firstCall.year()).isEqualTo(2020);
        assertThat(firstCall.teamTotals()).extracting("teamId", "commission")
                .containsExactly(tuple(5L, new BigDecimal("60.00")));
        assertThat(secondCall).isSameAs(firstCall);

        statisticsService.clearCache();

        statisticsService.teamStatistics(null);

        verify(statisticsRepository, times(3)).findAvailableYears("PAID");
        verify(statisticsRepository, times(2)).findTeamTotals(2020, "PAID");
    }
}
