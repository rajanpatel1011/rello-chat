package com.rajan.config;

import com.rajan.model.User;
import com.rajan.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Set;

@Configuration
public class DataInitializer {

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
                System.out.println("Admin user created");
            }

            // Create regular users if they don't exist
            if (userRepository.findByUsername("user1").isEmpty()) {
                User user1 = new User(
                        "user1",
                        passwordEncoder.encode("user123"),
                        Set.of("USER")
                );
                userRepository.save(user1);
                System.out.println("User1 created");
            }

            if (userRepository.findByUsername("user2").isEmpty()) {
                User user2 = new User(
                        "user2",
                        passwordEncoder.encode("user456"),
                        Set.of("USER")
                );
                userRepository.save(user2);
                System.out.println("User2 created");
            }

            System.out.println("Data initialization complete!");
        };
    }
}