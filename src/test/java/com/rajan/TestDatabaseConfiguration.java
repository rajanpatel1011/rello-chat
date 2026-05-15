package com.rajan;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;

@TestConfiguration
@Profile("test")
public class TestDatabaseConfiguration {
    // Test configuration will use application-test.properties automatically
}
