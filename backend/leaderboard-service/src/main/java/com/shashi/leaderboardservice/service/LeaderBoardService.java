package com.shashi.leaderboardservice.service;

import java.util.concurrent.atomic.AtomicInteger;

import com.shashi.leaderboardservice.dto.AnalyticsResponse;
import com.shashi.leaderboardservice.model.LeaderBoard;
import com.shashi.leaderboardservice.feign.AnalyticsClient;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Service
public class LeaderBoardService {

    private final AnalyticsClient analyticsClient;

    public LeaderBoardService(AnalyticsClient analyticsClient) {
        this.analyticsClient = analyticsClient;
    }

    @Cacheable("analyticsData")
    public List<AnalyticsResponse> getAnalyticsData() {
        return analyticsClient.getAnalyticsMonthly();
    }

    public ResponseEntity<List<LeaderBoard>> getTopPerformers(int top) {

        List<AnalyticsResponse> response = getAnalyticsData();
//        List<LeaderBoard> leaderBoards = response.stream()
//                .map(r -> new LeaderBoard(r.getUsername(), r.getTotalEmission()))
//                .sorted((a, b) -> Double.compare(a.getTotalEmission(), b.getTotalEmission()))
//                .limit(top)
//                .toList();
        List<LeaderBoard> leaderBoards = response.stream()
                .map(r -> new LeaderBoard(r.getUsername(), r.getTotalEmission()))
                .sorted((a, b) -> Double.compare(a.getTotalEmission(), b.getTotalEmission()))
                .limit(top)
                .collect(Collectors.toList());

        AtomicInteger rankCounter = new AtomicInteger(1);
        leaderBoards.forEach(entry -> entry.setRank(rankCounter.getAndIncrement()));

        return ResponseEntity.ok(leaderBoards);

    }

    public ResponseEntity<List<LeaderBoard>> getLastPerformers(int last) {

        List<AnalyticsResponse> response = getAnalyticsData();
//        List<LeaderBoard> leaderBoards = response.stream()
//                .map(r -> new LeaderBoard(r.getUsername(), r.getTotalEmission()))
//                .sorted((a, b) -> Double.compare(b.getTotalEmission(), a.getTotalEmission()))
//                .limit(last)
//                .toList();

        List<LeaderBoard> leaderBoards = response.stream()
                .map(r -> new LeaderBoard(r.getUsername(), r.getTotalEmission()))
                .sorted((a, b) -> Double.compare(b.getTotalEmission(), a.getTotalEmission()))
                .limit(last)
                .collect(Collectors.toList());

        AtomicInteger rankCounter = new AtomicInteger(1);
        leaderBoards.forEach(entry -> entry.setRank(rankCounter.getAndIncrement()));


        return ResponseEntity.ok(leaderBoards);

    }

    public ResponseEntity<LeaderBoard> getUserRank(String username) {

        List<AnalyticsResponse> response = getAnalyticsData();
        List<LeaderBoard> leaderBoards = response.stream()
                .map(r -> new LeaderBoard(r.getUsername(), r.getTotalEmission()))
                .sorted((a, b) -> Double.compare(a.getTotalEmission(), b.getTotalEmission()))
                .toList();

        int rank = 1;
        for (LeaderBoard leaderBoard : leaderBoards) {
            if (leaderBoard.getUsername().equals(username)) {
                return ResponseEntity.ok(new LeaderBoard(username, leaderBoard.getTotalEmission(), rank));
            }
            rank++;
        }

        return ResponseEntity.notFound().build();
    }
}
