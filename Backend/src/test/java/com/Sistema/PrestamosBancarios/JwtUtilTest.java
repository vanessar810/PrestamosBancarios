package com.Sistema.PrestamosBancarios;

import com.Sistema.PrestamosBancarios.security.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

class JwtUtilTest {

    private JwtUtil jwtUtil;
    private UserDetails userDetails;

    @BeforeEach
    void setUp() {
        jwtUtil = new JwtUtil();
        userDetails = new User("test@test.com", "password", Collections.emptyList());
    }

    private void setField(String fieldName, Object value) throws Exception {
        var field = JwtUtil.class.getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(jwtUtil, value);
    }

    @BeforeEach
    void initJwtUtil() throws Exception {
        jwtUtil = new JwtUtil();
        setField("secretKey", "d2hhdGV2ZXJ5bG9uZ3lvdXdhbnR0b2RvZXNpcXVhbHRvMjU2Yml0cw==");
        setField("jwtExpiration", 86400000L);
    }

    @Test
    void generateToken_ShouldReturnValidToken() {
        String token = jwtUtil.generateToken(userDetails);

        assertNotNull(token);
        assertFalse(token.isEmpty());
    }

    @Test
    void extractEmail_ShouldReturnCorrectEmail() {
        String token = jwtUtil.generateToken(userDetails);
        String extractedEmail = jwtUtil.extractEmail(token);

        assertEquals("test@test.com", extractedEmail);
    }

    @Test
    void isTokenValid_ShouldReturnTrueForValidToken() {
        String token = jwtUtil.generateToken(userDetails);

        assertTrue(jwtUtil.isTokenValid(token, userDetails));
    }

    @Test
    void isTokenValid_ShouldReturnFalseForDifferentUser() {
        String token = jwtUtil.generateToken(userDetails);
        UserDetails otherUser = new User("other@test.com", "password", Collections.emptyList());

        assertFalse(jwtUtil.isTokenValid(token, otherUser));
    }

    @Test
    void extractEmail_ShouldThrowForInvalidToken() {
        assertThrows(Exception.class,
                () -> jwtUtil.extractEmail("invalid.token.here"));
    }
}
