package com.Sistema.PrestamosBancarios.controller;

import com.Sistema.PrestamosBancarios.dto.request.LoginRequest;
import com.Sistema.PrestamosBancarios.dto.request.RegisterRequest;
import com.Sistema.PrestamosBancarios.dto.response.AuthResponse;
import com.Sistema.PrestamosBancarios.dto.response.UserResponse;
import com.Sistema.PrestamosBancarios.mapper.UserMapper;
import com.Sistema.PrestamosBancarios.model.User;
import com.Sistema.PrestamosBancarios.repository.UserRepository;
import com.Sistema.PrestamosBancarios.exception.ResourceNotFoundException;
import com.Sistema.PrestamosBancarios.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "User registration and login")
@Slf4j
public class AuthController {

    private final AuthService authService;
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @PostMapping("/register")
    @Operation(summary = "Register a new user", description = "Create a new user account with email, username and password",
            responses = {
                    @ApiResponse(responseCode = "201", description = "User registered successfully"),
                    @ApiResponse(responseCode = "409", description = "Email already in use")
            })
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        log.info("POST /api/auth/register - Registering user: {}", request.getEmail());
        AuthResponse response = authService.register(request);
        log.info("User registered successfully: {}", request.getEmail());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/login")
    @Operation(summary = "Login", description = "Authenticate with email and password, returns JWT token",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Login successful"),
                    @ApiResponse(responseCode = "401", description = "Invalid credentials")
            })
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        log.info("POST /api/auth/login - Login attempt for: {}", request.getEmail());
        AuthResponse response = authService.login(request);
        log.info("Login successful for: {}", request.getEmail());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/me")
    @Operation(summary = "Get current user", description = "Returns the authenticated user's profile")
    public ResponseEntity<UserResponse> getCurrentUser(Authentication authentication) {
        log.info("GET /api/auth/me - User: {}", authentication.getName());
        User user = userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return ResponseEntity.ok(userMapper.toResponse(user));
    }
}
