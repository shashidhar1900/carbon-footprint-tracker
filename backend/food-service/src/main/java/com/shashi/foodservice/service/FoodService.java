package com.shashi.foodservice.service;

import com.shashi.foodservice.constants.FoodConstants;
import com.shashi.foodservice.dto.FoodRequest;
import com.shashi.foodservice.dto.FoodResponse;
import com.shashi.foodservice.model.FoodUsage;
import com.shashi.foodservice.repository.FoodRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class FoodService {

    @Autowired
    private FoodRepository foodRepository;

    private String getCurrentUsername() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return (auth != null) ? auth.getName() : null;
    }

    public ResponseEntity<?> addFood(FoodRequest request) {
        String username = getCurrentUsername();
        String type = request.getType();

        if(foodRepository.findByUsernameAndDateAndType(username, LocalDate.now().toString(), type) != null) {
            return ResponseEntity.badRequest().body("Food record for today already exists with that type. Please update it instead!!.");
        }

        FoodUsage usage = FoodUsage.builder()
                .username(username)
                .type(request.getType())
                .quantity(request.getQuantity())
                .date(LocalDate.now().toString())
                .build();

        foodRepository.save(usage);
        return ResponseEntity.ok("Food record added!");
    }

    public ResponseEntity<List<FoodUsage>> getHistory() {
        String username = getCurrentUsername();
        return ResponseEntity.ok(foodRepository.findByUsername(username));
    }

    public ResponseEntity<FoodResponse> getMonthlyFood(String username, int year, int month) {

        List<FoodUsage> usages = foodRepository.findByUsername(username);
        double totalEmission = usages.stream()
                .filter(u -> {
                    LocalDate date = LocalDate.parse(u.getDate());
                    return date.getYear() == year && date.getMonthValue() == month;
                })
                .mapToDouble(u -> {
                    double emissionFactor = 0.0;
                    switch (u.getType().toLowerCase()) {
                        case "veg":
                            emissionFactor = FoodConstants.VEG_EMISSION_FACTOR;
                            break;
                        case "non_veg":
                            emissionFactor = FoodConstants.NONVEG_EMISSION_FACTOR;
                            break;
                        case "junk":
                            emissionFactor = FoodConstants.JUNK_EMISSION_FACTOR;
                            break;
                        // Add more modes as needed
                        default:
                            emissionFactor = 0.5; // Default factor
                    }
                    return u.getQuantity() * emissionFactor;
                })
                .sum();
        return ResponseEntity.ok(new FoodResponse(username, year, month, totalEmission));
    }

    public ResponseEntity<?> updateFood(FoodRequest request) {
        String username = getCurrentUsername();
        String type = request.getType();

        FoodUsage existingUsage = foodRepository.findByUsernameAndDateAndType(username, LocalDate.now().toString(), type);
        if (existingUsage != null) {
            foodRepository.delete(existingUsage);
            FoodUsage newUsage = FoodUsage.builder()
                    .username(username)
                    .type(request.getType())
                    .quantity(request.getQuantity())
                    .date(LocalDate.now().toString())
                    .build();
            foodRepository.save(newUsage);
            return ResponseEntity.ok("Food record updated!");
        } else {
            return ResponseEntity.badRequest().body("No food record found for today to update.");
        }
    }

    public ResponseEntity<?> deleteFood(String type) {
        String username = getCurrentUsername();
        FoodUsage existingUsage = foodRepository.findByUsernameAndDateAndType(username, LocalDate.now().toString(),type);
        if (existingUsage != null) {
            foodRepository.delete(existingUsage);
            return ResponseEntity.ok("Today's Food record deleted!");
        } else {
            return ResponseEntity.badRequest().body("No food record found for today to delete.");
        }
    }
}
