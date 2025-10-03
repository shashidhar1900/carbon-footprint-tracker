package com.shashi.transportservice.controller;



import com.shashi.transportservice.dto.TransportRequest;
import com.shashi.transportservice.dto.TransportResponse;
import com.shashi.transportservice.model.TransportUsage;
import com.shashi.transportservice.service.TransportService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/transport")
public class TransportController {

    @Autowired
    private TransportService transportService;

    @PostMapping("/add")
    public ResponseEntity<?> addTransport(@RequestBody TransportRequest transport) {
        return transportService.addTransport(transport);
    }

    @GetMapping("/history")
    public ResponseEntity<List<TransportUsage>> getHistory() {
        return transportService.getHistory();
    }

    @GetMapping("/history/{username}/{year}/{month}")
    public ResponseEntity<TransportResponse> getMonthlyTransport(@PathVariable String username, @PathVariable int year, @PathVariable int month, @RequestHeader("Authorization") String jwtHeader){
        return transportService.getMonthlyTransport(username, year, month);
    }

    @GetMapping("/history/allUsers/{year}/{month}")
    public ResponseEntity<List<TransportResponse>> getAllUsersMonthlyTransport(@PathVariable int year, @PathVariable int month) {
        return transportService.getAllUsersMonthlyTransport(year, month);
    }
}
