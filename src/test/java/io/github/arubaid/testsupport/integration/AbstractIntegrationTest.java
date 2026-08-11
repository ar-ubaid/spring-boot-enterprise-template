package io.github.arubaid.testsupport.integration;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

@SpringBootTest
@ActiveProfiles("test")
public abstract class AbstractIntegrationTest {

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {

        registry.add("spring.datasource.url",
                SharedPostgresContainer.POSTGRES::getJdbcUrl);

        registry.add("spring.datasource.username",
                SharedPostgresContainer.POSTGRES::getUsername);

        registry.add("spring.datasource.password",
                SharedPostgresContainer.POSTGRES::getPassword);

        registry.add("spring.datasource.driver-class-name",
                SharedPostgresContainer.POSTGRES::getDriverClassName);
    }
}