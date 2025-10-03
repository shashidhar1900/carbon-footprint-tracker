package com.shashi.authservice.service;


import com.shashi.authservice.security.JwtUtil;
import com.shashi.authservice.dto.AuthResponse;
import com.shashi.authservice.dto.LoginRequest;
import com.shashi.authservice.dto.RegisterRequest;
import com.shashi.authservice.model.User;
import com.shashi.authservice.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private JwtUtil jwtUtil;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public ResponseEntity<String> register(RegisterRequest registerRequest) {

        if(userRepository.existsByUsername(registerRequest.getUsername())) {
            return ResponseEntity.badRequest().body("Username is already taken!");
        }

        if(userRepository.existsByEmail(registerRequest.getEmail())) {
            return ResponseEntity.badRequest().body("Email is already in use!");
        }

        User user = new User();
        user.setUsername(registerRequest.getUsername());
        user.setEmail(registerRequest.getEmail());
        user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        user.setRole("USER");
        userRepository.save(user);
        return ResponseEntity.ok("User registered successfully");

    }

    public ResponseEntity<AuthResponse> login(LoginRequest loginRequest) {
        User user = userRepository.findByUsername(loginRequest.getUsername()).get();
        if (user == null || !passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            return ResponseEntity.status(401).body(null);
        }


        String token = jwtUtil.generateToken(user.getUsername(), user.getRole());

        AuthResponse authResponse = new AuthResponse(token, user.getUsername(), user.getRole());
        return ResponseEntity.ok(authResponse);
    }

    public ResponseEntity<?> getAllUsernames() {
        return ResponseEntity.ok(userRepository.findAllUsernames());
    }
}
