package com.shashi.leaderboardservice.model;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;


@Data
public class LeaderBoard {

    private String username;
    private Double totalEmission;
    int rank;

    public LeaderBoard(String username, Double totalEmission) {
        this.username = username;
        this.totalEmission = totalEmission;
    }

    public LeaderBoard(String username, Double totalEmission, int rank) {
        this.username = username;
        this.totalEmission = totalEmission;
        this.rank = rank;
    }
}
