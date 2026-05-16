package com.rajan.error;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class ErrorHandlerTest {

    @Test
    void testErrorHandlerCreation() {
        ErrorHandler errorHandler = new ErrorHandler();
        assertNotNull(errorHandler);
    }

    @Test
    void testErrorHandlerIsExtendingStompSubProtocolErrorHandler() {
        ErrorHandler errorHandler = new ErrorHandler();
        assert errorHandler instanceof org.springframework.web.socket.messaging.StompSubProtocolErrorHandler;
    }
}
