package com.Sistema.PrestamosBancarios.service;

import com.Sistema.PrestamosBancarios.dto.request.LoginRequest;
import com.Sistema.PrestamosBancarios.dto.request.RegisterRequest;
import com.Sistema.PrestamosBancarios.dto.response.AuthResponse;
import com.Sistema.PrestamosBancarios.exception.DuplicateEmailException;
import com.Sistema.PrestamosBancarios.mapper.UserMapper;
import com.Sistema.PrestamosBancarios.model.User;
import com.Sistema.PrestamosBancarios.repository.UserRepository;
import com.Sistema.PrestamosBancarios.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;

    public AuthResponse register(RegisterRequest request) {
        log.info("Registering new user with email: {}", request.getEmail());
        if (userRepository.existsByEmail(request.getEmail())) {
            log.warn("Registration failed - email already in use: {}", request.getEmail());
            throw new DuplicateEmailException("Email already in use: " + request.getEmail());
        }

        String encodedPassword = passwordEncoder.encode(request.getPassword());
        User user = userMapper.toEntity(request, encodedPassword);
        userRepository.save(user);

        UserDetails userDetails = userDetailsService.loadUserByUsername(user.getEmail());
        String token = jwtUtil.generateToken(userDetails);

        log.info("User registered successfully: {} with role: {}", user.getEmail(), user.getRole());
        return new AuthResponse(token, user.getEmail(), user.getRole().name());
    }

    public AuthResponse login(LoginRequest request) {
        log.info("Login attempt for email: {}", request.getEmail());
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
            );
            log.info("Authentication successful for email: {}", request.getEmail());
        } catch (Exception e) {
            log.error("Authentication failed for email: {} | Error: {}", request.getEmail(), e.getMessage(), e);
            throw e;
        }

        UserDetails userDetails = userDetailsService.loadUserByUsername(request.getEmail());
        String token = jwtUtil.generateToken(userDetails);

        User user = userRepository.findByEmail(request.getEmail()).orElseThrow();
        log.info("Login successful for email: {}, role: {}", user.getEmail(), user.getRole());
        return new AuthResponse(token, user.getEmail(), user.getRole().name());
    }
}
