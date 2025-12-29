package com.rajan.config;

import com.rajan.model.User;
import com.rajan.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Set;

@Configuration
public class DataInitializer {
    private static final Logger logger = LoggerFactory.getLogger(DataInitializer.class);

    @Bean
    public CommandLineRunner initData(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            // Create admin user if it doesn't exist
            if (userRepository.findByUsername("admin").isEmpty()) {
                User admin = new User(
                        "admin",
                        passwordEncoder.encode("admin123"),
                        Set.of("ADMIN", "USER")
                );
                userRepository.save(admin);
                logger.info("Admin user created");
            }

            // Create regular users if they don't exist
            if (userRepository.findByUsername("user1").isEmpty()) {
                User user1 = new User(
                        "user1",
                        passwordEncoder.encode("user123"),
                        Set.of("USER")
                );
                userRepository.save(user1);
                logger.info("User1 created");
            }

            if (userRepository.findByUsername("user2").isEmpty()) {
                User user2 = new User(
                        "user2",
                        passwordEncoder.encode("user456"),
                        Set.of("USER")
                );
                userRepository.save(user2);
                logger.info("User2 created");
            }

            logger.info("Data initialization complete!");
        };
    }
}