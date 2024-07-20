package com.example.app.repository;

import com.example.app.model.ServicePassword;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

public class ServicePasswordRepositoryTest {

    @Mock
    private ServicePasswordRepository servicePasswordRepository;

    @InjectMocks
    private ServicePassword servicePassword;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testFindByUsername() {
        List<ServicePassword> mockPasswords = new ArrayList<>();
        ServicePassword servicePassword1 = new ServicePassword();
        servicePassword1.setUsername("testuser");
        servicePassword1.setServiceName("testservice");
        servicePassword1.setPassword("testpassword");

        mockPasswords.add(servicePassword1);

        when(servicePasswordRepository.findByUsername("testuser")).thenReturn(mockPasswords);

        List<ServicePassword> result = servicePasswordRepository.findByUsername("testuser");

        assertEquals(1, result.size());
        assertEquals("testservice", result.get(0).getServiceName());
        verify(servicePasswordRepository).findByUsername("testuser");
    }

    @Test
    public void testFindByUsernameAndServiceName() {
        ServicePassword mockPassword = new ServicePassword();
        mockPassword.setUsername("testuser");
        mockPassword.setServiceName("testservice");
        mockPassword.setPassword("testpassword");

        when(servicePasswordRepository.findByUsernameAndServiceName("testuser", "testservice"))
                .thenReturn(Optional.of(mockPassword));

        Optional<ServicePassword> result = servicePasswordRepository.findByUsernameAndServiceName("testuser", "testservice");

        assertTrue(result.isPresent());
        assertEquals("testpassword", result.get().getPassword());
        verify(servicePasswordRepository).findByUsernameAndServiceName("testuser", "testservice");
    }

    @Test
    public void testDeleteByUsernameAndServiceName() {
        servicePasswordRepository.deleteByUsernameAndServiceName("testuser", "testservice");
        verify(servicePasswordRepository).deleteByUsernameAndServiceName("testuser", "testservice");
    }

    @Test
    public void testFindByUsername_NoResults() {
        when(servicePasswordRepository.findByUsername("unknownuser")).thenReturn(new ArrayList<>());

        List<ServicePassword> result = servicePasswordRepository.findByUsername("unknownuser");

        assertTrue(result.isEmpty());
        verify(servicePasswordRepository).findByUsername("unknownuser");
    }
}
