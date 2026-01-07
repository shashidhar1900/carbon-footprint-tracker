package com.shashi.transportservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.shashi.transportservice.dto.TransportRequest;
import com.shashi.transportservice.dto.TransportResponse;
import com.shashi.transportservice.model.TransportUsage;
import com.shashi.transportservice.service.TransportService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
public class TransportControllerTest {

    @Mock
    private TransportService transportService;

    @InjectMocks
    private TransportController transportController;

    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(transportController).build();
    }

    @Test
    void addTransport_delegatesToService_andReturnsResponse() throws Exception {
        TransportRequest req = new TransportRequest();
        req.setMode("car");
        req.setDistance(12.5);

        doReturn(ResponseEntity.ok("Transport record added!"))
                .when(transportService)
                .addTransport(any(TransportRequest.class));


        mockMvc.perform(post("/api/transport/add")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(content().string("Transport record added!"));

        verify(transportService, times(1)).addTransport(any(TransportRequest.class));
    }

    @Test
    void updateTransport_delegatesToService_andReturnsResponse() throws Exception {
        TransportRequest req = new TransportRequest();
        req.setMode("bus");
        req.setDistance(5.0);

        doReturn(ResponseEntity.ok("Updated"))
                .when(transportService)
                .updateTransport(any(TransportRequest.class));


        mockMvc.perform(put("/api/transport/update")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(content().string("Updated"));

        verify(transportService, times(1)).updateTransport(any(TransportRequest.class));
    }

    @Test
    void deleteTransport_delegatesToService_andReturnsResponse() throws Exception {
        String mode = "car";

        doReturn(ResponseEntity.ok("Deleted")).when(transportService).deleteTransport(mode);

        mockMvc.perform(delete("/api/transport/delete/{mode}", mode))
                .andExpect(status().isOk())
                .andExpect(content().string("Deleted"));

        verify(transportService, times(1)).deleteTransport(mode);
    }

    @Test
    void getHistory_delegatesToService_andReturnsList() throws Exception {
        TransportUsage u = new TransportUsage();
        u.setUsername("user1");
        u.setMode("bike");
        u.setDistance(3.2);
        u.setDate(LocalDate.now().toString());

        List<TransportUsage> list = List.of(u);
        when(transportService.getHistory()).thenReturn(ResponseEntity.ok(list));

        mockMvc.perform(get("/api/transport/history"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(list)));

        verify(transportService, times(1)).getHistory();
    }

    @Test
    void getMonthlyTransport_requiresAuthHeader_delegatesAndReturnsResponse() throws Exception {
        String username = "user1";
        int year = 2025;
        int month = 6;

        TransportResponse resp = new TransportResponse();
        resp.setUsername(username);
        resp.setYear(year);
        resp.setMonth(month);
        resp.setTotalTransportCarbonEmission(42.0);

        when(transportService.getMonthlyTransport(username, year, month))
                .thenReturn(ResponseEntity.ok(resp));

        mockMvc.perform(get("/api/transport/history/{username}/{year}/{month}", username, year, month)
                        .header("Authorization", "Bearer dummy"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(resp)));

        verify(transportService, times(1)).getMonthlyTransport(username, year, month);
    }

    @Test
    void getAllUsersMonthlyTransport_delegatesAndReturnsResponses() throws Exception {
        int year = 2025;
        int month = 7;

        TransportResponse r1 = new TransportResponse();
        r1.setUsername("u1");
        r1.setYear(year);
        r1.setMonth(month);
        r1.setTotalTransportCarbonEmission(10.0);

        TransportResponse r2 = new TransportResponse();
        r2.setUsername("u2");
        r2.setYear(year);
        r2.setMonth(month);
        r2.setTotalTransportCarbonEmission(20.0);

        List<TransportResponse> responses = List.of(r1, r2);
        when(transportService.getAllUsersMonthlyTransport(year, month))
                .thenReturn(ResponseEntity.ok(responses));

        mockMvc.perform(get("/api/transport/history/allUsers/{year}/{month}", year, month))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(responses)));

        verify(transportService, times(1)).getAllUsersMonthlyTransport(year, month);
    }
}
