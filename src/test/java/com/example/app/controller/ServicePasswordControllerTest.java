package com.example.app.controller;

import com.example.app.exception.ResourceNotFoundException;
import com.example.app.model.ServicePassword;
import com.example.app.repository.ServicePasswordRepository;
import com.example.app.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.util.NestedServletException;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class ServicePasswordControllerTest {

    @InjectMocks
    private ServicePasswordController servicePasswordController;

    @Mock
    private ServicePasswordRepository servicePasswordRepository;

    @Mock
    private UserService userService;

    private MockMvc mockMvc;
    private ServicePassword servicePassword;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(servicePasswordController).build();

        servicePassword = new ServicePassword();
        servicePassword.setUsername("testuser");
        servicePassword.setServiceName("testservice");
        servicePassword.setPassword("testpassword");
    }

    @Test
    public void testAddPassword_Success() throws Exception {
        Map<String, String> request = new HashMap<>();
        request.put("username", "testuser");
        request.put("password", "password");
        request.put("serviceName", "testservice");
        request.put("servicePassword", "testpassword");

        when(userService.validateUser("testuser", "password")).thenReturn(true);
        when(servicePasswordRepository.save(any(ServicePassword.class))).thenReturn(servicePassword);

        mockMvc.perform(post("/api/pass-manager/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("testuser"))
                .andExpect(jsonPath("$.serviceName").value("testservice"))
                .andExpect(jsonPath("$.password").value("testpassword"));

        verify(servicePasswordRepository).save(any(ServicePassword.class));
    }

    @Test
    public void testAddPassword_InvalidUser() throws Exception {
        Map<String, String> request = new HashMap<>();
        request.put("username", "testuser");
        request.put("password", "wrongpassword");
        request.put("serviceName", "testservice");
        request.put("servicePassword", "testpassword");

        when(userService.validateUser("testuser", "wrongpassword")).thenReturn(false);

        Exception exception = assertThrows(NestedServletException.class, () -> {
            mockMvc.perform(post("/api/pass-manager/add")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(new ObjectMapper().writeValueAsString(request)))
                    .andReturn();
        });

        assertTrue(exception.getCause() instanceof SecurityException);
        assertEquals("Invalid username or password", exception.getCause().getMessage());

        verify(servicePasswordRepository, never()).save(any(ServicePassword.class));
    }

    @Test
    public void testGetPasswords_Success() throws Exception {
        when(userService.validateUser("testuser", "password")).thenReturn(true);
        when(servicePasswordRepository.findByUsername("testuser")).thenReturn(Collections.singletonList(servicePassword));

        mockMvc.perform(get("/api/pass-manager/testuser")
                        .param("password", "password"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].serviceName").value("testservice"));

        verify(servicePasswordRepository).findByUsername("testuser");
    }

    @Test
    public void testGetPasswords_InvalidUser() throws Exception {
        when(userService.validateUser("testuser", "wrongpassword")).thenReturn(false);

        Exception exception = assertThrows(NestedServletException.class, () -> {
            mockMvc.perform(get("/api/pass-manager/testuser")
                            .param("password", "wrongpassword"))
                    .andReturn();
        });

        assertTrue(exception.getCause() instanceof SecurityException);
        assertEquals("Invalid username or password", exception.getCause().getMessage());
    }

    @Test
    public void testUpdatePassword_Success() throws Exception {
        Map<String, String> requestParams = new HashMap<>();
        requestParams.put("username", "testuser");
        requestParams.put("password", "password");
        requestParams.put("servicePassword", "newpassword");

        when(userService.validateUser("testuser", "password")).thenReturn(true);
        when(servicePasswordRepository.findByUsernameAndServiceName("testuser", "testservice"))
                .thenReturn(Optional.of(servicePassword));
        when(servicePasswordRepository.save(any(ServicePassword.class))).thenReturn(servicePassword);

        mockMvc.perform(put("/api/pass-manager/update/testservice")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(requestParams)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.password").value("newpassword"));

        verify(servicePasswordRepository).save(any(ServicePassword.class));
    }

    @Test
    public void testDeletePassword_Success() throws Exception {
        Map<String, String> requestParams = new HashMap<>();
        requestParams.put("username", "testuser");
        requestParams.put("password", "password");

        when(userService.validateUser("testuser", "password")).thenReturn(true);
        when(servicePasswordRepository.findByUsernameAndServiceName("testuser", "testservice"))
                .thenReturn(Optional.of(servicePassword));

        mockMvc.perform(delete("/api/pass-manager/delete/testservice")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(requestParams)))
                .andExpect(status().isOk());

        verify(servicePasswordRepository).deleteByUsernameAndServiceName("testuser", "testservice");
    }
}
