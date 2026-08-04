package com.shashi.foodservice.service;

import com.shashi.foodservice.constants.FoodConstants;
import com.shashi.foodservice.dto.FoodRequest;
import com.shashi.foodservice.dto.FoodResponse;
import com.shashi.foodservice.model.FoodUsage;
import com.shashi.foodservice.repository.FoodRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class FoodServiceTest {

    @Mock
    private FoodRepository foodRepository;

    @InjectMocks
    private FoodService foodService;

    private String testUsername;
    private String testDate;

    @BeforeEach
    void setUp() {
        testUsername = "testuser";
        testDate = LocalDate.now().toString();
    }

    // ==================== addFood Tests ====================

    @Test
    void testAddFood_Success() {
        FoodRequest request = new FoodRequest();
        request.setType("VEG");
        request.setQuantity(100.0);

        try (MockedStatic<SecurityContextHolder> securityContextHolderMock = mockStatic(SecurityContextHolder.class)) {
            SecurityContext securityContext = mock(SecurityContext.class);
            Authentication authentication = mock(Authentication.class);
            securityContextHolderMock.when(SecurityContextHolder::getContext).thenReturn(securityContext);
            when(securityContext.getAuthentication()).thenReturn(authentication);
            when(authentication.getName()).thenReturn(testUsername);
            when(foodRepository.findByUsernameAndDateAndType(testUsername, testDate, "VEG")).thenReturn(null);

            ResponseEntity<?> response = foodService.addFood(request);

            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertEquals("Food record added!", response.getBody());
            verify(foodRepository, times(1)).save(any(FoodUsage.class));
        }
    }

    @Test
    void testAddFood_RecordAlreadyExists() {
        FoodRequest request = new FoodRequest();
        request.setType("VEG");
        request.setQuantity(100.0);

        FoodUsage existingUsage = FoodUsage.builder()
                .id(1)
                .username(testUsername)
                .type("VEG")
                .quantity(50.0)
                .date(testDate)
                .build();

        try (MockedStatic<SecurityContextHolder> securityContextHolderMock = mockStatic(SecurityContextHolder.class)) {
            SecurityContext securityContext = mock(SecurityContext.class);
            Authentication authentication = mock(Authentication.class);
            securityContextHolderMock.when(SecurityContextHolder::getContext).thenReturn(securityContext);
            when(securityContext.getAuthentication()).thenReturn(authentication);
            when(authentication.getName()).thenReturn(testUsername);
            when(foodRepository.findByUsernameAndDateAndType(testUsername, testDate, "VEG")).thenReturn(existingUsage);

            ResponseEntity<?> response = foodService.addFood(request);

            assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
            assertTrue(response.getBody().toString().contains("Food record for today already exists"));
            verify(foodRepository, never()).save(any(FoodUsage.class));
        }
    }

    @Test
    void testAddFood_NullAuthentication() {
        FoodRequest request = new FoodRequest();
        request.setType("NON_VEG");
        request.setQuantity(150.0);

        try (MockedStatic<SecurityContextHolder> securityContextHolderMock = mockStatic(SecurityContextHolder.class)) {
            SecurityContext securityContext = mock(SecurityContext.class);
            securityContextHolderMock.when(SecurityContextHolder::getContext).thenReturn(securityContext);
            when(securityContext.getAuthentication()).thenReturn(null);
            when(foodRepository.findByUsernameAndDateAndType(null, testDate, "NON_VEG")).thenReturn(null);

            ResponseEntity<?> response = foodService.addFood(request);

            assertEquals(HttpStatus.OK, response.getStatusCode());
            verify(foodRepository, times(1)).save(any(FoodUsage.class));
        }
    }

    // ==================== updateFood Tests ====================

    @Test
    void testUpdateFood_Success() {
        FoodRequest request = new FoodRequest();
        request.setType("JUNK");
        request.setQuantity(200.0);

        FoodUsage existingUsage = FoodUsage.builder()
                .id(1)
                .username(testUsername)
                .type("JUNK")
                .quantity(100.0)
                .date(testDate)
                .build();

        try (MockedStatic<SecurityContextHolder> securityContextHolderMock = mockStatic(SecurityContextHolder.class)) {
            SecurityContext securityContext = mock(SecurityContext.class);
            Authentication authentication = mock(Authentication.class);
            securityContextHolderMock.when(SecurityContextHolder::getContext).thenReturn(securityContext);
            when(securityContext.getAuthentication()).thenReturn(authentication);
            when(authentication.getName()).thenReturn(testUsername);
            when(foodRepository.findByUsernameAndDateAndType(testUsername, testDate, "JUNK")).thenReturn(existingUsage);

            ResponseEntity<?> response = foodService.updateFood(request);

            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertEquals("Food record updated!", response.getBody());
            verify(foodRepository, times(1)).delete(existingUsage);
            verify(foodRepository, times(1)).save(any(FoodUsage.class));
        }
    }

    @Test
    void testUpdateFood_RecordNotFound() {
        FoodRequest request = new FoodRequest();
        request.setType("VEG");
        request.setQuantity(100.0);

        try (MockedStatic<SecurityContextHolder> securityContextHolderMock = mockStatic(SecurityContextHolder.class)) {
            SecurityContext securityContext = mock(SecurityContext.class);
            Authentication authentication = mock(Authentication.class);
            securityContextHolderMock.when(SecurityContextHolder::getContext).thenReturn(securityContext);
            when(securityContext.getAuthentication()).thenReturn(authentication);
            when(authentication.getName()).thenReturn(testUsername);
            when(foodRepository.findByUsernameAndDateAndType(testUsername, testDate, "VEG")).thenReturn(null);

            ResponseEntity<?> response = foodService.updateFood(request);

            assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
            assertTrue(response.getBody().toString().contains("No food record found"));
            verify(foodRepository, never()).delete(any(FoodUsage.class));
            verify(foodRepository, never()).save(any(FoodUsage.class));
        }
    }

    // ==================== deleteFood Tests ====================

    @Test
    void testDeleteFood_Success() {
        FoodUsage existingUsage = FoodUsage.builder()
                .id(1)
                .username(testUsername)
                .type("VEG")
                .quantity(100.0)
                .date(testDate)
                .build();

        try (MockedStatic<SecurityContextHolder> securityContextHolderMock = mockStatic(SecurityContextHolder.class)) {
            SecurityContext securityContext = mock(SecurityContext.class);
            Authentication authentication = mock(Authentication.class);
            securityContextHolderMock.when(SecurityContextHolder::getContext).thenReturn(securityContext);
            when(securityContext.getAuthentication()).thenReturn(authentication);
            when(authentication.getName()).thenReturn(testUsername);
            when(foodRepository.findByUsernameAndDateAndType(testUsername, testDate, "VEG")).thenReturn(existingUsage);

            ResponseEntity<?> response = foodService.deleteFood("VEG");

            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertEquals("Today's Food record deleted!", response.getBody());
            verify(foodRepository, times(1)).delete(existingUsage);
        }
    }

    @Test
    void testDeleteFood_RecordNotFound() {
        try (MockedStatic<SecurityContextHolder> securityContextHolderMock = mockStatic(SecurityContextHolder.class)) {
            SecurityContext securityContext = mock(SecurityContext.class);
            Authentication authentication = mock(Authentication.class);
            securityContextHolderMock.when(SecurityContextHolder::getContext).thenReturn(securityContext);
            when(securityContext.getAuthentication()).thenReturn(authentication);
            when(authentication.getName()).thenReturn(testUsername);
            when(foodRepository.findByUsernameAndDateAndType(testUsername, testDate, "NON_VEG")).thenReturn(null);

            ResponseEntity<?> response = foodService.deleteFood("NON_VEG");

            assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
            assertTrue(response.getBody().toString().contains("No food record found"));
            verify(foodRepository, never()).delete(any(FoodUsage.class));
        }
    }

    // ==================== getHistory Tests ====================

    @Test
    void testGetHistory_Success() {
        List<FoodUsage> foodUsages = new ArrayList<>();
        foodUsages.add(FoodUsage.builder()
                .id(1)
                .username(testUsername)
                .type("VEG")
                .quantity(100.0)
                .date(testDate)
                .build());
        foodUsages.add(FoodUsage.builder()
                .id(2)
                .username(testUsername)
                .type("NON_VEG")
                .quantity(150.0)
                .date(testDate)
                .build());

        try (MockedStatic<SecurityContextHolder> securityContextHolderMock = mockStatic(SecurityContextHolder.class)) {
            SecurityContext securityContext = mock(SecurityContext.class);
            Authentication authentication = mock(Authentication.class);
            securityContextHolderMock.when(SecurityContextHolder::getContext).thenReturn(securityContext);
            when(securityContext.getAuthentication()).thenReturn(authentication);
            when(authentication.getName()).thenReturn(testUsername);
            when(foodRepository.findByUsername(testUsername)).thenReturn(foodUsages);

            ResponseEntity<List<FoodUsage>> response = foodService.getHistory();

            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertEquals(2, response.getBody().size());
            assertEquals("VEG", response.getBody().get(0).getType());
            assertEquals("NON_VEG", response.getBody().get(1).getType());
            verify(foodRepository, times(1)).findByUsername(testUsername);
        }
    }

    @Test
    void testGetHistory_EmptyList() {
        List<FoodUsage> emptyList = new ArrayList<>();

        try (MockedStatic<SecurityContextHolder> securityContextHolderMock = mockStatic(SecurityContextHolder.class)) {
            SecurityContext securityContext = mock(SecurityContext.class);
            Authentication authentication = mock(Authentication.class);
            securityContextHolderMock.when(SecurityContextHolder::getContext).thenReturn(securityContext);
            when(securityContext.getAuthentication()).thenReturn(authentication);
            when(authentication.getName()).thenReturn(testUsername);
            when(foodRepository.findByUsername(testUsername)).thenReturn(emptyList);

            ResponseEntity<List<FoodUsage>> response = foodService.getHistory();

            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertEquals(0, response.getBody().size());
            verify(foodRepository, times(1)).findByUsername(testUsername);
        }
    }

    // ==================== getMonthlyFood Tests ====================

    @Test
    void testGetMonthlyFood_Success() {
        String username = "testuser";
        int year = 2025;
        int month = 1;

        List<FoodUsage> foodUsages = new ArrayList<>();
        foodUsages.add(FoodUsage.builder()
                .id(1)
                .username(username)
                .type("veg")
                .quantity(100.0)
                .date("2025-01-01")
                .build());
        foodUsages.add(FoodUsage.builder()
                .id(2)
                .username(username)
                .type("non_veg")
                .quantity(150.0)
                .date("2025-01-02")
                .build());
        foodUsages.add(FoodUsage.builder()
                .id(3)
                .username(username)
                .type("junk")
                .quantity(50.0)
                .date("2025-01-03")
                .build());

        when(foodRepository.findByUsername(username)).thenReturn(foodUsages);

        ResponseEntity<FoodResponse> response = foodService.getMonthlyFood(username, year, month);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        FoodResponse foodResponse = response.getBody();
        assertNotNull(foodResponse);
        assertEquals(username, foodResponse.getUsername());
        assertEquals(year, foodResponse.getYear());
        assertEquals(month, foodResponse.getMonth());

        // Verify calculation: (100 * 1.5) + (150 * 3.0) + (50 * 2.0) = 150 + 450 + 100 = 700
        double expectedEmission = (100 * FoodConstants.VEG_EMISSION_FACTOR) +
                (150 * FoodConstants.NONVEG_EMISSION_FACTOR) +
                (50 * FoodConstants.JUNK_EMISSION_FACTOR);
        assertEquals(expectedEmission, foodResponse.getTotalFoodCarbonEmission(), 0.01);

        verify(foodRepository, times(1)).findByUsername(username);
    }

    @Test
    void testGetMonthlyFood_NoRecordsForMonth() {
        String username = "testuser";
        int year = 2025;
        int month = 1;

        List<FoodUsage> foodUsages = new ArrayList<>();
        foodUsages.add(FoodUsage.builder()
                .id(1)
                .username(username)
                .type("veg")
                .quantity(100.0)
                .date("2025-02-01")
                .build());

        when(foodRepository.findByUsername(username)).thenReturn(foodUsages);

        ResponseEntity<FoodResponse> response = foodService.getMonthlyFood(username, year, month);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        FoodResponse foodResponse = response.getBody();
        assertNotNull(foodResponse);
        assertEquals(0.0, foodResponse.getTotalFoodCarbonEmission());
    }

    @Test
    void testGetMonthlyFood_MultipleMonths() {
        String username = "testuser";

        List<FoodUsage> foodUsages = new ArrayList<>();
        foodUsages.add(FoodUsage.builder()
                .id(1)
                .username(username)
                .type("veg")
                .quantity(100.0)
                .date("2025-01-01")
                .build());
        foodUsages.add(FoodUsage.builder()
                .id(2)
                .username(username)
                .type("veg")
                .quantity(200.0)
                .date("2025-02-01")
                .build());

        when(foodRepository.findByUsername(username)).thenReturn(foodUsages);

        ResponseEntity<FoodResponse> response1 = foodService.getMonthlyFood(username, 2025, 1);
        ResponseEntity<FoodResponse> response2 = foodService.getMonthlyFood(username, 2025, 2);

        assertEquals(100 * FoodConstants.VEG_EMISSION_FACTOR, response1.getBody().getTotalFoodCarbonEmission(), 0.01);
        assertEquals(200 * FoodConstants.VEG_EMISSION_FACTOR, response2.getBody().getTotalFoodCarbonEmission(), 0.01);
    }

    @Test
    void testGetMonthlyFood_DefaultEmissionFactor() {
        String username = "testuser";
        int year = 2025;
        int month = 1;

        List<FoodUsage> foodUsages = new ArrayList<>();
        foodUsages.add(FoodUsage.builder()
                .id(1)
                .username(username)
                .type("unknown")
                .quantity(100.0)
                .date("2025-01-01")
                .build());

        when(foodRepository.findByUsername(username)).thenReturn(foodUsages);

        ResponseEntity<FoodResponse> response = foodService.getMonthlyFood(username, year, month);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        FoodResponse foodResponse = response.getBody();
        // Default factor is 0.5
        assertEquals(100 * 0.5, foodResponse.getTotalFoodCarbonEmission(), 0.01);
    }

}
