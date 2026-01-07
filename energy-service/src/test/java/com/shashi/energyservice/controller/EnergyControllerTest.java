package com.shashi.energyservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.shashi.energyservice.dto.EnergyRequest;
import com.shashi.energyservice.dto.EnergyResponse;
import com.shashi.energyservice.service.EnergyService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;


@ExtendWith(MockitoExtension.class)
class EnergyControllerTest {



    @Mock
    private EnergyService energyService;

    @InjectMocks
    private EnergyController energyController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        // Setup code if needed
        mockMvc = MockMvcBuilders.standaloneSetup(energyController).build();
        objectMapper = new ObjectMapper();
    }

    @Test
    void shouldAddEnergyAndReturnSuccess() throws Exception {
        EnergyRequest request = new EnergyRequest();
        request.setUnits(21.4);

        doReturn(ResponseEntity.ok("Energy added successfully")).when(energyService).addEnergy(any(EnergyRequest.class));

        mockMvc.perform(post("/api/energy/add")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Energy added successfully")));

        verify(energyService, times(1)).addEnergy(any(EnergyRequest.class));
    }

    @Test
    void shouldUpdateEnergyAndReturnSuccess() throws Exception {
        EnergyRequest request = new EnergyRequest();
        request.setUnits(15.5);

        doReturn(ResponseEntity.ok("Energy updated successfully")).when(energyService).updateEnergy(any(EnergyRequest.class));

        mockMvc.perform(put("/api/energy/update")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Energy updated successfully")));

        verify(energyService, times(1)).updateEnergy(any(EnergyRequest.class));
    }

    @Test
    void shouldDeleteEnergyAndReturnSuccess() throws Exception {
        doReturn(ResponseEntity.ok("Energy deleted successfully")).when(energyService).deleteEnergy();

        mockMvc.perform(delete("/api/energy/delete")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Energy deleted successfully")));

        verify(energyService, times(1)).deleteEnergy();
    }

    @Test
    void shouldGetEnergyHistoryAndReturnSuccess() throws Exception {
        doReturn(ResponseEntity.ok("Energy history retrieved")).when(energyService).getEnergyHistory();

        mockMvc.perform(get("/api/energy/history")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Energy history retrieved")));

        verify(energyService, times(1)).getEnergyHistory();
    }

    @Test
    void shouldGetMonthlyEnergyAndReturnResponse() throws Exception {
        String username = "testuser";
        int year = 2025;
        int month = 1;

        EnergyResponse response = new EnergyResponse();
        response.setUsername(username);
        response.setYear(year);
        response.setMonth(month);
        response.setTotalEnergyCarbonEmission(45.67);

        doReturn(ResponseEntity.ok(response)).when(energyService).getMonthlyEnergy(username, year, month);

        mockMvc.perform(get("/api/energy/history/{username}/{year}/{month}", username, year, month)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value(username))
                .andExpect(jsonPath("$.year").value(year))
                .andExpect(jsonPath("$.month").value(month))
                .andExpect(jsonPath("$.totalEnergyCarbonEmission").value(45.67));

        verify(energyService, times(1)).getMonthlyEnergy(username, year, month);
    }
}