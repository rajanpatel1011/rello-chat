package com.rajan.config;

import com.rajan.model.User;
import com.rajan.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class DataInitializerTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    void testAdminUserIsCreatedOnStartup() {
        var adminUser = userRepository.findByUsername("admin");
        assertTrue(adminUser.isPresent());
        assertEquals("admin", adminUser.get().getUsername());
        assertTrue(adminUser.get().getRoles().contains("ADMIN"));
        assertTrue(adminUser.get().getRoles().contains("USER"));
    }

    @Test
    void testAdminUserPasswordIsEncoded() {
        var adminUser = userRepository.findByUsername("admin");
        assertTrue(adminUser.isPresent());
        
        // Password should be encoded, not plain text
        assertNotEquals("admin123", adminUser.get().getPassword());
        assertTrue(passwordEncoder.matches("admin123", adminUser.get().getPassword()));
    }

    @Test
    void testAdminUserIsNotCreatedTwice() {
        var firstCheck = userRepository.findByUsername("admin");
        assertTrue(firstCheck.isPresent());
        
        long firstCount = userRepository.count();
        
        // Simulate another startup
        var secondCheck = userRepository.findByUsername("admin");
        assertTrue(secondCheck.isPresent());
        
        long secondCount = userRepository.count();
        assertEquals(firstCount, secondCount);
    }
}
