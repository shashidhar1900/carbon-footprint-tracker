package com.shashi.transportservice.model;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "transport_usage")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TransportUsage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String username;   // from JWT
    private String mode;       // BUS, BIKE, CAR
    private double distance;   // in km
    private String date;       // yyyy-MM-dd

}
