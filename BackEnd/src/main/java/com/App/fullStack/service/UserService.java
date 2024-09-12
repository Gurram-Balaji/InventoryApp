package com.App.fullStack.service;

import com.App.fullStack.ApplicationConfig.JwtProvider;
import com.App.fullStack.exception.FoundException;
import com.App.fullStack.pojos.User;
import com.App.fullStack.repositories.UserRepository;
import com.App.fullStack.responseHandler.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
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

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @SuppressWarnings({ "rawtypes", "unchecked" })
    public ApiResponse AddUser(User user) {

        if (userRepository.existsByEmail(user.getEmail())) {
            throw new FoundException("Email Is Already Used With Another Account");
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        User savedUser = userRepository.save(user);
        userRepository.save(savedUser);
        Authentication authentication = new UsernamePasswordAuthenticationToken(user.getEmail(), user.getPassword());
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String token = JwtProvider.generateToken(authentication);
        return new ApiResponse(true, "Register Success", token);
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
}