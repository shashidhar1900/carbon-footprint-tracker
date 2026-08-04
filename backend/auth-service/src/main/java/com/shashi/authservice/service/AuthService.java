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

        if (userRepository.existsByUsername(registerRequest.getUsername())) {
            return ResponseEntity.badRequest().body("Username is already taken!");
        }

        if (userRepository.existsByEmail(registerRequest.getEmail())) {
            return ResponseEntity.badRequest().body("Email is already in use!");
        }

        String email = registerRequest.getEmail();
        if (!isValidEmail(email)) {
            return ResponseEntity.badRequest().body("Email format is invalid");
        }

        String password = registerRequest.getPassword();
        if (!isStrongPassword(password)) {
            return ResponseEntity.badRequest().body("Password must be at least 8 characters and include uppercase, lowercase, digit and special character.");
        }

        User user = new User();
        user.setUsername(registerRequest.getUsername());
        user.setEmail(registerRequest.getEmail());
        user.setPassword(passwordEncoder.encode(password));
        user.setRole("USER");
        userRepository.save(user);
        return ResponseEntity.ok("User registered successfully");
    }

    private boolean isValidEmail(String email) {
        if (email == null) {
            return false;
        }
        String trimmed = email.trim();
        String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";
        return java.util.regex.Pattern.compile(emailRegex).matcher(trimmed).matches();
    }

    private boolean isStrongPassword(String password) {
        if (password == null || password.length() < 8) {
            return false;
        }
        boolean hasUpper = java.util.regex.Pattern.compile("[A-Z]").matcher(password).find();
        boolean hasLower = java.util.regex.Pattern.compile("[a-z]").matcher(password).find();
        boolean hasDigit = java.util.regex.Pattern.compile("\\d").matcher(password).find();
        boolean hasSpecial = java.util.regex.Pattern.compile("[^A-Za-z0-9]").matcher(password).find();
        return hasUpper && hasLower && hasDigit && hasSpecial;
    }

    public ResponseEntity<?> login(LoginRequest loginRequest) {
        User user = userRepository.findByUsername(loginRequest.getUsername()).orElse(null);
        if (user == null) {
            return ResponseEntity.status(401).body("User does not exist");
        }
        if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            return ResponseEntity.status(401).body("Invalid credentials");
        }

        String token = jwtUtil.generateToken(user.getUsername(), user.getRole());
        AuthResponse authResponse = new AuthResponse(token, user.getUsername(), user.getRole());
        return ResponseEntity.ok(authResponse);
    }

    public ResponseEntity<?> getAllUsernames() {
        return ResponseEntity.ok(userRepository.findAllUsernames());
    }
}
