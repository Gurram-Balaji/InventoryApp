package com.App.fullStack.controller;

import com.App.fullStack.pojos.User;
import com.App.fullStack.responseHandler.ApiResponse;
import com.App.fullStack.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class UserController {

    @Autowired
    private UserService userService;

    // User signup (registration)
    @PostMapping("/signup")
    public ResponseEntity<ApiResponse<User>> createUserHandler(@RequestBody User user) {
        @SuppressWarnings("unchecked")
        ApiResponse<User> response = userService.AddUser(user);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    // User signin (login)
    @PostMapping("/signin")
    public ResponseEntity<ApiResponse<User>> signin(@RequestBody User loginRequest) {
        @SuppressWarnings("unchecked")
        ApiResponse<User> response = userService.loginUser(loginRequest);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    // Get the username based on the authorization token (assuming JWT or similar)
    @GetMapping("/name")
    public ResponseEntity<ApiResponse<String>> getName() {
        String username = userService.GetUsername();
        ApiResponse<String> response = new ApiResponse<>(true, "User Found.", username);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/profile")
    public ResponseEntity<ApiResponse<User>> getProfile() {
        User profile = userService.GetProfile();
        ApiResponse<User> response = new ApiResponse<>(true, "User Found.", profile);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }


    @PutMapping("/profile")
    public ResponseEntity<ApiResponse<User>> putProfile(@RequestBody User user) {
        User updatedProfile = userService.updateProfile(user);
        ApiResponse<User> response = new ApiResponse<>(true, "User profile updated.", updatedProfile);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

}
