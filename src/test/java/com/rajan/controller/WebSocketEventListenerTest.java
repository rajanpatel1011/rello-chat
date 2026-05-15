package com.rajan.controller;

import com.rajan.model.ChatMessage;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.messaging.support.GenericMessage;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@ActiveProfiles("test")
class WebSocketEventListenerTest {

    @Autowired
    private WebSocketEventListener webSocketEventListener;

    @Test
    void testWebSocketEventListenerCreation() {
        assertNotNull(webSocketEventListener);
    }

    @Test
    void testHandleWebSocketConnectListener() {
        // Create a mock SessionConnectEvent
        byte[] payload = new byte[0];
        org.springframework.web.socket.messaging.SessionConnectEvent event = 
                new org.springframework.web.socket.messaging.SessionConnectEvent(
                        this,
                        new GenericMessage<>(payload)
                );
        
        // Should not throw exception
        webSocketEventListener.handleWebSocketConnectListener(event);
    }

    @Test
    void testChatMessageCreation() {
        ChatMessage message = new ChatMessage();
        message.setType(ChatMessage.MessageType.LEAVE);
        message.setSender("testuser");
        
        assertNotNull(message);
        assert message.getType().equals(ChatMessage.MessageType.LEAVE);
    }
}
