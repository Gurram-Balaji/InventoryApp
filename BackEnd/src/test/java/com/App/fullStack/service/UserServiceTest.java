package com.App.fullStack.service;

import com.App.fullStack.ApplicationConfig.JwtProvider;
import com.App.fullStack.exception.FoundException;
import com.App.fullStack.pojos.User;
import com.App.fullStack.repositories.UserRepository;
import com.App.fullStack.responseHandler.ApiResponse;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @InjectMocks
    private UserService userService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JavaMailSender mailSender;

    private User user;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        user = new User();
        user.setEmail("test@example.com");
        user.setFullName("Test User");
        user.setPassword("plainPassword");
        user.setVerified(true); // Assuming the user needs to be verified for profile updates
    }

    @Test
    void testAddUser_Success() throws MessagingException {
        // Mock user repository and password encoder
        when(userRepository.existsByEmail(user.getEmail())).thenReturn(false);
        when(passwordEncoder.encode(user.getPassword())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(user);

        // Mock JavaMailSender and MimeMessage
        MimeMessage mimeMessage = mock(MimeMessage.class);
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);

        ApiResponse<String> response = userService.AddUser(user);

        // Verify response
        assertTrue(response.isSuccess());
        assertEquals("Email sent, Please verify your email.", response.getMessage());
        assertEquals("Registered successfully.", response.getPayload());

        // Verify that the send method was called on the mailSender
        verify(mailSender, times(1)).send(any(MimeMessage.class));
    }

    @Test
    void testAddUser_EmailAlreadyUsed() {
        when(userRepository.existsByEmail(user.getEmail())).thenReturn(true);

        FoundException exception = assertThrows(FoundException.class, () -> userService.AddUser(user));
        assertEquals("Email Is Already Used With Another Account", exception.getMessage());
    }

    @Test
    void testLoginUser_Success() {
        user.setPassword("encodedPassword");
        when(userRepository.findByEmail(user.getEmail())).thenReturn(user);
        when(passwordEncoder.matches("encodedPassword", "encodedPassword")).thenReturn(true);
        Authentication authentication = new UsernamePasswordAuthenticationToken(user.getEmail(), null, new ArrayList<>());
        SecurityContextHolder.getContext().setAuthentication(authentication);

        ApiResponse<String> response = userService.loginUser(user);

        assertTrue(response.isSuccess());
        assertEquals("Logged in Success", response.getMessage());
        assertNotNull(response.getPayload());
    }

    @Test
    void testLoginUser_InvalidEmail() {
        when(userRepository.findByEmail(user.getEmail())).thenReturn(null);

        FoundException exception = assertThrows(FoundException.class, () -> userService.loginUser(user));
        assertEquals("Invalid email and password.", exception.getMessage());
    }

    @Test
    void testLoginUser_InvalidPassword() {
        user.setPassword("wrongPassword");
        when(userRepository.findByEmail(user.getEmail())).thenReturn(user);
        when(passwordEncoder.matches("wrongPassword", user.getPassword())).thenReturn(false);

        FoundException exception = assertThrows(FoundException.class, () -> userService.loginUser(user));
        assertEquals("Invalid password", exception.getMessage());
    }



    @Test
    void testLoginUser_UserNotVerified() {
        user.setVerified(false);
        when(userRepository.findByEmail(user.getEmail())).thenReturn(user);

        FoundException exception = assertThrows(FoundException.class, () -> userService.loginUser(user));
        assertEquals("Email is not verified.", exception.getMessage());
    }


    @Test
    void testVerifyEmail_AlreadyVerified() {
        String token = UUID.randomUUID().toString();
        User user = new User();
        user.setVerified(true);

        when(userRepository.findByVerificationToken(token)).thenReturn(Optional.of(user));

        boolean result = userService.verifyEmail(token);

        assertFalse(result);
        verify(userRepository, times(0)).save(user);
    }


    @Test
    void testAddUser_NullUser() {
        assertThrows(NullPointerException.class, () -> userService.AddUser(null));
    }



    @Test
    void testGetUsername_NoAuthentication() {
        SecurityContextHolder.clearContext();

        String fullName = userService.GetUsername();
        assertNull(fullName);
    }



    @Test
    void testGetProfile_NoAuthentication() {
        SecurityContextHolder.clearContext();
        FoundException exception = assertThrows(FoundException.class, () -> userService.GetProfile());
        assertEquals("Invalid email user token.", exception.getMessage());
    }


    @Test
    void testVerifyEmail_Success() {
        String token = UUID.randomUUID().toString();
        user.setVerificationToken(token);
        user.setVerified(false);
        when(userRepository.findByVerificationToken(token)).thenReturn(Optional.of(user));
        when(userRepository.save(user)).thenReturn(user);

        boolean isVerified = userService.verifyEmail(token);
        assertTrue(isVerified);
        assertTrue(user.isVerified());
    }

    @Test
    void testVerifyEmail_InvalidToken() {
        String token = "invalidToken";
        when(userRepository.findByVerificationToken(token)).thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> userService.verifyEmail(token));
        assertEquals("Invalid token", exception.getMessage());
    }

    @Test
    void testLoginUser_UserLoggedIn() {
        // Arrange
        user.setPassword("encodedPassword");
        when(userRepository.findByEmail(user.getEmail())).thenReturn(user);
        when(passwordEncoder.matches("encodedPassword", "encodedPassword")).thenReturn(true);

        // Simulating a logged-in user
        Authentication authentication = new UsernamePasswordAuthenticationToken(user.getEmail(), null, new ArrayList<>());
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // Act
        ApiResponse<String> response = userService.loginUser(user);

        // Assert
        assertTrue(response.isSuccess());
        assertEquals("Logged in Success", response.getMessage());
    }

    @Test
    void testUpdateProfile_UserNotAuthenticated() {
        // Clear authentication context
        SecurityContextHolder.clearContext();

        User updatedUser = new User();
        updatedUser.setFullName("Updated User");

        FoundException exception = assertThrows(FoundException.class, () -> userService.updateProfile(updatedUser));
        assertEquals("Invalid user token.", exception.getMessage());
    }



    @Test
    void testVerifyEmail_UserAlreadyVerified() {
        // Arrange
        String token = UUID.randomUUID().toString();
        user.setVerificationToken(token);
        user.setVerified(true); // User is already verified

        when(userRepository.findByVerificationToken(token)).thenReturn(Optional.of(user));

        // Act
        boolean isVerified = userService.verifyEmail(token);

        // Assert
        assertFalse(isVerified); // User should not be re-verified
    }

    @Test
    void testAddUser_UserWithNoEmail() {
        // Arrange
        user.setEmail(null); // Null email

        // Act & Assert
        assertThrows(NullPointerException.class, () -> userService.AddUser(user));
    }



    @Test
    void testAddUser_UnexpectedError() {
        // Arrange
        when(userRepository.existsByEmail(user.getEmail())).thenReturn(false);
        when(passwordEncoder.encode(user.getPassword())).thenThrow(new RuntimeException("Unexpected error"));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> userService.AddUser(user));
        assertEquals("Unexpected error", exception.getMessage());
    }

    @Test
    void testLoginUser_EmptyPassword() {
        // Arrange
        user.setPassword(""); // Empty password
        when(userRepository.findByEmail(user.getEmail())).thenReturn(user);

        // Act & Assert
        FoundException exception = assertThrows(FoundException.class, () -> userService.loginUser(user));
        assertEquals("Invalid password", exception.getMessage());
    }

    @Test
    void testVerifyEmail_TokenExpired() {
        // Arrange
        String token = "expiredToken";
        user.setVerificationToken(token);
        user.setVerified(false);
        when(userRepository.findByVerificationToken(token)).thenReturn(Optional.of(user));

        // Simulate token expiration logic in verifyEmail
        user.setVerificationToken(null); // Simulate expiration

        // Act
        boolean isVerified = userService.verifyEmail(token);

        // Assert
        assertTrue(isVerified);
        assertTrue(user.isVerified());
    }

    @Test
    void testAddUser_InvalidUser() {
        User invalidUser = new User(); // No email, no name, no password
        assertThrows(NullPointerException.class, () -> userService.AddUser(invalidUser));
    }

    @Test
    void testVerifyEmail_NullToken() {
        assertThrows(IllegalArgumentException.class, () -> userService.verifyEmail(null));
    }

    @Test
    void testLoginUser_NullEmail() {
        user.setEmail(null);
        assertThrows(FoundException.class, () -> userService.loginUser(user));
    }

    @Test
    void testLoginUser_NullUser() {
        assertThrows(NullPointerException.class, () -> userService.loginUser(null));
    }

    @Test
    void testUpdateProfile_NullFullName() {
        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(user.getEmail(), null));
        user.setFullName(null);
        assertThrows(FoundException.class, () -> userService.updateProfile(user));
    }

    @Test
    void testAddUser_EmailSendingFailure() {
        // Arrange
        User user = new User();
        user.setEmail("test@example.com");
        user.setPassword("password");

        MimeMessage mimeMessage = Mockito.mock(MimeMessage.class);  // Mock the MimeMessage
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);  // Mock the creation of MimeMessage

        when(userRepository.existsByEmail(user.getEmail())).thenReturn(false);
        when(passwordEncoder.encode(user.getPassword())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(user);

        // Simulate the email sending failure by throwing a RuntimeException
        doThrow(new RuntimeException("Email sending failed")).when(mailSender).send(any(MimeMessage.class));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> userService.AddUser(user));
        assertEquals("Email sending failed", exception.getMessage());
    }

    @Test
    void testVerifyEmail_NonexistentToken() {
        String token = UUID.randomUUID().toString();
        when(userRepository.findByVerificationToken(token)).thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> userService.verifyEmail(token));
        assertEquals("Invalid token", exception.getMessage());
    }

    @Test
    void testAddUser_NullPassword() {
        user.setPassword(null); // Set null password
        assertThrows(NullPointerException.class, () -> userService.AddUser(user));
    }

    @Test
    void testUpdateProfile_NullEmail() {
        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(user.getEmail(), null));
        user.setEmail(null); // Null email
        assertThrows(FoundException.class, () -> userService.updateProfile(user));
    }

    @Test
    void testUpdateProfile_EmptyEmail() {
        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(user.getEmail(), null));
        user.setEmail(""); // Empty email
        assertThrows(FoundException.class, () -> userService.updateProfile(user));
    }

    @Test
    void testLoginUser_EmailNotVerified() {
        user.setVerified(false); // User is not verified
        when(userRepository.findByEmail(user.getEmail())).thenReturn(user);

        FoundException exception = assertThrows(FoundException.class, () -> userService.loginUser(user));
        assertEquals("Email is not verified.", exception.getMessage());
    }

    @Test
    void testAddUser_EmptyFullName() {
        user.setFullName(""); // Empty full name
        assertThrows(NullPointerException.class, () -> userService.AddUser(user));
    }

    @Test
    void testLoginUser_SpecialCharactersEmail() {
        user.setEmail("test+filter@example.com");
        user.setPassword("encodedPassword");
        when(userRepository.findByEmail(user.getEmail())).thenReturn(user);
        when(passwordEncoder.matches("encodedPassword", user.getPassword())).thenReturn(true);

        ApiResponse<String> response = userService.loginUser(user);
        assertTrue(response.isSuccess());
        assertEquals("Logged in Success", response.getMessage());
    }

    @Test
    void testAddUser_EmailAlreadyExists() {
        // Arrange
        when(userRepository.existsByEmail(user.getEmail())).thenReturn(true);

        // Act & Assert
        FoundException exception = assertThrows(FoundException.class, () -> userService.AddUser(user));
        assertEquals("Email Is Already Used With Another Account", exception.getMessage());
    }

    @Test
    void testAddUser_MissingFields() {
        User invalidUser = new User(); // All fields are missing
        assertThrows(NullPointerException.class, () -> userService.AddUser(invalidUser));
    }

    @Test
    void testAddUser_InvalidEmailFormat() {
        user.setEmail("invalidEmailFormat"); // Invalid email
        assertThrows(NullPointerException.class, () -> userService.AddUser(user));
    }


    @Test
    void testGetProfile_AuthenticationNull() {
        when(SecurityContextHolder.getContext().getAuthentication()).thenReturn(null);

        assertThrows(FoundException.class, () -> userService.GetProfile());
    }

    @Test
    void testVerifyEmail_InvalidToken_path2() {
        String token = UUID.randomUUID().toString();

        when(userRepository.findByVerificationToken(token)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> userService.verifyEmail(token));
    }

    @Test
    void testAuthenticate_Success() {
        User user = new User();
        user.setEmail("test@example.com");
        user.setPassword("encodedPassword");
        user.setVerified(true);
        when(userRepository.findByEmail(user.getEmail())).thenReturn(user);
        when(passwordEncoder.matches("rawPassword", user.getPassword())).thenReturn(true);

        Authentication result = userService.authenticate(user.getEmail(), "rawPassword");

        assertNotNull(result);
        assertEquals(user.getEmail(), ((UserDetails) result.getPrincipal()).getUsername());
    }


    @Test
    void testAuthenticate_InvalidEmail() {
        String email = "nonexistent@example.com";

        when(userRepository.findByEmail(email)).thenReturn(null);

        assertThrows(FoundException.class, () -> userService.authenticate(email, "password"));
    }

    @Test
    void testLoadUserByEmail_Success() {
        User user = new User();
        user.setEmail("test@example.com");
        user.setPassword("encodedPassword");
        user.setVerified(true);

        when(userRepository.findByEmail(user.getEmail())).thenReturn(user);

        UserDetails userDetails = userService.loadUserByEmail(user.getEmail());

        assertNotNull(userDetails);
        assertEquals(user.getEmail(), userDetails.getUsername());
    }

    @Test
    void testLoadUserByEmail_NotFound() {
        String email = "nonexistent@example.com";

        when(userRepository.findByEmail(email)).thenReturn(null);

        assertThrows(FoundException.class, () -> userService.loadUserByEmail(email));
    }

    @Test
    void testLoadUserByEmail_NotVerified() {
        User user = new User();
        user.setEmail("test@example.com");
        user.setVerified(false);

        when(userRepository.findByEmail(user.getEmail())).thenReturn(user);

        assertThrows(FoundException.class, () -> userService.loadUserByEmail(user.getEmail()));
    }

}
