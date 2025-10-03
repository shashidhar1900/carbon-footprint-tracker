package com.shashi.foodservice.dto;

import lombok.Data;

@Data
public class FoodRequest {

    private String type;     // VEG, NON_VEG, JUNK
    private double quantity; // grams

}