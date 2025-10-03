package com.shashi.energyservice.model;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "energy_usage")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EnergyUsage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String username;  // from JWT
    private double units;     // electricity in kWh
    private String date;      // yyyy-MM-dd
}
