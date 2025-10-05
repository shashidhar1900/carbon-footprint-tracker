package com.shashi.energyservice.service;


import com.shashi.energyservice.constants.EnergyConstants;
import com.shashi.energyservice.dto.EnergyRequest;
import com.shashi.energyservice.dto.EnergyResponse;
import com.shashi.energyservice.model.EnergyUsage;
import com.shashi.energyservice.repository.EnergyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class EnergyService {

    @Autowired
    private EnergyRepository energyRepository;

    public String getCurrentUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return (authentication != null) ? authentication.getName() : null;
    }

    public ResponseEntity<?> addEnergy(EnergyRequest energyRequest) {

        String username = getCurrentUsername();
        EnergyUsage energyUsage = EnergyUsage.builder()
                .username(username)
                .units(energyRequest.getUnits())
                .date(LocalDate.now().toString())
                .build();
        energyRepository.save(energyUsage);
        return ResponseEntity.ok("Energy record added!");

    }

    public ResponseEntity<?> getEnergyHistory() {

        String username = getCurrentUsername();
        return ResponseEntity.ok(energyRepository.findByUsername(username));

    }

    public ResponseEntity<EnergyResponse> getMonthyEnergy(String username, int year, int month) {

        List<EnergyUsage> usages = energyRepository.findByUsername(username);
        double totalEmission = usages.stream()
                .filter(u -> {
                    LocalDate date = LocalDate.parse(u.getDate());
                    return date.getYear() == year && date.getMonthValue() == month;
                })
                .mapToDouble(u -> u.getUnits() * EnergyConstants.ELECTRICITY_EMISSION_FACTOR)
                .sum();
        return ResponseEntity.ok(new EnergyResponse(username, year, month, totalEmission));

    }

    public ResponseEntity<?> updateEnergy(EnergyRequest energyRequest) {
        String username = getCurrentUsername();

        EnergyUsage existingUsage = energyRepository.findByUsernameAndDate(username, LocalDate.now().toString());
        if (existingUsage == null) {
            return ResponseEntity.badRequest().body("No energy record found for today. Please add one first.");
        }

        existingUsage.setUnits(energyRequest.getUnits());
        energyRepository.save(existingUsage);
        return ResponseEntity.ok("Energy record updated!");
    }

    public ResponseEntity<?> deleteEnergy() {
        String username = getCurrentUsername();

        EnergyUsage existingUsage = energyRepository.findByUsernameAndDate(username, LocalDate.now().toString());
        if (existingUsage == null) {
            return ResponseEntity.badRequest().body("No energy record found for today.");
        }

        energyRepository.delete(existingUsage);
        return ResponseEntity.ok("Energy record deleted!");
    }
}
