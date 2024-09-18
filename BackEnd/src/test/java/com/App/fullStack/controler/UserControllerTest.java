package com.App.fullStack.controler;

import com.App.fullStack.controller.UserController;
import com.App.fullStack.pojos.User;
import com.App.fullStack.responseHandler.ApiResponse;
import com.App.fullStack.service.UserService;
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

    @Test
    void createUserHandler_Success() {
        User user = new User();
        ApiResponse<User> response = new ApiResponse<>(true, "User created.", user);
        when(userService.AddUser(user)).thenReturn(response);

        ResponseEntity<ApiResponse<User>> result = userController.createUserHandler(user);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(response.getPayload(), Objects.requireNonNull(result.getBody()).getPayload());
    }

    @Test
    void signIn_Success() {
        User loginRequest = new User();
        ApiResponse<User> response = new ApiResponse<>(true, "Login successful.", loginRequest);
        when(userService.loginUser(loginRequest)).thenReturn(response);

        ResponseEntity<ApiResponse<User>> result = userController.signIn(loginRequest);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(response.getPayload(), Objects.requireNonNull(result.getBody()).getPayload());
    }

    @Test
    void getName_Success() {
        String username = "testUser";
        ApiResponse<String> response = new ApiResponse<>(true, "User Found.", username);
        when(userService.GetUsername()).thenReturn(username);

        ResponseEntity<ApiResponse<String>> result = userController.getName();

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(response.getPayload(), Objects.requireNonNull(result.getBody()).getPayload());
    }
}
