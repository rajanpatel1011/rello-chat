package com.rajan.controller;

import com.rajan.model.ChatMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;

import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;

class ChatControllerTest {

    private ChatController chatController;
    private ChatMessage testMessage;

    @BeforeEach
    void setUp() {
        chatController = new ChatController();
        testMessage = new ChatMessage();
        testMessage.setType(ChatMessage.MessageType.CHAT);
        testMessage.setContent("Test message");
        testMessage.setSender("testuser");
    }

    @Test
    void testSendMessageReturnsMessage() {
        ChatMessage result = chatController.sendMessage(testMessage);
        assertNotNull(result);
        assertEquals(testMessage.getContent(), result.getContent());
        assertEquals(testMessage.getSender(), result.getSender());
        assertEquals(ChatMessage.MessageType.CHAT, result.getType());
    }

    @Test
    void testSendMessageWithDifferentContent() {
        ChatMessage message = new ChatMessage();
        message.setType(ChatMessage.MessageType.CHAT);
        message.setContent("Different content");
        message.setSender("user2");

        ChatMessage result = chatController.sendMessage(message);
        assertEquals("Different content", result.getContent());
        assertEquals("user2", result.getSender());
    }

    @Test
    void testSendMessageWithJoinType() {
        ChatMessage joinMessage = new ChatMessage();
        joinMessage.setType(ChatMessage.MessageType.JOIN);
        joinMessage.setContent("User joined");
        joinMessage.setSender("newuser");

        ChatMessage result = chatController.sendMessage(joinMessage);
        assertNotNull(result);
        assertEquals("newuser", result.getSender());
        assertEquals(ChatMessage.MessageType.JOIN, result.getType());
    }

    @Test
    void testSendMessageWithLeaveType() {
        ChatMessage leaveMessage = new ChatMessage();
        leaveMessage.setType(ChatMessage.MessageType.LEAVE);
        leaveMessage.setContent("User left");
        leaveMessage.setSender("leftuser");

        ChatMessage result = chatController.sendMessage(leaveMessage);
        assertNotNull(result);
        assertEquals("leftuser", result.getSender());
        assertEquals(ChatMessage.MessageType.LEAVE, result.getType());
    }

    @Test
    void testAddUserStoresUsernameInSession() {
        ChatMessage joinMessage = new ChatMessage();
        joinMessage.setType(ChatMessage.MessageType.JOIN);
        joinMessage.setSender("newuser");

        SimpMessageHeaderAccessor headerAccessor = SimpMessageHeaderAccessor.create();
        headerAccessor.setSessionAttributes(new HashMap<>());

        ChatMessage result = chatController.addUser(joinMessage, headerAccessor);

        assertNotNull(result);
        assertEquals("newuser", result.getSender());
        assertEquals(ChatMessage.MessageType.JOIN, result.getType());
        assertEquals("newuser", headerAccessor.getSessionAttributes().get("username"));
    }

    @Test
    void testSendMessageThrowsForBlankSender() {
        ChatMessage invalidMessage = new ChatMessage();
        invalidMessage.setType(ChatMessage.MessageType.CHAT);
        invalidMessage.setContent("Hello");
        invalidMessage.setSender("   ");

        assertThrows(IllegalArgumentException.class, () -> chatController.sendMessage(invalidMessage));
    }

    @Test
    void testSendMessageThrowsForChatWithoutContent() {
        ChatMessage invalidMessage = new ChatMessage();
        invalidMessage.setType(ChatMessage.MessageType.CHAT);
        invalidMessage.setSender("testuser");
        invalidMessage.setContent("    ");

        assertThrows(IllegalArgumentException.class, () -> chatController.sendMessage(invalidMessage));
    }

    @Test
    void testChatMessageTransport() {
        ChatMessage message = new ChatMessage();
        message.setType(ChatMessage.MessageType.CHAT);
        message.setContent("Transport test");
        message.setSender("sender1");

        ChatMessage sentMessage = chatController.sendMessage(message);

        assertNotNull(sentMessage);
        assertEquals(message.getType(), sentMessage.getType());
        assertEquals(message.getContent(), sentMessage.getContent());
        assertEquals(message.getSender(), sentMessage.getSender());
    }
}
