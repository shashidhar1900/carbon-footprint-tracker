package com.shashi.analyticsservcie.service;

import com.shashi.analyticsservcie.dto.AnalyticsResponse;
import com.shashi.analyticsservcie.dto.EnergyResponse;
import com.shashi.analyticsservcie.dto.FoodResponse;
import com.shashi.analyticsservcie.dto.TransportResponse;
import com.shashi.analyticsservcie.feign.AuthClient;
import com.shashi.analyticsservcie.feign.EnergyClient;
import com.shashi.analyticsservcie.feign.FoodClient;
import com.shashi.analyticsservcie.feign.TransportClient;
import com.shashi.analyticsservcie.model.*;
import com.shashi.analyticsservcie.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import jakarta.servlet.http.HttpServletRequest;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AnalyticsService {

    private final AuthClient authClient;
    private final TransportClient transportClient;
    private final EnergyClient energyClient;
    private final FoodClient foodClient;
    private final UserMonthlyEmissionRepository userMonthlyEmissionRepository;
    private final UserMonthlyTransportEmissionRepository userMonthlyTransportEmissionRepository;
    private final UserMonthlyFoodEmissionRepository userMonthlyFoodEmissionRepository;
    private final UserMonthlyEnergyEmissionRepository userMonthlyEnergyEmissionRepository;
    private final HttpServletRequest request; // Injected to get JWT

    private String getJwtHeader() {
        return request.getHeader("Authorization");
    }



    public ResponseEntity<List<UserMonthlyTotalEmission>> getMonthlyTotalEmission(int year, int month) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            System.out.println("Authentication object is null.");
        } else {
            System.out.println("Principal: " + authentication.getPrincipal());
            System.out.println("Credentials: " + authentication.getCredentials()); // JWT is usually here!
        }
        List<String> usernames = authClient.getAllUsernames();
        List<UserMonthlyTotalEmission> emissions = new ArrayList<>();

        for (String username : usernames) {
            TransportResponse transportData = transportClient.getMonthlyTransport(username, year, month);
            EnergyResponse energyData = energyClient.getMonthlyEnergy(username, year, month);
            FoodResponse foodData = foodClient.getMonthlyFood(username, year, month);

            double totalEmission = 0.0;
            if (transportData != null) totalEmission += transportData.getTotalTransportCarbonEmission();
            if (energyData != null) totalEmission += energyData.getTotalEnergyCarbonEmission();
            if (foodData != null) totalEmission += foodData.getTotalFoodCarbonEmission();

            UserMonthlyTotalEmission userEmission = new UserMonthlyTotalEmission();
            userEmission.setUsername(username);
            userEmission.setYear(year);
            userEmission.setMonth(month);
            userEmission.setTotalEmission(totalEmission);

            emissions.add(userEmission);
        }


        return ResponseEntity.ok(emissions);
    }

    // Similar changes for other methods:
    public ResponseEntity<List<UserMonthlyTransportEmission>> getMonthlyTransportEmissions(int year, int month) {
        List<String> usernames = authClient.getAllUsernames();
        List<UserMonthlyTransportEmission> emissions = new ArrayList<>();
        for (String username : usernames) {
            TransportResponse transportData = transportClient.getMonthlyTransport(username, year, month);
            double totalEmission = transportData != null ? transportData.getTotalTransportCarbonEmission() : 0.0;
            UserMonthlyTransportEmission userEmission = new UserMonthlyTransportEmission();
            userEmission.setUsername(username);
            userEmission.setYear(year);
            userEmission.setMonth(month);
            userEmission.setTotalTransportEmission(totalEmission);
            emissions.add(userEmission);
        }
        userMonthlyTransportEmissionRepository.saveAll(emissions);
        return ResponseEntity.ok(emissions);
    }

    public ResponseEntity<List<UserMonthlyFoodEmission>> getMonthlyFoodEmissions(int year, int month) {
        List<String> usernames = authClient.getAllUsernames();
        List<UserMonthlyFoodEmission> emissions = new ArrayList<>();
        for (String username : usernames) {
            FoodResponse foodData = foodClient.getMonthlyFood(username, year, month);
            double totalEmission = foodData != null ? foodData.getTotalFoodCarbonEmission() : 0.0;
            UserMonthlyFoodEmission userEmission = new UserMonthlyFoodEmission();
            userEmission.setUsername(username);
            userEmission.setYear(year);
            userEmission.setMonth(month);
            userEmission.setTotalFoodEmission(totalEmission);
            emissions.add(userEmission);
        }
        userMonthlyFoodEmissionRepository.saveAll(emissions);
        return ResponseEntity.ok(emissions);
    }

    public ResponseEntity<List<UserMonthlyEnergyEmission>> getMonthlyEnergyEmissions(int year, int month) {
        List<String> usernames = authClient.getAllUsernames();
        List<UserMonthlyEnergyEmission> emissions = new ArrayList<>();
        for (String username : usernames) {
            EnergyResponse energyData = energyClient.getMonthlyEnergy(username, year, month);
            double totalEmission = energyData != null ? energyData.getTotalEnergyCarbonEmission() : 0.0;
            UserMonthlyEnergyEmission userEmission = new UserMonthlyEnergyEmission();
            userEmission.setUsername(username);
            userEmission.setYear(year);
            userEmission.setMonth(month);
            userEmission.setTotalEnergyEmission(totalEmission);
            emissions.add(userEmission);
        }
        userMonthlyEnergyEmissionRepository.saveAll(emissions);
        return ResponseEntity.ok(emissions);
    }

    public ResponseEntity<?> getAnalyticsMonthly() {

        LocalDate now = LocalDate.now();
        List<UserMonthlyTotalEmission> totalEmissions = getMonthlyTotalEmission(now.getYear(), now.getMonthValue()).getBody();
        List<AnalyticsResponse> responses = totalEmissions.stream()
                .map(e -> new AnalyticsResponse(e.getUsername(), e.getTotalEmission()))
                .toList();

        return ResponseEntity.ok(responses);

    }
}
