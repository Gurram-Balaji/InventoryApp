package com.App.fullStack.ApplicationConfig;

import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.cors.CorsConfiguration;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

class ApplicationConfigTest {

    private ApplicationConfig applicationConfig;

    @BeforeEach
    void setUp() {
        applicationConfig = new ApplicationConfig();
    }


    @Test
    void testCorsConfigurationSource() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        
        CorsConfiguration corsConfig = applicationConfig.corsConfigurationSource().getCorsConfiguration(request);

        // Validate the CORS configuration
        assertNotNull(corsConfig);
        assertEquals(List.of("http://localhost:3000"), corsConfig.getAllowedOrigins());
        assertEquals(Collections.singletonList("*"), corsConfig.getAllowedMethods());
        assertTrue(corsConfig.getAllowCredentials());
        assertEquals(Collections.singletonList("*"), corsConfig.getAllowedHeaders());
        assertEquals(List.of("Authorization"), corsConfig.getExposedHeaders());
        assertEquals(3600L, corsConfig.getMaxAge());
    }

    @Test
    void testPasswordEncoder() {
        PasswordEncoder passwordEncoder = applicationConfig.passwordEncoder();

        // Validate that BCryptPasswordEncoder is returned
        assertNotNull(passwordEncoder);
        assertTrue(passwordEncoder instanceof BCryptPasswordEncoder);

        // Check if password encoding works as expected
        String rawPassword = "password123";
        String encodedPassword = passwordEncoder.encode(rawPassword);
        assertTrue(passwordEncoder.matches(rawPassword, encodedPassword));
    }
}
