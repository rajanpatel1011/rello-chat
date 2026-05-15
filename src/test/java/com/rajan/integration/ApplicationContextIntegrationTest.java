package com.rajan.integration;

import com.rajan.config.DataInitializer;
import com.rajan.config.SecurityConfig;
import com.rajan.config.WebSocketConfig;
import com.rajan.controller.*;
import com.rajan.error.ErrorHandler;
import com.rajan.repository.UserRepository;
import com.rajan.service.CustomUserDetailsService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class ApplicationContextIntegrationTest {

    @Autowired
    private ApplicationContext applicationContext;

    @Test
    void testApplicationContextLoads() {
        assertNotNull(applicationContext);
    }

    @Test
    void testAllBeansAreCreated() {
        assertNotNull(applicationContext.getBean(HealthController.class));
        assertNotNull(applicationContext.getBean(LoginController.class));
        assertNotNull(applicationContext.getBean(ChatController.class));
        assertNotNull(applicationContext.getBean(UserManagementController.class));
        assertNotNull(applicationContext.getBean(WebSocketEventListener.class));
    }

    @Test
    void testSecurityConfigBeanExists() {
        assertNotNull(applicationContext.getBean(SecurityConfig.class));
    }

    @Test
    void testWebSocketConfigBeanExists() {
        assertNotNull(applicationContext.getBean(WebSocketConfig.class));
    }

    @Test
    void testUserRepositoryBeanExists() {
        assertNotNull(applicationContext.getBean(UserRepository.class));
    }

    @Test
    void testCustomUserDetailsServiceBeanExists() {
        assertNotNull(applicationContext.getBean(CustomUserDetailsService.class));
    }

    @Test
    void testPasswordEncoderBeanExists() {
        assertNotNull(applicationContext.getBean(PasswordEncoder.class));
    }

    @Test
    void testErrorHandlerBeanExists() {
        assertNotNull(applicationContext.getBean(ErrorHandler.class));
    }

    @Test
    void testDataInitializerBeanExists() {
        assertNotNull(applicationContext.getBean(DataInitializer.class));
    }

    @Test
    void testMultipleBeansCanBeInjected() {
        UserRepository userRepository = applicationContext.getBean(UserRepository.class);
        PasswordEncoder passwordEncoder = applicationContext.getBean(PasswordEncoder.class);
        CustomUserDetailsService customUserDetailsService = 
                applicationContext.getBean(CustomUserDetailsService.class);

        assertNotNull(userRepository);
        assertNotNull(passwordEncoder);
        assertNotNull(customUserDetailsService);
    }
}
