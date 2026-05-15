package com.rajan.integration;

import com.rajan.model.User;
import com.rajan.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
class DatabaseIntegrationTest {

    @Autowired
    private TestEntityManager testEntityManager;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
    }

    @Test
    void testSaveAndRetrieveUser() {
        User user = new User("dbtest", "password", Set.of("USER"));
        userRepository.save(user);

        var found = userRepository.findByUsername("dbtest");
        assertTrue(found.isPresent());
        assertEquals("dbtest", found.get().getUsername());
    }

    @Test
    void testFindUserByUsernameNotFound() {
        var notFound = userRepository.findByUsername("nonexistent");
        assertTrue(notFound.isEmpty());
    }

    @Test
    void testUserWithMultipleRolesArePersisted() {
        User user = new User("multirole", "pass", Set.of("USER", "ADMIN", "MODERATOR"));
        userRepository.save(user);

        var found = userRepository.findByUsername("multirole");
        assertTrue(found.isPresent());
        assertEquals(3, found.get().getRoles().size());
    }

    @Test
    void testUpdateUserPassword() {
        User user = new User("updatetest", "password", Set.of("USER"));
        userRepository.save(user);

        var found = userRepository.findByUsername("updatetest").get();
        found.setPassword("newpassword");
        userRepository.save(found);

        var updated = userRepository.findByUsername("updatetest").get();
        assertEquals("newpassword", updated.getPassword());
    }

    @Test
    void testDeleteUser() {
        User user = new User("deletetest", "password", Set.of("USER"));
        User saved = userRepository.save(user);

        userRepository.deleteById(saved.getId());

        var deleted = userRepository.findByUsername("deletetest");
        assertTrue(deleted.isEmpty());
    }

    @Test
    void testFindAllUsers() {
        userRepository.save(new User("user1", "pass", Set.of("USER")));
        userRepository.save(new User("user2", "pass", Set.of("USER")));
        userRepository.save(new User("user3", "pass", Set.of("ADMIN")));

        assertEquals(3, userRepository.findAll().size());
    }
}
