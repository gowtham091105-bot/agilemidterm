package com.example.registration.controller;

import com.example.registration.dto.UserDto;
import com.example.registration.model.User;
import com.example.registration.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class RegistrationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    public void setup() {
        userRepository.deleteAll();
    }

    @Test
    public void testSuccessfulRegistration() throws Exception {
        UserDto validUser = new UserDto("testuser", "test@example.com", "password123");

        mockMvc.perform(post("/api/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validUser)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("User registered successfully!"));
    }

    @Test
    public void testRegistrationWithBlankUsername() throws Exception {
        UserDto invalidUser = new UserDto("", "test@example.com", "password123");

        mockMvc.perform(post("/api/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidUser)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.username").exists());
    }

    @Test
    public void testRegistrationWithInvalidEmail() throws Exception {
        UserDto invalidUser = new UserDto("testuser", "invalidemail", "password123");

        mockMvc.perform(post("/api/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidUser)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.email").value("Invalid email format"));
    }

    @Test
    public void testRegistrationWithShortPassword() throws Exception {
        UserDto invalidUser = new UserDto("testuser", "test@example.com", "short");

        mockMvc.perform(post("/api/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidUser)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.password").value("Password must be at least 8 characters long"));
    }

    @Test
    public void testRegistrationWithDuplicateUsername() throws Exception {
        User existingUser = new User("testuser", "existing@example.com", "password123");
        userRepository.save(existingUser);

        UserDto duplicateUser = new UserDto("testuser", "test@example.com", "password123");

        mockMvc.perform(post("/api/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(duplicateUser)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Error: Username is already taken!"));
    }

    @Test
    public void testRegistrationWithDuplicateEmail() throws Exception {
        User existingUser = new User("existinguser", "test@example.com", "password123");
        userRepository.save(existingUser);

        UserDto duplicateUser = new UserDto("testuser", "test@example.com", "password123");

        mockMvc.perform(post("/api/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(duplicateUser)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Error: Email is already in use!"));
    }
}
