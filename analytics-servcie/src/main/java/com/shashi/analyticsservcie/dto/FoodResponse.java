package com.shashi.analyticsservcie.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FoodResponse {
    private String username;
    private int year;
    private int month;
    private double totalFoodCarbonEmission;
}
