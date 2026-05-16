package com.rajan.service;

import com.rajan.model.User;
import com.rajan.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CustomUserDetailsServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CustomUserDetailsService customUserDetailsService;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testuser");
        testUser.setPassword("password123");
        testUser.setRoles(Set.of("USER"));
    }

    @Test
    void testLoadUserByUsernameReturnsUserDetails() {
        when(userRepository.findByUsername("testuser"))
                .thenReturn(Optional.of(testUser));

        UserDetails userDetails = customUserDetailsService.loadUserByUsername("testuser");

        assertNotNull(userDetails);
        assertEquals("testuser", userDetails.getUsername());
        assertEquals("password123", userDetails.getPassword());
    }

    @Test
    void testLoadUserByUsernameThrowsExceptionWhenUserNotFound() {
        when(userRepository.findByUsername("nonexistent"))
                .thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () -> 
                customUserDetailsService.loadUserByUsername("nonexistent")
        );
    }

    @Test
    void testLoadUserByUsernameWithAdminRole() {
        User adminUser = new User();
        adminUser.setUsername("admin");
        adminUser.setPassword("admin123");
        adminUser.setRoles(Set.of("ADMIN", "USER"));

        when(userRepository.findByUsername("admin"))
                .thenReturn(Optional.of(adminUser));

        UserDetails userDetails = customUserDetailsService.loadUserByUsername("admin");

        assertNotNull(userDetails);
        assertEquals(2, userDetails.getAuthorities().size());
    }

    @Test
    void testLoadUserByUsernamePreservesUserRoles() {
        User userWithRoles = new User();
        userWithRoles.setUsername("user");
        userWithRoles.setPassword("pass");
        userWithRoles.setRoles(Set.of("USER", "MODERATOR"));

        when(userRepository.findByUsername("user"))
                .thenReturn(Optional.of(userWithRoles));

        UserDetails userDetails = customUserDetailsService.loadUserByUsername("user");

        assertNotNull(userDetails);
        assertTrue(userDetails.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_USER")));
        assertTrue(userDetails.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_MODERATOR")));
    }
}
