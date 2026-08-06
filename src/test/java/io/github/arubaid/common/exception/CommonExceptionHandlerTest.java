package io.github.arubaid.common.exception;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CommonExceptionHandlerTest {

    private final CommonExceptionHandler handler =
            new CommonExceptionHandler();

    @Test
    void shouldReturnNotImplementedResponse() {

        FeatureNotImplementedException exception =
                new FeatureNotImplementedException("Get all users");

        ResponseEntity<String> response =
                handler.handleFeatureNotImplemented(exception);

        assertEquals(HttpStatus.NOT_IMPLEMENTED, response.getStatusCode());
        assertEquals(
                "Get all users is coming soon.",
                response.getBody()
        );
    }
}