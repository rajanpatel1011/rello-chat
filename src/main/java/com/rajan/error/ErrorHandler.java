package com.rajan.error;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.StompSubProtocolErrorHandler;

@Component
public class ErrorHandler extends StompSubProtocolErrorHandler {

    public ErrorHandler() {
    }

}
