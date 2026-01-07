package com.shashi.leaderboardservice.service;

import com.shashi.leaderboardservice.dto.AnalyticsResponse;
import com.shashi.leaderboardservice.feign.AnalyticsClient;
import com.shashi.leaderboardservice.model.LeaderBoard;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class LeaderBoardServiceTest {

    @Mock
    AnalyticsClient analyticsClient;

    @InjectMocks
    LeaderBoardService leaderBoardService;

    private final String MOCK_USER = "shashi_user";
    private final String TODAY = LocalDate.now().toString();


    @Test
    void getTopPerformers_shouldReturnListOfTopPerformers() {
        List<AnalyticsResponse> analyticsResponses = Arrays.asList(
                new AnalyticsResponse(MOCK_USER, 100.0),
                new AnalyticsResponse(MOCK_USER+"1", 150.0),
                new AnalyticsResponse(MOCK_USER+"2", 80.0),
                new AnalyticsResponse(MOCK_USER+"3", 120.0));
        when(analyticsClient.getAnalyticsMonthly()).thenReturn(analyticsResponses);
        ResponseEntity<List<LeaderBoard>> response = leaderBoardService.getTopPerformers(2);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        List<LeaderBoard> body = response.getBody();
        assertNotNull(body);
        assertEquals(2, body.size());

        // service sorts ascending and then limits, so lowest emissions first
        assertEquals(MOCK_USER + "2", body.get(0).getUsername());
        assertEquals(80.0, body.get(0).getTotalEmission());
        assertEquals(MOCK_USER, body.get(1).getUsername());
        assertEquals(100.0, body.get(1).getTotalEmission());

    }

    @Test
    void getTopPerformers_shouldHandleEmptyAnalyticsList() {
        when(analyticsClient.getAnalyticsMonthly()).thenReturn(Collections.emptyList());

        ResponseEntity<List<LeaderBoard>> response = leaderBoardService.getTopPerformers(3);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isEmpty());
    }

}
