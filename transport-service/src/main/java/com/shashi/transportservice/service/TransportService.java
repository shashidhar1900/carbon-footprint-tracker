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
        String today = LocalDate.now().toString();
        String mode = transport.getMode();

        // Check if a record already exists for today with that mode
        TransportUsage existing = transportRepository.findByUsernameAndDateAndMode(username, today, mode);
        if (existing != null) {
            return ResponseEntity.badRequest().body("Transport already added for today with that mode. You can update or edit if you want.");
        }

        TransportUsage usage = TransportUsage.builder()
                .username(username)
                .mode(transport.getMode())
                .distance(transport.getDistance())
                .date(today)
                .build();

        transportRepository.save(usage);
        return ResponseEntity.ok("Transport record added!");
    }

    public ResponseEntity<?> updateTransport(TransportRequest transport) {
        String username = getCurrentUsername();
        String today = LocalDate.now().toString();
        String mode = transport.getMode();
        TransportUsage existing = transportRepository.findByUsernameAndDateAndMode(username, today, mode);
        if (existing == null) {
            return ResponseEntity.badRequest().body("No transport record found for today to update. Please add one first.");
        }
        // Delete the old record
        transportRepository.delete(existing);
        // Add the new record
        TransportUsage usage = TransportUsage.builder()
                .username(username)
                .mode(transport.getMode())
                .distance(transport.getDistance())
                .date(LocalDate.now().toString())
                .build();

        transportRepository.save(usage);
        return ResponseEntity.ok("Transport record updated!");
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


    public ResponseEntity<?> deleteTransport(String mode) {
        String username = getCurrentUsername();
        String today = LocalDate.now().toString();
        TransportUsage existing = transportRepository.findByUsernameAndDateAndMode(username, today, mode);
        if (existing == null) {
            return ResponseEntity.badRequest().body("No transport record found for today to delete.");
        }
        transportRepository.delete(existing);
        return ResponseEntity.ok("Today's transport record deleted successfully.");
    }
}
