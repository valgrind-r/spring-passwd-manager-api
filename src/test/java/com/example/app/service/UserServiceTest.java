package com.example.app.service;

import com.example.app.exception.ResourceNotFoundException;
import com.example.app.model.User;
import com.example.app.repository.UserRepository;
import com.example.app.util.PasswordHasher;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class UserServiceTest {

    @InjectMocks
    private UserService userService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordHasher passwordHasher;

    private User user;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        user = new User();
        user.setUsername("testUser");
        user.setPassword("hashedPassword");
    }

    @Test
    void registerUser_ShouldSaveUser() {
        when(passwordHasher.hashPassword("password")).thenReturn("hashedPassword");
        when(userRepository.save(any(User.class))).thenReturn(user);

        User result = userService.registerUser(user);

        assertNotNull(result);
        assertEquals(user.getUsername(), result.getUsername());
        verify(userRepository).save(user);
        System.out.println("registerUser_ShouldSaveUser passed successfully.");
    }

    @Test
    void getUserByUsername_ValidUser_ShouldReturnUser() {
        String password = "testPassword";
        when(userRepository.findByUsername("testUser")).thenReturn(user);
        when(passwordHasher.validatePassword("testUser", password, user.getPassword())).thenReturn(true);

        Optional<User> result = userService.getUserByUsername("testUser", password);

        assertTrue(result.isPresent());
        assertEquals(user.getUsername(), result.get().getUsername());
        System.out.println("getUserByUsername_ValidUser_ShouldReturnUser passed successfully.");
    }

    // Corner Case: Register User with Existing Username
    @Test
    void registerUser_ExistingUsername_ShouldThrowException() {
        when(userRepository.findByUsername("testUser")).thenReturn(user);

        Exception exception = assertThrows(SecurityException.class, () -> {
            userService.registerUser(user);
        });

        assertEquals("Username already exists", exception.getMessage());
        System.out.println("registerUser_ExistingUsername_ShouldThrowException passed successfully.");
    }

    // Corner Case: Missing username arg
    @Test
    void registerUser_MissingUsername_ShouldThrowException() {
        User userWithNoUsername = new User();
        userWithNoUsername.setPassword("password");

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            userService.registerUser(userWithNoUsername);
        });

        assertEquals("Username cannot be null or empty", exception.getMessage());
    }

    // Corner Case: Missing passwd arg
    @Test
    void registerUser_MissingPassword_ShouldThrowException() {
        User userWithNoPassword = new User();
        userWithNoPassword.setUsername("testUser");

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            userService.registerUser(userWithNoPassword);
        });

        assertEquals("Password cannot be null or empty", exception.getMessage());
    }

    // Corner Case: Get User by Non-Existing Username
    @Test
    void getUserByUsername_NonExistingUser_ShouldReturnEmpty() {
        String password = "password";
        when(userRepository.findByUsername("nonExistingUser")).thenReturn(null);

        Optional<User> result = userService.getUserByUsername("nonExistingUser", password);

        assertFalse(result.isPresent());
        System.out.println("getUserByUsername_NonExistingUser_ShouldReturnEmpty passed successfully.");
    }

    // Corner Case: Get User with Incorrect Password
    @Test
    void getUserByUsername_IncorrectPassword_ShouldThrowException() {
        when(userRepository.findByUsername("testUser")).thenReturn(user);
        when(passwordHasher.validatePassword("testUser", "wrongPassword", user.getPassword())).thenReturn(false);

        Exception exception = assertThrows(SecurityException.class, () -> {
            userService.getUserByUsername("testUser", "wrongPassword");
        });

        assertEquals("Invalid username or password", exception.getMessage());
        System.out.println("getUserByUsername_IncorrectPassword_ShouldThrowException passed successfully.");
    }

    // Corner Case: Update User with Non-Existing Username
    @Test
    void updateUser_NonExistingUser_ShouldThrowException() {
        when(userRepository.findByUsername("nonExistingUser")).thenReturn(null);

        Exception exception = assertThrows(ResourceNotFoundException.class, () -> {
            userService.updateUser("nonExistingUser", "oldPassword", user);
        });

        assertEquals("User not found", exception.getMessage());
        System.out.println("updateUser_NonExistingUser_ShouldThrowException passed successfully.");
    }

    // Corner Case: Delete User with Non-Existing Username
    @Test
    void deleteUser_NonExistingUser_ShouldThrowException() {
        when(userRepository.findByUsername("nonExistingUser")).thenReturn(null);

        Exception exception = assertThrows(ResourceNotFoundException.class, () -> {
            userService.deleteUser("nonExistingUser", "password");
        });

        assertEquals("User not found", exception.getMessage());
        System.out.println("deleteUser_NonExistingUser_ShouldThrowException passed successfully.");
    }
}
