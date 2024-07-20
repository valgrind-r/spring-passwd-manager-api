package com.example.app.repository;

import com.example.app.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.when;

public class UserRepositoryTest {

    @Mock
    private UserRepository userRepository;

    private User user;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        user = new User();
        user.setId(1L);
        user.setUsername("testuser");
        user.setPassword("testpassword");
    }

    @Test
    public void testFindByUsername_UserFound() {
        when(userRepository.findByUsername("testuser")).thenReturn(user);

        User result = userRepository.findByUsername("testuser");

        assertEquals("testuser", result.getUsername());
        assertEquals("testpassword", result.getPassword());
    }

    @Test
    public void testFindByUsername_UserNotFound() {
        when(userRepository.findByUsername("unknownuser")).thenReturn(null);

        User result = userRepository.findByUsername("unknownuser");

        assertNull(result);
    }
}
