package com.example.app.service;

import com.example.app.exception.ResourceNotFoundException;
import com.example.app.model.ServicePassword;
import com.example.app.model.User;
import com.example.app.repository.ServicePasswordRepository;
import com.example.app.repository.UserRepository;
import com.example.app.util.PasswordHasher;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PasswordServiceTest {

    @InjectMocks
    private PasswordService passwordService;

    @Mock
    private ServicePasswordRepository servicePasswordRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordHasher passwordHasher;

    private User user;
    private ServicePassword servicePassword;

    @BeforeEach
    public void setUp() {
        user = new User();
        user.setUsername("testuser");
        user.setPassword("hashedpassword");

        servicePassword = new ServicePassword();
        servicePassword.setId(1L);
        servicePassword.setUsername("testuser");
        servicePassword.setServiceName("testservice");
        servicePassword.setPassword("testpassword");
    }

    @Test
    public void testAddPassword_ValidUser() {
        when(userRepository.findByUsername("testuser")).thenReturn(user);
        when(passwordHasher.validatePassword(anyString(), anyString(), anyString())).thenReturn(true);
        when(servicePasswordRepository.save(any(ServicePassword.class))).thenReturn(servicePassword);

        ServicePassword result = passwordService.addPassword("testuser", "password", servicePassword);

        assertThat(result).isNotNull();
        assertThat(result.getServiceName()).isEqualTo("testservice");
        verify(servicePasswordRepository).save(any(ServicePassword.class));
    }

    @Test
    public void testGetPasswords_ValidUser() {
        when(userRepository.findByUsername("testuser")).thenReturn(user);
        when(passwordHasher.validatePassword(anyString(), anyString(), anyString())).thenReturn(true);
        when(servicePasswordRepository.findByUsername("testuser")).thenReturn(Arrays.asList(servicePassword));

        List<ServicePassword> result = passwordService.getPasswords("testuser", "password");

        assertThat(result).isNotEmpty();
        assertThat(result.get(0).getServiceName()).isEqualTo("testservice");
    }

    @Test
    public void testUpdatePassword_ValidUser() {
        ServicePassword updatedPassword = new ServicePassword();
        updatedPassword.setServiceName("updatedService");
        updatedPassword.setPassword("newPassword");

        when(userRepository.findByUsername("testuser")).thenReturn(user);
        when(passwordHasher.validatePassword(anyString(), anyString(), anyString())).thenReturn(true);
        when(servicePasswordRepository.findById(1L)).thenReturn(Optional.of(servicePassword));
        when(servicePasswordRepository.save(any(ServicePassword.class))).thenReturn(servicePassword);

        ServicePassword result = passwordService.updatePassword("testuser", "password", 1L, updatedPassword);

        assertThat(result.getServiceName()).isEqualTo("updatedService");
        assertThat(result.getPassword()).isEqualTo("newPassword");
    }

    @Test
    public void testUpdatePassword_ServicePasswordNotFound() {
        when(userRepository.findByUsername("testuser")).thenReturn(user);
        when(passwordHasher.validatePassword(anyString(), anyString(), anyString())).thenReturn(true);
        when(servicePasswordRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            passwordService.updatePassword("testuser", "password", 1L, servicePassword);
        });
    }

    @Test
    public void testDeletePassword_ValidUser() {
        when(userRepository.findByUsername("testuser")).thenReturn(user);
        when(passwordHasher.validatePassword(anyString(), anyString(), anyString())).thenReturn(true);
        when(servicePasswordRepository.findByUsernameAndServiceName("testuser", "testservice")).thenReturn(Optional.of(servicePassword));

        passwordService.deletePassword("testuser", "password", "testservice");

        verify(servicePasswordRepository).deleteByUsernameAndServiceName("testuser", "testservice");
    }

    @Test
    public void testDeletePassword_UserNotFound() {
        when(passwordHasher.validatePassword(anyString(), anyString(), anyString())).thenReturn(true);
        when(userRepository.findByUsername("testuser")).thenReturn(user);
        when(servicePasswordRepository.findByUsernameAndServiceName("testuser", "testservice")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            passwordService.deletePassword("testuser", "password", "testservice");
        });
    }
}
