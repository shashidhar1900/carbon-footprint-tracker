package com.shashi.analyticsservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.shashi.analyticsservice.feign.AuthClient;
import com.shashi.analyticsservice.model.UserMonthlyEnergyEmission;
import com.shashi.analyticsservice.model.UserMonthlyFoodEmission;
import com.shashi.analyticsservice.model.UserMonthlyTotalEmission;
import com.shashi.analyticsservice.model.UserMonthlyTransportEmission;
import com.shashi.analyticsservice.service.AnalyticsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class AnalyticsControllerTest {




    @Mock
    private AnalyticsService analyticsService;

    @Mock
    private AuthClient authClient;

    @InjectMocks
    private  AnalyticsController analyticsController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void init() {
        mockMvc = MockMvcBuilders.standaloneSetup(new AnalyticsController(analyticsService, authClient)).build();
        objectMapper = new ObjectMapper();
    }

    private UserMonthlyTotalEmission totalEmission;
    private UserMonthlyTransportEmission transportEmission;
    private UserMonthlyFoodEmission foodEmission;
    private UserMonthlyEnergyEmission energyEmission;

    @BeforeEach
    void setUp() {
        totalEmission = new UserMonthlyTotalEmission();
        transportEmission = new UserMonthlyTransportEmission();
        foodEmission = new UserMonthlyFoodEmission();
        energyEmission = new UserMonthlyEnergyEmission();
    }

    @Test
    void testGetMonthlyTotalEmission() throws Exception {
        List<UserMonthlyTotalEmission> emissions = Arrays.asList(totalEmission);
        when(analyticsService.getMonthlyTotalEmission(2024, 1))
                .thenReturn(new ResponseEntity<>(emissions, HttpStatus.OK));

        mockMvc.perform(get("/api/analytics/monthlyTotalEmission/2024/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void testGetMonthlyTransportEmissions() throws Exception {
        List<UserMonthlyTransportEmission> emissions = Arrays.asList(transportEmission);
        when(analyticsService.getMonthlyTransportEmissions(2024, 1))
                .thenReturn(new ResponseEntity<>(emissions, HttpStatus.OK));

        mockMvc.perform(get("/api/analytics/mothlyTransportEmission/2024/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void testGetMonthlyFoodEmissions() throws Exception {
        List<UserMonthlyFoodEmission> emissions = Arrays.asList(foodEmission);
        when(analyticsService.getMonthlyFoodEmissions(2024, 1))
                .thenReturn(new ResponseEntity<>(emissions, HttpStatus.OK));

        mockMvc.perform(get("/api/analytics/monthlyFoodEmission/2024/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void testGetMonthlyEnergyEmissions() throws Exception {
        List<UserMonthlyEnergyEmission> emissions = Arrays.asList(energyEmission);
        when(analyticsService.getMonthlyEnergyEmissions(2024, 1))
                .thenReturn(new ResponseEntity<>(emissions, HttpStatus.OK));

        mockMvc.perform(get("/api/analytics/monthlyEnergyEmission/2024/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void testDebugAuth() throws Exception {
        when(authClient.debug()).thenReturn("Auth Service Working");

        mockMvc.perform(get("/api/analytics/debug")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value("Auth says: Auth Service Working"));
    }

    @Test
    void testGetAnalyticsMonthly() throws Exception {
        doReturn(new ResponseEntity<>(Arrays.asList(totalEmission), HttpStatus.OK)).when(analyticsService)
                .getAnalyticsMonthly();

        mockMvc.perform(get("/api/analytics/monthly")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }
}