package com.rajan.controller;

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

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class UserManagementControllerTest {

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
    @WithMockUser(username = "admin", roles = "ADMIN")
    void testListUsersWithAdminRole() throws Exception {
        User user = new User("testuser", "password", Set.of("USER"));
        userRepository.save(user);

        mockMvc.perform(get("/admin/users"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/users/list"));
    }

    @Test
    @WithMockUser(username = "user", roles = "USER")
    void testListUsersWithoutAdminRole() throws Exception {
        mockMvc.perform(get("/admin/users"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void testShowAddFormWithAdminRole() throws Exception {
        mockMvc.perform(get("/admin/users/add"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/users/add"))
                .andExpect(model().attributeExists("user"));
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void testAddUserWithAdminRole() throws Exception {
        mockMvc.perform(post("/admin/users/add")
                .param("username", "newuser")
                .param("password", "password123")
                .param("roles", "USER")
                .with(csrf()))
                .andExpect(status().is3xxRedirection());

        var savedUser = userRepository.findByUsername("newuser");
        assert savedUser.isPresent();
    }

    @Test
    @WithMockUser(username = "user", roles = "USER")
    void testAddUserWithoutAdminRole() throws Exception {
        mockMvc.perform(post("/admin/users/add")
                .param("username", "newuser")
                .param("password", "password123")
                .param("roles", "USER")
                .with(csrf()))
                .andExpect(status().isForbidden());
    }

    @Test
    void testUserManagementEndpointRequiresAuthentication() throws Exception {
        mockMvc.perform(get("/admin/users"))
                .andExpect(status().is3xxRedirection());
    }
}
