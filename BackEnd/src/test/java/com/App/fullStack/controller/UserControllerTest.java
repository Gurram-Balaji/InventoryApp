package com.App.fullStack.controller;

import com.App.fullStack.exception.FoundException;
import com.App.fullStack.pojos.User;
import com.App.fullStack.responseHandler.ApiResponse;
import com.App.fullStack.service.UserService;
import jakarta.mail.MessagingException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserControllerTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    // Test case for user signup successfully
    @Test
    void createUserHandler_Success() throws MessagingException {
        User mockUser = new User();
        ApiResponse<String> mockResponse = new ApiResponse<>(true, "User registered successfully.", "user123");
        when(userService.AddUser(mockUser)).thenReturn(mockResponse);

        ResponseEntity<ApiResponse<String>> result = userController.createUserHandler(mockUser);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertTrue(Objects.requireNonNull(result.getBody()).isSuccess());
        assertEquals(mockResponse, result.getBody());

        verify(userService, times(1)).AddUser(mockUser);
    }

    // Test case for user sign in successfully
    @Test
    void signIn_Success() {
        User mockLoginRequest = new User();
        ApiResponse<String> mockResponse = new ApiResponse<>(true, "Login successful.", "token123");
        when(userService.loginUser(mockLoginRequest)).thenReturn(mockResponse);

        ResponseEntity<ApiResponse<String>> result = userController.signIn(mockLoginRequest);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertTrue(Objects.requireNonNull(result.getBody()).isSuccess());
        assertEquals(mockResponse, result.getBody());

        verify(userService, times(1)).loginUser(mockLoginRequest);
    }

    // Test case for getting username successfully
    @Test
    void getName_Success() {
        String mockUsername = "user123";
        when(userService.GetUsername()).thenReturn(mockUsername);

        ResponseEntity<ApiResponse<String>> result = userController.getName();

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertTrue(Objects.requireNonNull(result.getBody()).isSuccess());
        assertEquals(mockUsername, result.getBody().getPayload());

        verify(userService, times(1)).GetUsername();
    }

    // Test case for getting user profile successfully
    @Test
    void getProfile_Success() {
        User mockProfile = new User();
        when(userService.GetProfile()).thenReturn(mockProfile);

        ResponseEntity<ApiResponse<User>> result = userController.getProfile();

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertTrue(Objects.requireNonNull(result.getBody()).isSuccess());
        assertEquals(mockProfile, result.getBody().getPayload());

        verify(userService, times(1)).GetProfile();
    }

    // Test case for updating user profile successfully
    @Test
    void putProfile_Success() {
        User mockUser = new User();
        User updatedProfile = new User();
        when(userService.updateProfile(mockUser)).thenReturn(updatedProfile);

        ResponseEntity<ApiResponse<User>> result = userController.putProfile(mockUser);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertTrue(Objects.requireNonNull(result.getBody()).isSuccess());
        assertEquals(updatedProfile, result.getBody().getPayload());

        verify(userService, times(1)).updateProfile(mockUser);
    }

    // Test case for email verification successful
    @Test
    void verifyEmail_Success() {
        String token = "validToken";
        when(userService.verifyEmail(token)).thenReturn(true);

        ResponseEntity<ApiResponse<String>> result = userController.verifyEmail(token);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertTrue(Objects.requireNonNull(result.getBody()).isSuccess());
        assertEquals("Email verified successfully.", result.getBody().getPayload());

        verify(userService, times(1)).verifyEmail(token);
    }

    // Test case for email verification failure
    @Test
    void verifyEmail_Failure() {
        String token = "invalidToken";
        when(userService.verifyEmail(token)).thenReturn(false);

        FoundException thrown = assertThrows(FoundException.class, () -> userController.verifyEmail(token));
        assertEquals("Invalid or expired token.", thrown.getMessage());

        verify(userService, times(1)).verifyEmail(token);
    }
}
