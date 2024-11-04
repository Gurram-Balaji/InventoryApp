package com.App.fullStack.ApplicationConfig;

import com.App.fullStack.exception.FoundException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.context.SecurityContextHolder;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;

import javax.crypto.SecretKey;
import java.io.IOException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class JwtTokenValidatorTest {

    private JwtTokenValidator jwtTokenValidator;
    private MockHttpServletRequest request;
    private MockHttpServletResponse response;
    private FilterChain filterChain;

    @BeforeEach
    void setUp() {
        jwtTokenValidator = new JwtTokenValidator();
        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();
        filterChain = mock(FilterChain.class);

        // Clear the security context before each test
        SecurityContextHolder.clearContext();
    }

    @Test
    void testValidJwtToken() throws ServletException, IOException {
        // Mocking a valid JWT token
        SecretKey key = Keys.hmacShaKeyFor("wpembytrwcvnryxksdbqwjebruyGHyudqgwveytrtrCSnwifoesarjbwe".getBytes());
        String jwt = Jwts.builder()
                .claim("email", "testuser@example.com")
                .claim("authorities", "ROLE_USER")
                .signWith(key)
                .compact();

        request.addHeader("Authorization", "Bearer " + jwt);

        // Call the filter method
        jwtTokenValidator.doFilterInternal(request, response, filterChain);

        // Verify that authentication is set in the security context
        assertNotNull(SecurityContextHolder.getContext().getAuthentication());
        assertEquals("testuser@example.com", SecurityContextHolder.getContext().getAuthentication().getPrincipal());

        // Ensure the filter chain proceeds
        verify(filterChain, times(1)).doFilter(request, response);
    }

    @Test
    void testInvalidJwtToken() throws ServletException, IOException {
        String invalidJwt = "invalid.jwt.token";

        request.addHeader("Authorization", "Bearer " + invalidJwt);

        assertThrows(FoundException.class, () -> {
            jwtTokenValidator.doFilterInternal(request, response, filterChain);
        });

        // Ensure the filter chain does not proceed
        verify(filterChain, times(0)).doFilter(request, response);
    }

    @Test
    void testNoJwtToken() throws ServletException, IOException {
        // No JWT token in the request header
        jwtTokenValidator.doFilterInternal(request, response, filterChain);

        // Ensure no authentication is set in the security context
        assertNull(SecurityContextHolder.getContext().getAuthentication());

        // Ensure the filter chain proceeds
        verify(filterChain, times(1)).doFilter(request, response);
    }

    @Test
    void testJwtTokenWithoutBearerPrefix() throws ServletException, IOException {
        SecretKey key = Keys.hmacShaKeyFor("wpembytrwcvnryxksdbqwjebruyGHyudqgwveytrtrCSnwifoesarjbwe".getBytes());
        String jwt = Jwts.builder()
                .claim("email", "testuser@example.com")
                .claim("authorities", "ROLE_USER")
                .signWith(key)
                .compact();

        request.addHeader("Authorization", jwt); // No "Bearer " prefix

        // Call the filter method
        jwtTokenValidator.doFilterInternal(request, response, filterChain);

        // No authentication should be set
        assertNull(SecurityContextHolder.getContext().getAuthentication());

        // Ensure the filter chain proceeds
        verify(filterChain, times(1)).doFilter(request, response);
    }


    @Test
    void testJwtTokenWithInvalidSignature() throws ServletException, IOException {
        // Mock a JWT token with an invalid signature
        String jwt = "Bearer eyJhbGciOiJIUzI1NiJ9.eyJlbWFpbCI6InRlc3R1c2VyQGV4YW1wbGUuY29tIiwiYXV0aG9yaXRpZXMiOiJST0xFX1VTRVIifQ.invalidsignature";
        request.addHeader("Authorization", jwt);

        FoundException exception = assertThrows(FoundException.class, () ->
            jwtTokenValidator.doFilterInternal(request, response, filterChain)
        );

        assertTrue(exception.getMessage().contains("Invalid token"));

        // Ensure no authentication is set in the security context
        assertNull(SecurityContextHolder.getContext().getAuthentication());

        // Ensure the filter chain does not proceed
        verify(filterChain, never()).doFilter(request, response);
    }

    @Test
    void testMissingJwtToken() throws ServletException, IOException {
        // No Authorization header in the request
        jwtTokenValidator.doFilterInternal(request, response, filterChain);

        // No authentication should be set
        assertNull(SecurityContextHolder.getContext().getAuthentication());

        // Ensure the filter chain proceeds
        verify(filterChain, times(1)).doFilter(request, response);
    }
    @Test
    void testExpiredJwtToken() throws ServletException, IOException {
        SecretKey key = Keys.hmacShaKeyFor("wpembytrwcvnryxksdbqwjebruyGHyudqgwveytrtrCSnwifoesarjbwe".getBytes());
        String expiredJwt = Jwts.builder()
                .claim("email", "testuser@example.com")
                .claim("authorities", "ROLE_USER")
                .setExpiration(Date.from(Instant.now().minus(1, ChronoUnit.DAYS)))
                .signWith(key)
                .compact();

        request.addHeader("Authorization", "Bearer " + expiredJwt);

        assertThrows(FoundException.class, () -> {
            jwtTokenValidator.doFilterInternal(request, response, filterChain);
        });

        // Ensure the filter chain does not proceed
        verify(filterChain, times(0)).doFilter(request, response);
    }

    @Test
    void testJwtTokenWithInvalidKey() throws ServletException, IOException {
        SecretKey key = Keys.hmacShaKeyFor("differentkeyexampledifferentkeyexample12345".getBytes());
        String jwt = Jwts.builder()
                .claim("email", "testuser@example.com")
                .claim("authorities", "ROLE_USER")
                .signWith(key)
                .compact();

        request.addHeader("Authorization", "Bearer " + jwt);

        assertThrows(FoundException.class, () -> {
            jwtTokenValidator.doFilterInternal(request, response, filterChain);
        });

        // Ensure the filter chain does not proceed
        verify(filterChain, times(0)).doFilter(request, response);
    }




}
