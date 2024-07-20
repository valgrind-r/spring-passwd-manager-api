package com.example.app.controller;

import com.example.app.model.User;
import com.example.app.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public User register(@RequestBody User user) {
        return userService.registerUser(user);
    }

    @GetMapping("/users")
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }

    @PostMapping("/get-user/{username}")
    public Optional<User> getUserByUsername(@PathVariable String username, @RequestParam String password) {
        return userService.getUserByUsername(username, password);
    }

    @PostMapping("/update-user/{username}")
    public User updateUser(
            @PathVariable String username, 
            @RequestParam String oldPassword, 
            @RequestParam String newPassword) {
        
        User updatedUser = new User();
        updatedUser.setUsername(username);
        updatedUser.setPassword(newPassword);

        return userService.updateUser(username, oldPassword, updatedUser);
    }

    @DeleteMapping("/delete-user/{username}")
    public void deleteUser(@PathVariable String username, @RequestParam String password) {
        userService.deleteUser(username, password);
    }
}
