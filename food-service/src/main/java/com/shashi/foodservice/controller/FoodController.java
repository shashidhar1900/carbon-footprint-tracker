package com.shashi.foodservice.controller;

import com.shashi.foodservice.dto.FoodRequest;
import com.shashi.foodservice.dto.FoodResponse;
import com.shashi.foodservice.model.FoodUsage;
import com.shashi.foodservice.service.FoodService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/food")
public class FoodController {

    @Autowired
    private FoodService foodService;

    @PostMapping("/add")
    public ResponseEntity<?> addFood(@RequestBody FoodRequest request) {
        return foodService.addFood(request);
    }

    @PutMapping("/update")
    public ResponseEntity<?> updateFood(@RequestBody FoodRequest request) {
        return foodService.updateFood(request);
    }

    @DeleteMapping("delete/{type}")
    public ResponseEntity<?> deleteFood(@PathVariable String type) {
        return foodService.deleteFood(type);
    }

    @GetMapping("/history")
    public ResponseEntity<List<FoodUsage>> getHistory() {
        return foodService.getHistory();
    }

    @GetMapping("/history/{username}/{year}/{month}")
    public ResponseEntity<FoodResponse> getMonthlyFood(@PathVariable String username, @PathVariable int year, @PathVariable int month) {
        return foodService.getMonthlyFood(username, year, month);
    }
}
