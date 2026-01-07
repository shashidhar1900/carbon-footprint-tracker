package com.shashi.transportservice.service;

import com.shashi.transportservice.constants.TransportConstants;
import com.shashi.transportservice.dto.TransportRequest;
import com.shashi.transportservice.dto.TransportResponse;
import com.shashi.transportservice.model.TransportUsage;
import com.shashi.transportservice.repositotry.TransportRepository;
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
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
public class TransportServiceTest {

    @Mock
    TransportRepository transportRepository;
    @Mock
    Authentication authentication;
    @Mock
    SecurityContext securityContext;

    @InjectMocks
    TransportService transportService;

    private final String MOCK_USER = "shashi_user";
    private final String TODAY = LocalDate.now().toString();

    @BeforeEach
    void setUp() {
        // Setup SecurityContext to return our mock username
        SecurityContextHolder.setContext(securityContext);
        lenient().when(securityContext.getAuthentication()).thenReturn(authentication);
        lenient().when(authentication.getName()).thenReturn(MOCK_USER);
    }



    @Test
    void addTransport_Success() {
        TransportRequest transportRequest = new TransportRequest();
        transportRequest.setMode("car");
        transportRequest.setDistance(19.5);
        when(transportRepository.findByUsernameAndDateAndMode(MOCK_USER, TODAY, "car"))
                .thenReturn(null);

        // Act
        ResponseEntity<?> response = transportService.addTransport(transportRequest);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Transport record added!", response.getBody());
        verify(transportRepository, times(1)).save(any(TransportUsage.class));
    }

    @Test
    void addTransport_AlreadyExists_ReturnsBadRequest() {
        // Arrange
        TransportRequest request = new TransportRequest();
        request.setMode("Bus");

        TransportUsage existingRecord = new TransportUsage();
        when(transportRepository.findByUsernameAndDateAndMode(MOCK_USER, TODAY, "Bus"))
                .thenReturn(existingRecord);

        // Act
        ResponseEntity<?> response = transportService.addTransport(request);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Transport already added for today with that mode. You can update or edit if you want.", response.getBody());
        verify(transportRepository, never()).save(any());
    }

    @Test
    void updateTransport_Success() {
        TransportRequest transportRequest = new TransportRequest();
        transportRequest.setMode("Car");
        transportRequest.setDistance(10.3);
        TransportUsage existing = new TransportUsage();
        existing.setId(1);
        existing.setMode("Car");
        existing.setDate(TODAY);
        existing.setUsername(MOCK_USER);
        when(transportRepository.findByUsernameAndDateAndMode(MOCK_USER,TODAY,"Car")).thenReturn(existing);
        ResponseEntity<?> response = transportService.updateTransport(transportRequest);

        assertEquals(HttpStatus.OK,response.getStatusCode());
        assertEquals("Transport record updated!", response.getBody());
        verify(transportRepository,times(1)).delete(existing);
        verify(transportRepository,times(1)).save(any(TransportUsage.class));
    }

    @Test
    void updateTransport_NotFound_ReturnsBadRequest() {
        TransportRequest req = new TransportRequest();
        req.setMode("car");
        when(transportRepository.findByUsernameAndDateAndMode(MOCK_USER, TODAY, "car"))
                .thenReturn(null);

        ResponseEntity<?> resp = transportService.updateTransport(req);

        assertEquals(HttpStatus.BAD_REQUEST, resp.getStatusCode());
        assertEquals("No transport record found for today to update. Please add one first.", resp.getBody());
        verify(transportRepository, never()).delete(any());
        verify(transportRepository, never()).save(any());
    }

    @Test
    void getHistory_Returns_ListOfTransportUsage() {
        TransportUsage u1 = new TransportUsage();
        u1.setUsername(MOCK_USER);
        u1.setMode("Bike");
        u1.setDate(TODAY);
        u1.setDistance(122.34);
        List<TransportUsage> usages = List.of(u1);
        when(transportRepository.findByUsername(MOCK_USER)).thenReturn(usages);
        ResponseEntity<List<TransportUsage>> response = transportService.getHistory();

        assertEquals(HttpStatus.OK,response.getStatusCode());
        verify(transportRepository,times(1)).findByUsername(MOCK_USER);
        assertEquals(usages,response.getBody());
    }

    @Test
    void getMonthlyTransport_ComputesTotalEmission() {
        int year = 2025;
        int month = 6;
        LocalDate d1 = LocalDate.of(year, month, 5);
        LocalDate d2 = LocalDate.of(year, month, 10);
        TransportUsage car = new TransportUsage();
        car.setUsername(MOCK_USER);
        car.setMode("car");
        car.setDistance(10.0);
        car.setDate(d1.toString());

        TransportUsage bus = new TransportUsage();
        bus.setUsername(MOCK_USER);
        bus.setMode("bus");
        bus.setDistance(20.0);
        bus.setDate(d2.toString());

        when(transportRepository.findByUsername(MOCK_USER)).thenReturn(List.of(car, bus));

        ResponseEntity<TransportResponse> resp = transportService.getMonthlyTransport(MOCK_USER, year, month);
        assertEquals(HttpStatus.OK, resp.getStatusCode());
        TransportResponse body = resp.getBody();
        assertNotNull(body);

        double expected = 10.0 * TransportConstants.CAR_EMISSION_FACTOR
                + 20.0 * TransportConstants.BUS_EMISSION_FACTOR;
        assertEquals(expected, body.getTotalTransportCarbonEmission(), 1e-6);
        assertEquals(MOCK_USER, body.getUsername());
        assertEquals(year, body.getYear());
        assertEquals(month, body.getMonth());
    }

    @Test
    void getAllUsersMonthlyTransport_ReturnsResponsesPerUser() {
        int year = 2025;
        int month = 7;

        when(transportRepository.findDistinctUsernames()).thenReturn(List.of(MOCK_USER+"u1",MOCK_USER+"u2"));

        TransportUsage u1rec = new TransportUsage();
        u1rec.setUsername(MOCK_USER+"u1");
        u1rec.setMode("car");
        u1rec.setDistance(5.0);
        u1rec.setDate(LocalDate.of(year, month, 1).toString());

        TransportUsage u2rec = new TransportUsage();
        u2rec.setUsername(MOCK_USER+"u2");
        u2rec.setMode("bus");
        u2rec.setDistance(8.0);
        u2rec.setDate(LocalDate.of(year, month, 2).toString());

        when(transportRepository.findByUsername(MOCK_USER+"u1")).thenReturn(List.of(u1rec));
        when(transportRepository.findByUsername(MOCK_USER+"u2")).thenReturn(List.of(u2rec));

        ResponseEntity<List<TransportResponse>> resp = transportService.getAllUsersMonthlyTransport(year, month);
        assertEquals(HttpStatus.OK, resp.getStatusCode());
        List<TransportResponse> bodies = resp.getBody();
        assertNotNull(bodies);
        assertEquals(2, bodies.size());

        double expected1 = 5.0 * TransportConstants.CAR_EMISSION_FACTOR;
        double expected2 = 8.0 * TransportConstants.BUS_EMISSION_FACTOR;

        // find responses by username
        TransportResponse r1 = bodies.stream().filter(r -> (MOCK_USER+"u1").equals(r.getUsername())).findFirst().orElse(null);
        TransportResponse r2 = bodies.stream().filter(r -> (MOCK_USER+"u2").equals(r.getUsername())).findFirst().orElse(null);
        assertNotNull(r1);
        assertNotNull(r2);
        assertEquals(expected1, r1.getTotalTransportCarbonEmission(), 1e-6);
        assertEquals(expected2, r2.getTotalTransportCarbonEmission(), 1e-6);
    }

    @Test
    void deleteTransport_Success() {
        String mode = "car";
        TransportUsage existing = new TransportUsage();
        existing.setUsername(MOCK_USER);
        existing.setMode(mode);
        existing.setDate(TODAY);

        when(transportRepository.findByUsernameAndDateAndMode(MOCK_USER, TODAY, mode)).thenReturn(existing);

        ResponseEntity<?> resp = transportService.deleteTransport(mode);

        assertEquals(HttpStatus.OK, resp.getStatusCode());
        assertEquals("Today's transport record deleted successfully.", resp.getBody());
        verify(transportRepository, times(1)).delete(existing);
    }

    @Test
    void deleteTransport_NotFound_ReturnsBadRequest() {
        String mode = "bike";
        when(transportRepository.findByUsernameAndDateAndMode(MOCK_USER, TODAY, mode)).thenReturn(null);

        ResponseEntity<?> resp = transportService.deleteTransport(mode);

        assertEquals(HttpStatus.BAD_REQUEST, resp.getStatusCode());
        assertEquals("No transport record found for today to delete.", resp.getBody());
        verify(transportRepository, never()).delete(any());
    }


}
