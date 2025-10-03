package com.shashi.analyticsservcie.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "user_monthly_emissions")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserMonthlyTotalEmission {


    @Id
    private String username;
    private int year;
    private int month;
    private double totalEmission; // transport + energy + food
}

