package com.shashi.analyticsservice.service;

 import com.shashi.analyticsservice.dto.AnalyticsResponse;
import com.shashi.analyticsservice.dto.EnergyResponse;
import com.shashi.analyticsservice.dto.FoodResponse;
import com.shashi.analyticsservice.dto.TransportResponse;
import com.shashi.analyticsservice.feign.AuthClient;
import com.shashi.analyticsservice.feign.EnergyClient;
import com.shashi.analyticsservice.feign.FoodClient;
import com.shashi.analyticsservice.feign.TransportClient;
import com.shashi.analyticsservice.model.UserMonthlyEnergyEmission;
import com.shashi.analyticsservice.model.UserMonthlyFoodEmission;
import com.shashi.analyticsservice.model.UserMonthlyTotalEmission;
import com.shashi.analyticsservice.model.UserMonthlyTransportEmission;
import com.shashi.analyticsservice.repository.UserMonthlyEmissionRepository;
import com.shashi.analyticsservice.repository.UserMonthlyEnergyEmissionRepository;
import com.shashi.analyticsservice.repository.UserMonthlyFoodEmissionRepository;
import com.shashi.analyticsservice.repository.UserMonthlyTransportEmissionRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
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
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("AnalyticsService Unit Tests")
class AnalyticsServiceTest {

    @Mock
    private AuthClient authClient;

    @Mock
    private TransportClient transportClient;

    @Mock
    private EnergyClient energyClient;

    @Mock
    private FoodClient foodClient;

    @Mock
    private UserMonthlyEmissionRepository userMonthlyEmissionRepository;

    @Mock
    private UserMonthlyTransportEmissionRepository userMonthlyTransportEmissionRepository;

    @Mock
    private UserMonthlyFoodEmissionRepository userMonthlyFoodEmissionRepository;

    @Mock
    private UserMonthlyEnergyEmissionRepository userMonthlyEnergyEmissionRepository;

    @Mock
    private HttpServletRequest request;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private AnalyticsService analyticsService;

    private List<String> usernames;
    private TransportResponse transportResponse;
    private EnergyResponse energyResponse;
    private FoodResponse foodResponse;

    @BeforeEach
    void setUp() {
        // Initialize test data
        usernames = Arrays.asList("user1", "user2", "user3");

        transportResponse = new TransportResponse();
        transportResponse.setUsername("user1");
        transportResponse.setYear(2024);
        transportResponse.setMonth(1);
        transportResponse.setTotalTransportCarbonEmission(50.5);

        energyResponse = new EnergyResponse();
        energyResponse.setUsername("user1");
        energyResponse.setYear(2024);
        energyResponse.setMonth(1);
        energyResponse.setTotalEnergyCarbonEmission(75.25);

        foodResponse = new FoodResponse();
        foodResponse.setUsername("user1");
        foodResponse.setYear(2024);
        foodResponse.setMonth(1);
        foodResponse.setTotalFoodCarbonEmission(30.75);

        // Setup SecurityContextHolder
        SecurityContextHolder.setContext(securityContext);
    }

    // ==================== Tests for getMonthlyTotalEmission ====================

    @Test
    @DisplayName("Should fetch monthly total emission for valid year and month")
    void testGetMonthlyTotalEmission_Success() {
        // Arrange
        int year = 2024;
        int month = 1;
        when(authClient.getAllUsernames()).thenReturn(Arrays.asList("user1", "user2"));
        when(transportClient.getMonthlyTransport("user1", year, month)).thenReturn(transportResponse);
        when(energyClient.getMonthlyEnergy("user1", year, month)).thenReturn(energyResponse);
        when(foodClient.getMonthlyFood("user1", year, month)).thenReturn(foodResponse);

        TransportResponse transportResponse2 = new TransportResponse("user2", year, month, 40.0);
        EnergyResponse energyResponse2 = new EnergyResponse("user2", year, month, 60.0);
        FoodResponse foodResponse2 = new FoodResponse("user2", year, month, 20.0);

        when(transportClient.getMonthlyTransport("user2", year, month)).thenReturn(transportResponse2);
        when(energyClient.getMonthlyEnergy("user2", year, month)).thenReturn(energyResponse2);
        when(foodClient.getMonthlyFood("user2", year, month)).thenReturn(foodResponse2);

        when(securityContext.getAuthentication()).thenReturn(authentication);

        // Act
        ResponseEntity<List<UserMonthlyTotalEmission>> response = analyticsService.getMonthlyTotalEmission(year, month);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(2, response.getBody().size());

        UserMonthlyTotalEmission emission1 = response.getBody().get(0);
        assertEquals("user1", emission1.getUsername());
        assertEquals(year, emission1.getYear());
        assertEquals(month, emission1.getMonth());
        assertEquals(50.5 + 75.25 + 30.75, emission1.getTotalEmission(), 0.01);

        UserMonthlyTotalEmission emission2 = response.getBody().get(1);
        assertEquals("user2", emission2.getUsername());
        assertEquals(40.0 + 60.0 + 20.0, emission2.getTotalEmission(), 0.01);

        verify(authClient, times(1)).getAllUsernames();
        verify(transportClient, times(2)).getMonthlyTransport(anyString(), eq(year), eq(month));
        verify(energyClient, times(2)).getMonthlyEnergy(anyString(), eq(year), eq(month));
        verify(foodClient, times(2)).getMonthlyFood(anyString(), eq(year), eq(month));
    }

    @Test
    @DisplayName("Should handle null responses from transport service")
    void testGetMonthlyTotalEmission_NullTransportResponse() {
        // Arrange
        int year = 2024;
        int month = 1;
        when(authClient.getAllUsernames()).thenReturn(Arrays.asList("user1"));
        when(transportClient.getMonthlyTransport("user1", year, month)).thenReturn(null);
        when(energyClient.getMonthlyEnergy("user1", year, month)).thenReturn(energyResponse);
        when(foodClient.getMonthlyFood("user1", year, month)).thenReturn(foodResponse);
        when(securityContext.getAuthentication()).thenReturn(authentication);

        // Act
        ResponseEntity<List<UserMonthlyTotalEmission>> response = analyticsService.getMonthlyTotalEmission(year, month);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
        assertEquals(75.25 + 30.75, response.getBody().get(0).getTotalEmission(), 0.01);
    }

    @Test
    @DisplayName("Should handle null responses from energy service")
    void testGetMonthlyTotalEmission_NullEnergyResponse() {
        // Arrange
        int year = 2024;
        int month = 1;
        when(authClient.getAllUsernames()).thenReturn(Arrays.asList("user1"));
        when(transportClient.getMonthlyTransport("user1", year, month)).thenReturn(transportResponse);
        when(energyClient.getMonthlyEnergy("user1", year, month)).thenReturn(null);
        when(foodClient.getMonthlyFood("user1", year, month)).thenReturn(foodResponse);
        when(securityContext.getAuthentication()).thenReturn(authentication);

        // Act
        ResponseEntity<List<UserMonthlyTotalEmission>> response = analyticsService.getMonthlyTotalEmission(year, month);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(50.5 + 30.75, response.getBody().get(0).getTotalEmission(), 0.01);
    }

    @Test
    @DisplayName("Should handle null responses from food service")
    void testGetMonthlyTotalEmission_NullFoodResponse() {
        // Arrange
        int year = 2024;
        int month = 1;
        when(authClient.getAllUsernames()).thenReturn(Arrays.asList("user1"));
        when(transportClient.getMonthlyTransport("user1", year, month)).thenReturn(transportResponse);
        when(energyClient.getMonthlyEnergy("user1", year, month)).thenReturn(energyResponse);
        when(foodClient.getMonthlyFood("user1", year, month)).thenReturn(null);
        when(securityContext.getAuthentication()).thenReturn(authentication);

        // Act
        ResponseEntity<List<UserMonthlyTotalEmission>> response = analyticsService.getMonthlyTotalEmission(year, month);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(50.5 + 75.25, response.getBody().get(0).getTotalEmission(), 0.01);
    }

    @Test
    @DisplayName("Should handle all null responses")
    void testGetMonthlyTotalEmission_AllNullResponses() {
        // Arrange
        int year = 2024;
        int month = 1;
        when(authClient.getAllUsernames()).thenReturn(Arrays.asList("user1"));
        when(transportClient.getMonthlyTransport("user1", year, month)).thenReturn(null);
        when(energyClient.getMonthlyEnergy("user1", year, month)).thenReturn(null);
        when(foodClient.getMonthlyFood("user1", year, month)).thenReturn(null);
        when(securityContext.getAuthentication()).thenReturn(authentication);

        // Act
        ResponseEntity<List<UserMonthlyTotalEmission>> response = analyticsService.getMonthlyTotalEmission(year, month);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(0.0, response.getBody().get(0).getTotalEmission(), 0.01);
    }

    // ==================== Tests for getMonthlyTransportEmissions ====================

    @Test
    @DisplayName("Should fetch monthly transport emissions successfully")
    void testGetMonthlyTransportEmissions_Success() {
        // Arrange
        int year = 2024;
        int month = 1;
        when(authClient.getAllUsernames()).thenReturn(Arrays.asList("user1", "user2"));
        when(transportClient.getMonthlyTransport("user1", year, month)).thenReturn(transportResponse);
        when(transportClient.getMonthlyTransport("user2", year, month))
                .thenReturn(new TransportResponse("user2", year, month, 45.5));
        when(userMonthlyTransportEmissionRepository.saveAll(anyList()))
                .thenReturn(Arrays.asList(
                        new UserMonthlyTransportEmission("user1", year, month, 50.5),
                        new UserMonthlyTransportEmission("user2", year, month, 45.5)
                ));

        // Act
        ResponseEntity<List<UserMonthlyTransportEmission>> response = analyticsService.getMonthlyTransportEmissions(year, month);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(2, response.getBody().size());
        assertEquals(50.5, response.getBody().get(0).getTotalTransportEmission(), 0.01);
        assertEquals(45.5, response.getBody().get(1).getTotalTransportEmission(), 0.01);

        verify(userMonthlyTransportEmissionRepository, times(1)).saveAll(anyList());
    }

    @Test
    @DisplayName("Should handle null transport response")
    void testGetMonthlyTransportEmissions_NullResponse() {
        // Arrange
        int year = 2024;
        int month = 1;
        when(authClient.getAllUsernames()).thenReturn(Arrays.asList("user1"));
        when(transportClient.getMonthlyTransport("user1", year, month)).thenReturn(null);
        when(userMonthlyTransportEmissionRepository.saveAll(anyList()))
                .thenReturn(Arrays.asList(new UserMonthlyTransportEmission("user1", year, month, 0.0)));

        // Act
        ResponseEntity<List<UserMonthlyTransportEmission>> response = analyticsService.getMonthlyTransportEmissions(year, month);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(0.0, response.getBody().get(0).getTotalTransportEmission(), 0.01);
    }

    @Test
    @DisplayName("Should return empty list when no usernames available")
    void testGetMonthlyTransportEmissions_NoUsers() {
        // Arrange
        int year = 2024;
        int month = 1;
        when(authClient.getAllUsernames()).thenReturn(Arrays.asList());
        when(userMonthlyTransportEmissionRepository.saveAll(anyList())).thenReturn(Arrays.asList());

        // Act
        ResponseEntity<List<UserMonthlyTransportEmission>> response = analyticsService.getMonthlyTransportEmissions(year, month);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(0, response.getBody().size());
    }

    // ==================== Tests for getMonthlyFoodEmissions ====================

    @Test
    @DisplayName("Should fetch monthly food emissions successfully")
    void testGetMonthlyFoodEmissions_Success() {
        // Arrange
        int year = 2024;
        int month = 1;
        when(authClient.getAllUsernames()).thenReturn(Arrays.asList("user1", "user2"));
        when(foodClient.getMonthlyFood("user1", year, month)).thenReturn(foodResponse);
        when(foodClient.getMonthlyFood("user2", year, month))
                .thenReturn(new FoodResponse("user2", year, month, 35.0));
        when(userMonthlyFoodEmissionRepository.saveAll(anyList()))
                .thenReturn(Arrays.asList(
                        new UserMonthlyFoodEmission("user1", year, month, 30.75),
                        new UserMonthlyFoodEmission("user2", year, month, 35.0)
                ));

        // Act
        ResponseEntity<List<UserMonthlyFoodEmission>> response = analyticsService.getMonthlyFoodEmissions(year, month);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(2, response.getBody().size());
        assertEquals(30.75, response.getBody().get(0).getTotalFoodEmission(), 0.01);
        assertEquals(35.0, response.getBody().get(1).getTotalFoodEmission(), 0.01);

        verify(userMonthlyFoodEmissionRepository, times(1)).saveAll(anyList());
    }

    @Test
    @DisplayName("Should handle null food response")
    void testGetMonthlyFoodEmissions_NullResponse() {
        // Arrange
        int year = 2024;
        int month = 1;
        when(authClient.getAllUsernames()).thenReturn(Arrays.asList("user1"));
        when(foodClient.getMonthlyFood("user1", year, month)).thenReturn(null);
        when(userMonthlyFoodEmissionRepository.saveAll(anyList()))
                .thenReturn(Arrays.asList(new UserMonthlyFoodEmission("user1", year, month, 0.0)));

        // Act
        ResponseEntity<List<UserMonthlyFoodEmission>> response = analyticsService.getMonthlyFoodEmissions(year, month);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(0.0, response.getBody().get(0).getTotalFoodEmission(), 0.01);
    }

    // ==================== Tests for getMonthlyEnergyEmissions ====================

    @Test
    @DisplayName("Should fetch monthly energy emissions successfully")
    void testGetMonthlyEnergyEmissions_Success() {
        // Arrange
        int year = 2024;
        int month = 1;
        when(authClient.getAllUsernames()).thenReturn(Arrays.asList("user1", "user2"));
        when(energyClient.getMonthlyEnergy("user1", year, month)).thenReturn(energyResponse);
        when(energyClient.getMonthlyEnergy("user2", year, month))
                .thenReturn(new EnergyResponse("user2", year, month, 85.0));
        when(userMonthlyEnergyEmissionRepository.saveAll(anyList()))
                .thenReturn(Arrays.asList(
                        new UserMonthlyEnergyEmission("user1", year, month, 75.25),
                        new UserMonthlyEnergyEmission("user2", year, month, 85.0)
                ));

        // Act
        ResponseEntity<List<UserMonthlyEnergyEmission>> response = analyticsService.getMonthlyEnergyEmissions(year, month);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(2, response.getBody().size());
        assertEquals(75.25, response.getBody().get(0).getTotalEnergyEmission(), 0.01);
        assertEquals(85.0, response.getBody().get(1).getTotalEnergyEmission(), 0.01);

        verify(userMonthlyEnergyEmissionRepository, times(1)).saveAll(anyList());
    }

    @Test
    @DisplayName("Should handle null energy response")
    void testGetMonthlyEnergyEmissions_NullResponse() {
        // Arrange
        int year = 2024;
        int month = 1;
        when(authClient.getAllUsernames()).thenReturn(Arrays.asList("user1"));
        when(energyClient.getMonthlyEnergy("user1", year, month)).thenReturn(null);
        when(userMonthlyEnergyEmissionRepository.saveAll(anyList()))
                .thenReturn(Arrays.asList(new UserMonthlyEnergyEmission("user1", year, month, 0.0)));

        // Act
        ResponseEntity<List<UserMonthlyEnergyEmission>> response = analyticsService.getMonthlyEnergyEmissions(year, month);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(0.0, response.getBody().get(0).getTotalEnergyEmission(), 0.01);
    }

    // ==================== Tests for getAnalyticsMonthly ====================

    @Test
    @DisplayName("Should fetch current month analytics successfully")
    void testGetAnalyticsMonthly_Success() {
        // Arrange
        int currentYear = LocalDate.now().getYear();
        int currentMonth = LocalDate.now().getMonthValue();

        when(authClient.getAllUsernames()).thenReturn(Arrays.asList("user1", "user2"));
        when(transportClient.getMonthlyTransport("user1", currentYear, currentMonth)).thenReturn(transportResponse);
        when(energyClient.getMonthlyEnergy("user1", currentYear, currentMonth)).thenReturn(energyResponse);
        when(foodClient.getMonthlyFood("user1", currentYear, currentMonth)).thenReturn(foodResponse);

        TransportResponse transportResponse2 = new TransportResponse("user2", currentYear, currentMonth, 40.0);
        EnergyResponse energyResponse2 = new EnergyResponse("user2", currentYear, currentMonth, 60.0);
        FoodResponse foodResponse2 = new FoodResponse("user2", currentYear, currentMonth, 20.0);

        when(transportClient.getMonthlyTransport("user2", currentYear, currentMonth)).thenReturn(transportResponse2);
        when(energyClient.getMonthlyEnergy("user2", currentYear, currentMonth)).thenReturn(energyResponse2);
        when(foodClient.getMonthlyFood("user2", currentYear, currentMonth)).thenReturn(foodResponse2);
        when(securityContext.getAuthentication()).thenReturn(authentication);

        // Act
        ResponseEntity<?> response = analyticsService.getAnalyticsMonthly();

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());

        List<AnalyticsResponse> body = (List<AnalyticsResponse>) response.getBody();
        assertEquals(2, body.size());
        assertEquals("user1", body.get(0).getUsername());
        assertEquals(50.5 + 75.25 + 30.75, body.get(0).getTotalEmission(), 0.01);
        assertEquals("user2", body.get(1).getUsername());
        assertEquals(40.0 + 60.0 + 20.0, body.get(1).getTotalEmission(), 0.01);
    }

    @Test
    @DisplayName("Should return empty list when no users for current month")
    void testGetAnalyticsMonthly_NoUsers() {
        // Arrange
        int currentYear = LocalDate.now().getYear();
        int currentMonth = LocalDate.now().getMonthValue();

        when(authClient.getAllUsernames()).thenReturn(Arrays.asList());
        when(securityContext.getAuthentication()).thenReturn(authentication);

        // Act
        ResponseEntity<?> response = analyticsService.getAnalyticsMonthly();

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());

        List<AnalyticsResponse> body = (List<AnalyticsResponse>) response.getBody();
        assertEquals(0, body.size());
    }

    @Test
    @DisplayName("Should handle single user for analytics monthly")
    void testGetAnalyticsMonthly_SingleUser() {
        // Arrange
        int currentYear = LocalDate.now().getYear();
        int currentMonth = LocalDate.now().getMonthValue();

        when(authClient.getAllUsernames()).thenReturn(Arrays.asList("user1"));
        when(transportClient.getMonthlyTransport("user1", currentYear, currentMonth)).thenReturn(transportResponse);
        when(energyClient.getMonthlyEnergy("user1", currentYear, currentMonth)).thenReturn(energyResponse);
        when(foodClient.getMonthlyFood("user1", currentYear, currentMonth)).thenReturn(foodResponse);
        when(securityContext.getAuthentication()).thenReturn(authentication);

        // Act
        ResponseEntity<?> response = analyticsService.getAnalyticsMonthly();

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());

        List<AnalyticsResponse> body = (List<AnalyticsResponse>) response.getBody();
        assertEquals(1, body.size());
        assertEquals("user1", body.get(0).getUsername());
        assertEquals(156.5, body.get(0).getTotalEmission(), 0.01);
    }
}
