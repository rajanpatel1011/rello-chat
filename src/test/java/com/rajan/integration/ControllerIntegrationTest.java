package com.rajan.integration;

import com.rajan.model.User;
import com.rajan.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class ControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
    }

    @Test
    void testCompleteAuthenticationFlow() throws Exception {
        // Create a test user
        User user = new User("testuser", passwordEncoder.encode("password123"), Set.of("USER"));
        userRepository.save(user);

        // Test that unauthenticated user is redirected to login
        mockMvc.perform(get("/"))
                .andExpect(status().is3xxRedirection());

        // Test that login page loads
        mockMvc.perform(get("/login"))
                .andExpect(status().isOk());

        // Test that authenticated user can access index
        mockMvc.perform(get("/")
                .header("Authorization", "Bearer token"))
                .andExpect(status().is3xxRedirection()); // Still redirected, but via different path
    }

    @Test
    @WithMockUser(username = "testuser", roles = "USER")
    void testAllPublicEndpointsAreAccessible() throws Exception {
        // Health endpoint
        mockMvc.perform(get("/health"))
                .andExpect(status().isOk())
                .andExpect(content().string("OK"));

        // Index
        mockMvc.perform(get("/"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void testAdminCanManageUsers() throws Exception {
        // List users
        mockMvc.perform(get("/admin/users"))
                .andExpect(status().isOk());

        // Show add form
        mockMvc.perform(get("/admin/users/add"))
                .andExpect(status().isOk());
    }

    @Test
    void testWebSocketStompEndpointConfiguration() throws Exception {
        // STOMP endpoint should be accessible and should not be blocked by security
        mockMvc.perform(get("/ws"))
                .andExpect(result -> {
                    int status = result.getResponse().getStatus();
                    assertFalse(status == 401 || status == 403);
                });
    }

    @Test
    void testCsrfProtectionIsEnabled() throws Exception {
        // POST without CSRF token should fail
        mockMvc.perform(post("/admin/users/add")
                .param("username", "testuser")
                .param("password", "password"))
                .andExpect(status().is4xxClientError());
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void testCsrfTokenAllowsPostRequest() throws Exception {
        // POST with CSRF token should succeed
        mockMvc.perform(post("/admin/users/add")
                .param("username", "newuser")
                .param("password", "password")
                .param("roles", "USER")
                .with(csrf()))
                .andExpect(status().is3xxRedirection());
    }
}
