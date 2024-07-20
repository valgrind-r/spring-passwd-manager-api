package com.example.app.service;

import com.example.app.exception.ResourceNotFoundException;
import com.example.app.model.User;
import com.example.app.repository.UserRepository;
import com.example.app.util.PasswordHasher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordHasher passwordHasher;

    public User registerUser(User user) {
        String hashedPassword = passwordHasher.hashPassword(user.getPassword());
        user.setPassword(hashedPassword);
        return userRepository.save(user);
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public Optional<User> getUserByUsername(String username, String password) {
        User existingUser = userRepository.findByUsername(username);
        if (existingUser == null) {
            return Optional.empty();
        }

        // Use instance method for password validation
        if (!passwordHasher.validatePassword(username, password, existingUser.getPassword())) {
            throw new SecurityException("Invalid username or password");
        }

        return Optional.of(existingUser);
    }

    public User updateUser(String username, String oldPassword, User user) {
        User existingUser = userRepository.findByUsername(username);
        if (existingUser == null) {
            throw new ResourceNotFoundException("User not found");
        }

        // Use instance method for old password validation
        if (!passwordHasher.validatePassword(username, oldPassword, existingUser.getPassword())) {
            throw new SecurityException("Old password is incorrect");
        }

        existingUser.setUsername(user.getUsername());
        existingUser.setPassword(passwordHasher.hashPassword(user.getPassword()));
        return userRepository.save(existingUser);
    }

    public void deleteUser(String username, String password) {
        User existingUser = userRepository.findByUsername(username);
        if (existingUser == null) {
            throw new ResourceNotFoundException("User not found");
        }

        // Use instance method for password validation
        if (!passwordHasher.validatePassword(username, password, existingUser.getPassword())) {
            throw new SecurityException("Invalid username or password");
        }

        userRepository.delete(existingUser);
    }

    public boolean validateUser(String username, String password) {
        User user = userRepository.findByUsername(username);
        if (user == null) {
            return false;
        }

        // Use instance method for password validation
        return passwordHasher.validatePassword(username, password, user.getPassword());
    }
}
