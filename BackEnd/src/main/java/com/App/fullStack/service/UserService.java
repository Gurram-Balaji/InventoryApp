package com.App.fullStack.service;

import com.App.fullStack.ApplicationConfig.JwtProvider;
import com.App.fullStack.exception.FoundException;
import com.App.fullStack.pojos.User;
import com.App.fullStack.repositories.UserRepository;
import com.App.fullStack.responseHandler.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private JavaMailSender mailSender;

    public ApiResponse<String> AddUser(User user) {
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new FoundException("Email Is Already Used With Another Account");
        }
        String token = UUID.randomUUID().toString();
        user.setVerificationToken(token);
        user.setVerified(false);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);
        // Send verification email
        sendVerificationEmail(user.getEmail(), token);
        return new ApiResponse<>(true, "Email sent, Please verify your email.", "Registered successfully.");
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    public ApiResponse loginUser(User loginRequest) {
        String email = loginRequest.getEmail();
        String password = loginRequest.getPassword();
        Authentication authentication = authenticate(email, password);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String token = JwtProvider.generateToken(authentication);
        return new ApiResponse(true, "Logged in Success", token);
    }

    private Authentication authenticate(String email, String password) {

        UserDetails userDetails = loadUserByEmail(email);

        if (userDetails == null) {
            throw new FoundException("Invalid email and password");
        }

        if (!passwordEncoder.matches(password, userDetails.getPassword())) {
            throw new FoundException("Invalid password");
        }
        return new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
    }

    public UserDetails loadUserByEmail(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email);
        if (user == null) {
            throw new FoundException("Invalid email and password. ");
        }
        if(!user.isVerified()){
            throw new FoundException("Email is not verified. ");

        }

        List<GrantedAuthority> authorities = new ArrayList<>();

        return new org.springframework.security.core.userdetails.User(
                user.getEmail(),
                user.getPassword(),
                authorities);
    }

    public String GetUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            String email = (String) authentication.getPrincipal();
            return userRepository.findByEmail(email).getFullName();
        }
        return null;
    }

    public User GetProfile() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            String email = (String) authentication.getPrincipal();
            return userRepository.findByEmail(email);
        }
        throw new FoundException("Invalid email user token.");
    }

    // Method for updating user profile
    public User updateProfile(User updatedUser) {
        // Get current authenticated user's profile
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            String email = (String) authentication.getPrincipal();
            User currentUser = userRepository.findByEmail(email);

            if (currentUser == null) {
                throw new FoundException("User not found.");
            }

            // Update user details
            if (updatedUser.getFullName() != null) {
                currentUser.setFullName(updatedUser.getFullName());
            }
            if (updatedUser.getEmail() != null && !updatedUser.getEmail().equals(currentUser.getEmail())) {
                if (userRepository.existsByEmail(updatedUser.getEmail())) {
                    throw new FoundException("Email is already in use.");
                }
                currentUser.setEmail(updatedUser.getEmail());
            }
            if (updatedUser.getPassword() != null && !updatedUser.getPassword().isEmpty()) {
                currentUser.setPassword(passwordEncoder.encode(updatedUser.getPassword()));
            }

            // Save the updated user profile
            userRepository.save(currentUser);

            return currentUser;
        }

        throw new FoundException("Invalid user token.");
    }

    private void sendVerificationEmail(String email, String token) {
        String url = "http://localhost:3000/verify-email?token=" + token;

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject("Email Verification");
        message.setText("Click the link to verify your email: " + url);
        mailSender.send(message);
    }

    public boolean verifyEmail(String token) {
        User user = userRepository.findByVerificationToken(token)
                .orElseThrow(() -> new IllegalArgumentException("Invalid token"));

        if (user != null && !user.isVerified()) {
            user.setVerified(true);
            userRepository.save(user);
            return true;
        }
        return false;
    }


}
