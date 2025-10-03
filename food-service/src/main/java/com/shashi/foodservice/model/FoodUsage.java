package com.shashi.foodservice.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "food_usage")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FoodUsage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String username;   // from JWT
    private String type;       // VEG, NON_VEG, JUNK
    private double quantity;   // grams
    private String date;       // yyyy-MM-dd

}
