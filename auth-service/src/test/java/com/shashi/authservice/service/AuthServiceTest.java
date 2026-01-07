package com.shashi.authservice.service;

import com.shashi.authservice.dto.AuthResponse;
import com.shashi.authservice.dto.LoginRequest;
import com.shashi.authservice.dto.RegisterRequest;
import com.shashi.authservice.model.User;
import com.shashi.authservice.repository.UserRepository;
import com.shashi.authservice.security.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Arrays;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private AuthService authService;

    private BCryptPasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp() {
        passwordEncoder = new BCryptPasswordEncoder();
    }

    // ---------------- REGISTER ----------------

    @Test
    void register_success() {
        RegisterRequest request = new RegisterRequest();
        request.setUsername("shashi");
        request.setEmail("shashi@gmail.com");
        request.setPassword("Strong@123");

        when(userRepository.existsByUsername("shashi")).thenReturn(false);
        when(userRepository.existsByEmail("shashi@gmail.com")).thenReturn(false);

        ResponseEntity<String> response = authService.register(request);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals("User registered successfully", response.getBody());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void register_usernameAlreadyExists() {
        RegisterRequest request = new RegisterRequest();
        request.setUsername("shashi");

        when(userRepository.existsByUsername("shashi")).thenReturn(true);

        ResponseEntity<String> response = authService.register(request);

        assertEquals(400, response.getStatusCodeValue());
        assertEquals("Username is already taken!", response.getBody());
        verify(userRepository, never()).save(any());
    }

    @Test
    void register_emailAlreadyExists() {
        RegisterRequest request = new RegisterRequest();
        request.setUsername("shashi");
        request.setEmail("shashi@gmail.com");

        when(userRepository.existsByUsername("shashi")).thenReturn(false);
        when(userRepository.existsByEmail("shashi@gmail.com")).thenReturn(true);

        ResponseEntity<String> response = authService.register(request);

        assertEquals(400, response.getStatusCodeValue());
        assertEquals("Email is already in use!", response.getBody());
    }

    @Test
    void register_invalidEmail() {
        RegisterRequest request = new RegisterRequest();
        request.setUsername("shashi");
        request.setEmail("invalid-email");
        request.setPassword("Strong@123");

        when(userRepository.existsByUsername(any())).thenReturn(false);
        when(userRepository.existsByEmail(any())).thenReturn(false);

        ResponseEntity<String> response = authService.register(request);

        assertEquals(400, response.getStatusCodeValue());
        assertEquals("Email format is invalid", response.getBody());
    }

    @Test
    void register_weakPassword() {
        RegisterRequest request = new RegisterRequest();
        request.setUsername("shashi");
        request.setEmail("shashi@gmail.com");
        request.setPassword("weak");

        when(userRepository.existsByUsername(any())).thenReturn(false);
        when(userRepository.existsByEmail(any())).thenReturn(false);

        ResponseEntity<String> response = authService.register(request);

        assertEquals(400, response.getStatusCodeValue());
        assertTrue(response.getBody().contains("Password must be"));
    }

    // ---------------- LOGIN ----------------

    @Test
    void login_success() {
        LoginRequest request = new LoginRequest();
        request.setUsername("shashi");
        request.setPassword("Strong@123");

        User user = new User();
        user.setUsername("shashi");
        user.setRole("USER");
        user.setPassword(passwordEncoder.encode("Strong@123"));

        when(userRepository.findByUsername("shashi"))
                .thenReturn(Optional.of(user));
        when(jwtUtil.generateToken("shashi", "USER"))
                .thenReturn("jwt-token");

        ResponseEntity<?> response = authService.login(request);

        assertEquals(200, response.getStatusCodeValue());
        AuthResponse authResponse = (AuthResponse) response.getBody();
        assertNotNull(authResponse);
        assertEquals("jwt-token", authResponse.getToken());
        assertEquals("shashi", authResponse.getUsername());
        assertEquals("USER", authResponse.getRole());
    }

    @Test
    void login_userNotFound() {
        LoginRequest request = new LoginRequest();
        request.setUsername("shashi");

        when(userRepository.findByUsername("shashi"))
                .thenReturn(Optional.empty());

        ResponseEntity<?> response = authService.login(request);

        assertEquals(401, response.getStatusCodeValue());
        assertEquals("User does not exist", response.getBody());
    }

    @Test
    void login_invalidPassword() {
        LoginRequest request = new LoginRequest();
        request.setUsername("shashi");
        request.setPassword("wrong");

        User user = new User();
        user.setUsername("shashi");
        user.setPassword(passwordEncoder.encode("Strong@123"));

        when(userRepository.findByUsername("shashi"))
                .thenReturn(Optional.of(user));

        ResponseEntity<?> response = authService.login(request);

        assertEquals(401, response.getStatusCodeValue());
        assertEquals("Invalid credentials", response.getBody());
    }

    // ---------------- GET ALL USERNAMES ----------------

    @Test
    void getAllUsernames_success() {
        when(userRepository.findAllUsernames())
                .thenReturn(Arrays.asList("user1", "user2"));

        ResponseEntity<?> response = authService.getAllUsernames();

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(2, ((java.util.List<?>) response.getBody()).size());
    }
}
