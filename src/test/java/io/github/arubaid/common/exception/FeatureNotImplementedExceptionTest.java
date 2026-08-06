package io.github.arubaid.common.exception;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class FeatureNotImplementedExceptionTest {

    @Test
    void shouldCreateExceptionWithComingSoonMessage() {
        FeatureNotImplementedException exception =
                new FeatureNotImplementedException("Test");

        assertEquals(
                "Test is coming soon.",
                exception.getMessage()
        );
    }
}