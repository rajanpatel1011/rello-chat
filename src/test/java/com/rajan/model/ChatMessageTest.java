package com.rajan.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ChatMessageTest {

    private ChatMessage chatMessage;

    @BeforeEach
    void setUp() {
        chatMessage = new ChatMessage();
    }

    @Test
    void testCreateChatMessage() {
        assertNull(chatMessage.getType());
        assertNull(chatMessage.getContent());
        assertNull(chatMessage.getSender());
    }

    @Test
    void testSetAndGetType() {
        chatMessage.setType(ChatMessage.MessageType.CHAT);
        assertEquals(ChatMessage.MessageType.CHAT, chatMessage.getType());
    }

    @Test
    void testSetAndGetContent() {
        String content = "Hello World";
        chatMessage.setContent(content);
        assertEquals(content, chatMessage.getContent());
    }

    @Test
    void testSetAndGetSender() {
        String sender = "testuser";
        chatMessage.setSender(sender);
        assertEquals(sender, chatMessage.getSender());
    }

    @Test
    void testAllMessageTypes() {
        assertNotNull(ChatMessage.MessageType.CHAT);
        assertNotNull(ChatMessage.MessageType.JOIN);
        assertNotNull(ChatMessage.MessageType.LEAVE);
    }

    @Test
    void testChatMessageWithAllFields() {
        chatMessage.setType(ChatMessage.MessageType.CHAT);
        chatMessage.setContent("Test message");
        chatMessage.setSender("user1");

        assertEquals(ChatMessage.MessageType.CHAT, chatMessage.getType());
        assertEquals("Test message", chatMessage.getContent());
        assertEquals("user1", chatMessage.getSender());
    }

    @Test
    void testJoinMessageType() {
        chatMessage.setType(ChatMessage.MessageType.JOIN);
        chatMessage.setSender("newuser");
        
        assertEquals(ChatMessage.MessageType.JOIN, chatMessage.getType());
    }

    @Test
    void testLeaveMessageType() {
        chatMessage.setType(ChatMessage.MessageType.LEAVE);
        chatMessage.setSender("leftuser");
        
        assertEquals(ChatMessage.MessageType.LEAVE, chatMessage.getType());
    }
}
