package com.example.app.controller;

import com.example.app.model.User;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class UserControllerTest {

    @InjectMocks
    private UserController userController;

    @Mock
    private UserService userService;

    private MockMvc mockMvc;
    private User user;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(userController).build();

        user = new User();
        user.setUsername("testuser");
        user.setPassword("testpassword");
    }

    @Test
    public void testRegister_Success() throws Exception {
        when(userService.registerUser(any(User.class))).thenReturn(user);

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(user)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("testuser"))
                .andExpect(jsonPath("$.password").value("testpassword"));

        verify(userService).registerUser(any(User.class));
    }

    @Test
    public void testGetAllUsers_Success() throws Exception {
        List<User> userList = new ArrayList<>();
        userList.add(user);

        when(userService.getAllUsers()).thenReturn(userList);

        mockMvc.perform(get("/api/auth/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].username").value("testuser"));

        verify(userService).getAllUsers();
    }

    @Test
    public void testGetUserByUsername_Success() throws Exception {
        when(userService.getUserByUsername("testuser", "password")).thenReturn(Optional.of(user));

        mockMvc.perform(post("/api/auth/get-user/testuser")
                        .param("password", "password"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("testuser"));

        verify(userService).getUserByUsername("testuser", "password");
    }

    @Test
    public void testUpdateUser_Success() throws Exception {
        User updatedUser = new User();
        updatedUser.setUsername("testuser");
        updatedUser.setPassword("newpassword");

        when(userService.updateUser(eq("testuser"), eq("oldpassword"), any(User.class))).thenReturn(updatedUser);

        mockMvc.perform(post("/api/auth/update-user/testuser")
                        .param("oldPassword", "oldpassword")
                        .param("newPassword", "newpassword"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("testuser"))
                .andExpect(jsonPath("$.password").value("newpassword"));

        verify(userService).updateUser(eq("testuser"), eq("oldpassword"), any(User.class));
    }

    @Test
    public void testDeleteUser_Success() throws Exception {
        doNothing().when(userService).deleteUser("testuser", "password");

        mockMvc.perform(delete("/api/auth/delete-user/testuser")
                        .param("password", "password"))
                .andExpect(status().isOk());

        verify(userService).deleteUser("testuser", "password");
    }
}
