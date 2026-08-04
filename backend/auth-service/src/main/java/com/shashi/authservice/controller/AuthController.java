package com.shashi.authservice.controller;


import com.shashi.authservice.dto.AuthResponse;
import com.shashi.authservice.dto.LoginRequest;
import com.shashi.authservice.dto.RegisterRequest;
import com.shashi.authservice.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody RegisterRequest registerRequest) {
        return authService.register(registerRequest);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        return authService.login(loginRequest);
    }

    @GetMapping("/users")
    public ResponseEntity<?> getAllUsernames() {
        return authService.getAllUsernames();
    }

}
