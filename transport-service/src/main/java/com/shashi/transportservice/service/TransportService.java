package com.shashi.transportservice.service;

import com.shashi.transportservice.constants.TransportConstants;
import com.shashi.transportservice.dto.TransportRequest;
import com.shashi.transportservice.dto.TransportResponse;
import com.shashi.transportservice.model.TransportUsage;
import com.shashi.transportservice.repositotry.TransportRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class TransportService {

    @Autowired
    private TransportRepository transportRepository;

    private String getCurrentUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return (authentication != null) ? authentication.getName() : null;
    }

    public ResponseEntity<?> addTransport(TransportRequest transport) {
        String username = getCurrentUsername();

        TransportUsage usage = TransportUsage.builder()
                .username(username)
                .mode(transport.getMode())
                .distance(transport.getDistance())
                .date(LocalDate.now().toString())
                .build();

        transportRepository.save(usage);
        return ResponseEntity.ok("Transport record added!");
    }

    public ResponseEntity<List<TransportUsage>> getHistory() {
        String username = getCurrentUsername();
        return ResponseEntity.ok(transportRepository.findByUsername(username));
    }

    public ResponseEntity<TransportResponse> getMonthlyTransport(String username, int year, int month) {

        List<TransportUsage> usages = transportRepository.findByUsername(username);
        double totalEmission = usages.stream()
                .filter(u -> {
                    LocalDate date = LocalDate.parse(u.getDate());
                    return date.getYear() == year && date.getMonthValue() == month;
                })
                .mapToDouble(u -> {
                    double emissionFactor = 0.0;
                    switch (u.getMode().toLowerCase()) {
                        case "car":
                            emissionFactor = TransportConstants.CAR_EMISSION_FACTOR;
                            break;
                        case "bus":
                            emissionFactor = TransportConstants.BUS_EMISSION_FACTOR;
                            break;
                        case "bike":
                            emissionFactor = TransportConstants.BIKE_EMISSION_FACTOR;
                            break;
                        // Add more modes as needed
                        default:
                            emissionFactor = 0.5; // Default factor
                    }
                    return u.getDistance() * emissionFactor;
                })
                .sum();

        return ResponseEntity.ok(new TransportResponse(username, year, month, totalEmission));


    }

    public ResponseEntity<List<TransportResponse>> getAllUsersMonthlyTransport(int year, int month) {
        List<String> usernames = transportRepository.findDistinctUsernames();
        List<TransportResponse> responses = usernames.stream()
                .map(username -> getMonthlyTransport(username, year, month).getBody())
                .toList();
        return ResponseEntity.ok(responses);
    }
}
