package com.rajan.error;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.StompSubProtocolErrorHandler;

public class ErrorHandler extends StompSubProtocolErrorHandler {

    public ErrorHandler() {
    }

}
