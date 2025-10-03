package com.shashi.energyservice.controller;


import com.shashi.energyservice.dto.EnergyRequest;
import com.shashi.energyservice.dto.EnergyResponse;
import com.shashi.energyservice.service.EnergyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/energy")
public class EnergyController {

    @Autowired
    private EnergyService energyService;

    @PostMapping("/add")
    public ResponseEntity<?> addEnergy(@RequestBody EnergyRequest energyRequest) {
        return energyService.addEnergy(energyRequest);
    }

    @GetMapping("/history")
    public ResponseEntity<?> getEnergyHistory() {
        return energyService.getEnergyHistory();
    }

    @GetMapping("/history/{username}/{year}/{month}")
    public ResponseEntity<EnergyResponse> getMonthlyEnergy(@PathVariable String username, @PathVariable int year, @PathVariable int month) {
        return energyService.getMonthyEnergy(username,year,month);
    }
}
