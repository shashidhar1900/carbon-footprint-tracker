package com.shashi.energyservice.service;

import com.shashi.energyservice.constants.EnergyConstants;
import com.shashi.energyservice.dto.EnergyRequest;
import com.shashi.energyservice.dto.EnergyResponse;
import com.shashi.energyservice.model.EnergyUsage;
import com.shashi.energyservice.repository.EnergyRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EnergyServiceTest {

    @Mock
    private EnergyRepository energyRepository;

    @Mock(strictness = Mock.Strictness.LENIENT)
    private SecurityContext securityContext;

    @Mock(strictness = Mock.Strictness.LENIENT)
    private Authentication authentication;

    @InjectMocks
    private EnergyService energyService;

    private static final String TEST_USERNAME = "testuser";
    private static final String TODAY = LocalDate.now().toString();
    private static final double TEST_UNITS = 21.4;

    @BeforeEach
    void setUp() {
        // Mock SecurityContextHolder to return test username
        when(authentication.getName()).thenReturn(TEST_USERNAME);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
    }

    @Test
    void testGetCurrentUsername_WhenAuthenticationExists_ReturnsUsername() {
        String username = energyService.getCurrentUsername();
        assertEquals(TEST_USERNAME, username);
        verify(authentication, times(1)).getName();
    }

    @Test
    void testGetCurrentUsername_WhenAuthenticationIsNull_ReturnsNull() {
        when(securityContext.getAuthentication()).thenReturn(null);
        String username = energyService.getCurrentUsername();
        assertNull(username);
    }

    @Test
    void testAddEnergy_WhenValidRequest_SavesAndReturnsSuccess() {
        EnergyRequest request = new EnergyRequest();
        request.setUnits(TEST_UNITS);

        EnergyUsage savedUsage = EnergyUsage.builder()
                .username(TEST_USERNAME)
                .units(TEST_UNITS)
                .date(TODAY)
                .build();

        when(energyRepository.save(any(EnergyUsage.class))).thenReturn(savedUsage);

        ResponseEntity<?> response = energyService.addEnergy(request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Energy record added!", response.getBody());
        verify(energyRepository, times(1)).save(any(EnergyUsage.class));
    }

    @Test
    void testGetEnergyHistory_WhenRecordsExist_ReturnsHistory() {
        List<EnergyUsage> energyUsages = new ArrayList<>();
        EnergyUsage usage1 = EnergyUsage.builder()
                .username(TEST_USERNAME)
                .units(10.0)
                .date(TODAY)
                .build();
        energyUsages.add(usage1);

        when(energyRepository.findByUsername(TEST_USERNAME)).thenReturn(energyUsages);

        ResponseEntity<?> response = energyService.getEnergyHistory();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        verify(energyRepository, times(1)).findByUsername(TEST_USERNAME);
    }

    @Test
    void testGetEnergyHistory_WhenNoRecords_ReturnsEmptyList() {
        List<EnergyUsage> emptyList = new ArrayList<>();
        when(energyRepository.findByUsername(TEST_USERNAME)).thenReturn(emptyList);

        ResponseEntity<?> response = energyService.getEnergyHistory();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(emptyList, response.getBody());
        verify(energyRepository, times(1)).findByUsername(TEST_USERNAME);
    }

    @Test
    void testGetMonthlyEnergy_WhenRecordsMatchMonthAndYear_ReturnsCalculatedEmission() {
        int year = 2025;
        int month = 1;

        List<EnergyUsage> energyUsages = new ArrayList<>();
        EnergyUsage usage1 = EnergyUsage.builder()
                .username(TEST_USERNAME)
                .units(10.0)
                .date("2025-01-10")
                .build();
        EnergyUsage usage2 = EnergyUsage.builder()
                .username(TEST_USERNAME)
                .units(20.0)
                .date("2025-01-20")
                .build();
        energyUsages.add(usage1);
        energyUsages.add(usage2);

        when(energyRepository.findByUsername(TEST_USERNAME)).thenReturn(energyUsages);

        ResponseEntity<EnergyResponse> response = energyService.getMonthlyEnergy(TEST_USERNAME, year, month);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(TEST_USERNAME, response.getBody().getUsername());
        assertEquals(year, response.getBody().getYear());
        assertEquals(month, response.getBody().getMonth());
        double expectedEmission = (10.0 + 20.0) * EnergyConstants.ELECTRICITY_EMISSION_FACTOR;
        assertEquals(expectedEmission, response.getBody().getTotalEnergyCarbonEmission(), 0.01);
        verify(energyRepository, times(1)).findByUsername(TEST_USERNAME);
    }

    @Test
    void testGetMonthlyEnergy_WhenNoMatchingRecords_ReturnsZeroEmission() {
        int year = 2025;
        int month = 1;

        List<EnergyUsage> energyUsages = new ArrayList<>();
        EnergyUsage usage1 = EnergyUsage.builder()
                .username(TEST_USERNAME)
                .units(10.0)
                .date("2024-12-10") // Different month
                .build();
        energyUsages.add(usage1);

        when(energyRepository.findByUsername(TEST_USERNAME)).thenReturn(energyUsages);

        ResponseEntity<EnergyResponse> response = energyService.getMonthlyEnergy(TEST_USERNAME, year, month);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(0.0, response.getBody().getTotalEnergyCarbonEmission(), 0.01);
        verify(energyRepository, times(1)).findByUsername(TEST_USERNAME);
    }

    @Test
    void testUpdateEnergy_WhenRecordExists_UpdatesAndReturnsSuccess() {
        EnergyRequest request = new EnergyRequest();
        request.setUnits(25.0);

        EnergyUsage existingUsage = EnergyUsage.builder()
                .username(TEST_USERNAME)
                .units(TEST_UNITS)
                .date(TODAY)
                .build();

        when(energyRepository.findByUsernameAndDate(TEST_USERNAME, TODAY)).thenReturn(existingUsage);
        when(energyRepository.save(any(EnergyUsage.class))).thenReturn(existingUsage);

        ResponseEntity<?> response = energyService.updateEnergy(request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Energy record updated!", response.getBody());
        verify(energyRepository, times(1)).findByUsernameAndDate(TEST_USERNAME, TODAY);
        verify(energyRepository, times(1)).save(any(EnergyUsage.class));
    }

    @Test
    void testUpdateEnergy_WhenRecordDoesNotExist_ReturnsBadRequest() {
        EnergyRequest request = new EnergyRequest();
        request.setUnits(25.0);

        when(energyRepository.findByUsernameAndDate(TEST_USERNAME, TODAY)).thenReturn(null);

        ResponseEntity<?> response = energyService.updateEnergy(request);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("No energy record found for today. Please add one first.", response.getBody());
        verify(energyRepository, times(1)).findByUsernameAndDate(TEST_USERNAME, TODAY);
        verify(energyRepository, never()).save(any(EnergyUsage.class));
    }

    @Test
    void testDeleteEnergy_WhenRecordExists_DeletesAndReturnsSuccess() {
        EnergyUsage existingUsage = EnergyUsage.builder()
                .username(TEST_USERNAME)
                .units(TEST_UNITS)
                .date(TODAY)
                .build();

        when(energyRepository.findByUsernameAndDate(TEST_USERNAME, TODAY)).thenReturn(existingUsage);

        ResponseEntity<?> response = energyService.deleteEnergy();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Energy record deleted!", response.getBody());
        verify(energyRepository, times(1)).findByUsernameAndDate(TEST_USERNAME, TODAY);
        verify(energyRepository, times(1)).delete(existingUsage);
    }

    @Test
    void testDeleteEnergy_WhenRecordDoesNotExist_ReturnsBadRequest() {
        when(energyRepository.findByUsernameAndDate(TEST_USERNAME, TODAY)).thenReturn(null);

        ResponseEntity<?> response = energyService.deleteEnergy();

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("No energy record found for today.", response.getBody());
        verify(energyRepository, times(1)).findByUsernameAndDate(TEST_USERNAME, TODAY);
        verify(energyRepository, never()).delete(any(EnergyUsage.class));
    }
}

