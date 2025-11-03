package com.shashi.analyticsservcie.controller;

import com.shashi.analyticsservcie.feign.AuthClient;
import com.shashi.analyticsservcie.model.UserMonthlyEnergyEmission;
import com.shashi.analyticsservcie.model.UserMonthlyFoodEmission;
import com.shashi.analyticsservcie.model.UserMonthlyTotalEmission;
import com.shashi.analyticsservcie.model.UserMonthlyTransportEmission;
import com.shashi.analyticsservcie.service.AnalyticsService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/analytics")
@RequiredArgsConstructor
public class AnalyticsController {

    @Autowired
    private final AnalyticsService analyticsService;

    private final AuthClient authClient;

    @GetMapping("/monthlyTotalEmission/{year}/{month}")
    public ResponseEntity<List<UserMonthlyTotalEmission>> getMonthlyTotalEmission(@PathVariable int year, @PathVariable int month) {

        return analyticsService.getMonthlyTotalEmission(year, month);

    }

    @GetMapping("/mothlyTransportEmission/{year}/{month}" )
    public ResponseEntity<List<UserMonthlyTransportEmission>> getMonthlyTransportEmissions(@PathVariable int year, @PathVariable int month) {
        return analyticsService.getMonthlyTransportEmissions(year, month);
    }

    @GetMapping("/monthlyFoodEmission/{year}/{month}" )
    public ResponseEntity<List<UserMonthlyFoodEmission>> getMonthlyFoodEmissions(@PathVariable int year, @PathVariable int month) {
        return analyticsService.getMonthlyFoodEmissions(year, month);
    }

    @GetMapping("/monthlyEnergyEmission/{year}/{month}" )
    public ResponseEntity<List<UserMonthlyEnergyEmission>> getMonthlyEnergyEmissions(@PathVariable int year, @PathVariable int month) {
        return analyticsService.getMonthlyEnergyEmissions(year, month);
    }

    @GetMapping("/debug")
    public ResponseEntity<String> debugAuth() {
        return ResponseEntity.ok("Auth says: " + authClient.debug());
    }

    @GetMapping("/monthly")
    public ResponseEntity<?> getAnalyticsMonthly() {
        return analyticsService.getAnalyticsMonthly();
    }

}

