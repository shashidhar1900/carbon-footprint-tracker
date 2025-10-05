package com.shashi.leaderboardservice.model;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;


@Data
public class LeaderBoard {

    private String username;
    private Double totalEmission;

    public LeaderBoard(String username, Double totalEmission) {
        this.username = username;
        this.totalEmission = totalEmission;
    }
}
