package com.shashi.analyticsservcie.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

@Entity
@Data
@Table(name = "user_monthly_food_emissions")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserMonthlyFoodEmission {

    @Id
    private String username;
    private int year;
    private int month;
    private double totalFoodEmission; // in kg CO2e
}
