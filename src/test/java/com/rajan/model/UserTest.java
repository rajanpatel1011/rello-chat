package com.rajan.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class UserTest {

    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
    }

    @Test
    void testCreateEmptyUser() {
        assertNull(user.getId());
        assertNull(user.getUsername());
        assertNull(user.getPassword());
    }

    @Test
    void testCreateUserWithConstructor() {
        Set<String> roles = Set.of("USER", "ADMIN");
        User testUser = new User("testuser", "password123", roles);

        assertEquals("testuser", testUser.getUsername());
        assertEquals("password123", testUser.getPassword());
        assertEquals(roles, testUser.getRoles());
    }

    @Test
    void testSetAndGetId() {
        user.setId(1L);
        assertEquals(1L, user.getId());
    }

    @Test
    void testSetAndGetUsername() {
        user.setUsername("testuser");
        assertEquals("testuser", user.getUsername());
    }

    @Test
    void testSetAndGetPassword() {
        user.setPassword("hashedpassword");
        assertEquals("hashedpassword", user.getPassword());
    }

    @Test
    void testSetAndGetRoles() {
        Set<String> roles = Set.of("USER");
        user.setRoles(roles);
        assertEquals(roles, user.getRoles());
    }

    @Test
    void testUserWithMultipleRoles() {
        Set<String> roles = Set.of("ADMIN", "USER", "MODERATOR");
        user.setRoles(roles);
        
        assertEquals(3, user.getRoles().size());
        assertTrue(user.getRoles().contains("ADMIN"));
        assertTrue(user.getRoles().contains("USER"));
        assertTrue(user.getRoles().contains("MODERATOR"));
    }

    @Test
    void testSetAllUserFields() {
        Set<String> roles = Set.of("USER");
        user.setId(1L);
        user.setUsername("john");
        user.setPassword("password");
        user.setRoles(roles);

        assertEquals(1L, user.getId());
        assertEquals("john", user.getUsername());
        assertEquals("password", user.getPassword());
        assertEquals(roles, user.getRoles());
    }
}
