package io.github.arubaid.testsupport.integration;

import org.testcontainers.postgresql.PostgreSQLContainer;

public final class SharedPostgresContainer {

    private SharedPostgresContainer() {
    }

    public static final PostgreSQLContainer POSTGRES =
            new PostgreSQLContainer("postgres:18")
                    .withDatabaseName("spring_boot_enterprise_template_test")
                    .withUsername("postgres")
                    .withPassword("postgres");

    static {
        POSTGRES.start();
    }
}