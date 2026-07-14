package com.Sistema.PrestamosBancarios;

import com.Sistema.PrestamosBancarios.dto.request.LoginRequest;
import com.Sistema.PrestamosBancarios.dto.request.RegisterRequest;
import com.Sistema.PrestamosBancarios.dto.response.AuthResponse;
import com.Sistema.PrestamosBancarios.exception.DuplicateEmailException;
import com.Sistema.PrestamosBancarios.mapper.UserMapper;
import com.Sistema.PrestamosBancarios.model.Role;
import com.Sistema.PrestamosBancarios.model.User;
import com.Sistema.PrestamosBancarios.repository.UserRepository;
import com.Sistema.PrestamosBancarios.security.JwtUtil;
import com.Sistema.PrestamosBancarios.service.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private UserDetailsService userDetailsService;

    @InjectMocks
    private AuthService authService;

    private RegisterRequest registerRequest;
    private User user;

    @BeforeEach
    void setUp() {
        registerRequest = new RegisterRequest("test@test.com", "TestUser", "password123");

        user = User.builder()
                .id(1L)
                .email("test@test.com")
                .username("TestUser")
                .password("encodedPassword")
                .role(Role.USER)
                .build();
    }

    @Test
    void register_ShouldCreateUserAndReturnToken() {
        when(userRepository.existsByEmail("test@test.com")).thenReturn(false);
        when(passwordEncoder.encode("password123")).thenReturn("encodedPassword");
        when(userMapper.toEntity(registerRequest, "encodedPassword")).thenReturn(user);
        when(userRepository.save(any(User.class))).thenReturn(user);

        UserDetails userDetails = new org.springframework.security.core.userdetails.User(
                "test@test.com", "encodedPassword", Collections.emptyList());
        when(userDetailsService.loadUserByUsername("test@test.com")).thenReturn(userDetails);
        when(jwtUtil.generateToken(userDetails)).thenReturn("jwt-token");

        AuthResponse response = authService.register(registerRequest);

        assertNotNull(response);
        assertEquals("jwt-token", response.getToken());
        assertEquals("test@test.com", response.getEmail());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void register_ShouldThrowWhenEmailExists() {
        when(userRepository.existsByEmail("test@test.com")).thenReturn(true);

        assertThrows(DuplicateEmailException.class,
                () -> authService.register(registerRequest));
    }

    @Test
    void login_ShouldReturnToken() {
        LoginRequest loginRequest = new LoginRequest("test@test.com", "password123");

        UserDetails userDetails = new org.springframework.security.core.userdetails.User(
                "test@test.com", "encodedPassword", Collections.emptyList());
        when(userDetailsService.loadUserByUsername("test@test.com")).thenReturn(userDetails);
        when(jwtUtil.generateToken(userDetails)).thenReturn("jwt-token");
        when(userRepository.findByEmail("test@test.com")).thenReturn(Optional.of(user));

        AuthResponse response = authService.login(loginRequest);

        assertNotNull(response);
        assertEquals("jwt-token", response.getToken());
        verify(authenticationManager, times(1))
                .authenticate(any());
    }
}
