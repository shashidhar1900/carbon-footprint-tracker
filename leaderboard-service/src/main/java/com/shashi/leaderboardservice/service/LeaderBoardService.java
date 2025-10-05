package com.shashi.leaderboardservice.service;

import com.shashi.leaderboardservice.dto.AnalyticsResponse;
import com.shashi.leaderboardservice.model.LeaderBoard;
import com.shashi.leaderboardservice.feign.AnalyticsClient;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LeaderBoardService {

    private final AnalyticsClient analyticsClient;

    public LeaderBoardService(AnalyticsClient analyticsClient) {
        this.analyticsClient = analyticsClient;
    }

    public ResponseEntity<List<LeaderBoard>> getTopPerformers(int top) {

        List<AnalyticsResponse> response = analyticsClient.getAnalyticsMonthly();
        List<LeaderBoard> leaderBoards = response.stream()
                .map(r -> new LeaderBoard(r.getUsername(), r.getTotalEmission()))
                .sorted((a, b) -> Double.compare(a.getTotalEmission(), b.getTotalEmission()))
                .limit(top)
                .toList();

        return ResponseEntity.ok(leaderBoards);

    }
}
