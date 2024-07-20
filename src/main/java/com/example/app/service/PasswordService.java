package com.example.app.service;

import org.springframework.transaction.annotation.Transactional;
import com.example.app.exception.ResourceNotFoundException;
import com.example.app.model.ServicePassword;
import com.example.app.model.User;
import com.example.app.repository.ServicePasswordRepository;
import com.example.app.repository.UserRepository;
import com.example.app.util.PasswordHasher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PasswordService {

    @Autowired
    private ServicePasswordRepository servicePasswordRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordHasher passwordHasher; // Injecting the instance

    public ServicePassword addPassword(String username, String password, ServicePassword servicePassword) {
        if (!validateUser(username, password)) {
            throw new SecurityException("Invalid username or password");
        }

        User user = userRepository.findByUsername(username);
        servicePassword.setUsername(user.getUsername());
        return servicePasswordRepository.save(servicePassword);
    }

    public List<ServicePassword> getPasswords(String username, String password) {
        if (!validateUser(username, password)) {
            throw new SecurityException("Invalid username or password");
        }

        return servicePasswordRepository.findByUsername(username);
    }

    public ServicePassword updatePassword(String username, String password, Long id, ServicePassword updatedPassword) {
        if (!validateUser(username, password)) {
            throw new SecurityException("Invalid username or password");
        }

        ServicePassword existingPassword = servicePasswordRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("ServicePassword not found for this id :: " + id));

        if (updatedPassword.getServiceName() != null) {
            existingPassword.setServiceName(updatedPassword.getServiceName());
        }
        if (updatedPassword.getPassword() != null) {
            existingPassword.setPassword(updatedPassword.getPassword());
        }

        return servicePasswordRepository.save(existingPassword);
    }

    @Transactional
    public void deletePassword(String username, String password, String serviceName) {
        if (!validateUser(username, password)) {
            throw new SecurityException("Invalid username or password");
        }

        if (servicePasswordRepository.findByUsernameAndServiceName(username, serviceName) == null) {
            throw new ResourceNotFoundException("ServicePassword not found for user: " + username + " and service: " + serviceName);
        }

        servicePasswordRepository.deleteByUsernameAndServiceName(username, serviceName);
    }

    private boolean validateUser(String username, String password) {
        User user = userRepository.findByUsername(username);
        if (user == null) {
            return false;
        }
        // Use the instance method to validate the password
        return passwordHasher.validatePassword(username, password, user.getPassword());
    }
}
