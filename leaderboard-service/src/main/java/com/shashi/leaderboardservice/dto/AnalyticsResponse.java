package com.shashi.leaderboardservice.dto;


import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AnalyticsResponse {
    String username;
    double totalEmission;
}
