package com.shashi.leaderboardservice.controller;


import com.shashi.leaderboardservice.model.LeaderBoard;
import com.shashi.leaderboardservice.service.LeaderBoardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/leaderboard")
@RequiredArgsConstructor
public class LeaderBoardController {

    private final LeaderBoardService leaderBoardService;

    @GetMapping("monthly/top/{top}")
    public ResponseEntity<List<LeaderBoard>> getGlobalTopLeaderboard(@PathVariable int top) {
        return leaderBoardService.getTopPerformers(top);
    }

    @GetMapping("monthly/last/{last}")
    public ResponseEntity<List<LeaderBoard>> getGlobalLastLeaderboard(@PathVariable int last) {
        return leaderBoardService.getLastPerformers(last);
    }

    @GetMapping("monthly/rank/{username}")
    public ResponseEntity<LeaderBoard> getUSerRank(@PathVariable String username) {
        return leaderBoardService.getUserRank(username);
    }
}
