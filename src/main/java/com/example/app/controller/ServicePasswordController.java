package com.example.app.controller;

import com.example.app.exception.ResourceNotFoundException;
import com.example.app.model.ServicePassword;
import com.example.app.repository.ServicePasswordRepository;
import com.example.app.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/pass-manager")
public class ServicePasswordController {

    @Autowired
    private ServicePasswordRepository servicePasswordRepository;

    @Autowired
    private UserService userService;

    @PostMapping("/add")
    public ServicePassword addPassword(@RequestBody Map<String, String> request) {
        String username = request.get("username");
        String password = request.get("password");
        String serviceName = request.get("serviceName");
        String servicePassword = request.get("servicePassword");

        if (!userService.validateUser(username, password)) {
            throw new SecurityException("Invalid username or password");
        }

        ServicePassword servicePasswordEntity = new ServicePassword();
        servicePasswordEntity.setServiceName(serviceName);
        servicePasswordEntity.setPassword(servicePassword);
        servicePasswordEntity.setUsername(username);

        return servicePasswordRepository.save(servicePasswordEntity);
    }

    @GetMapping("/{username}")
    public List<ServicePassword> getPasswords(@PathVariable String username, 
                                              @RequestParam String password) {

        if (!userService.validateUser(username, password)) {
            throw new SecurityException("Invalid username or password");
        }
        return servicePasswordRepository.findByUsername(username);
    }

    @PutMapping("/update/{serviceName}")
    public ServicePassword updatePassword(@PathVariable String serviceName,
                                          @RequestBody Map<String, String> requestParams) {
        String username = requestParams.get("username");
        String password = requestParams.get("password");

        if (!userService.validateUser(username, password)) {
            throw new SecurityException("Invalid username or password");
        }

        ServicePassword existingPassword = servicePasswordRepository.findByUsernameAndServiceName(username, serviceName)
                .orElseThrow(() -> new ResourceNotFoundException("ServicePassword not found for user :: " + username + " and service :: " + serviceName));

        String newServicePassword = requestParams.get("servicePassword");

        if (newServicePassword != null) {
            existingPassword.setPassword(newServicePassword);
        }

        return servicePasswordRepository.save(existingPassword);
    }

    @DeleteMapping("/delete/{serviceName}")
    public void deletePassword(@PathVariable String serviceName, 
                               @RequestBody Map<String, String> requestParams) {
        String username = requestParams.get("username");
        String password = requestParams.get("password");

        if (!userService.validateUser(username, password)) {
            throw new SecurityException("Invalid username or password");
        }
        
        if (servicePasswordRepository.findByUsernameAndServiceName(username, serviceName) == null) {
            throw new ResourceNotFoundException("ServicePassword not found for this user :: " + username);
        }
        servicePasswordRepository.deleteByUsernameAndServiceName(username, serviceName);
    }
}
