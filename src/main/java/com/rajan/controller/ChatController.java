package com.rajan.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Controller;

import com.rajan.model.ChatMessage;

import java.util.Map;
import java.util.Objects;

@Controller
public class ChatController {
    private static final Logger logger = LoggerFactory.getLogger(ChatController.class);

    @MessageMapping("/chat.sendMessage")
    @SendTo("/topic/public")
    public ChatMessage sendMessage(@Payload ChatMessage chatMessage) {
        validateChatMessage(chatMessage);
        logger.info("Sending message of type {} from {} with content length {}",
                chatMessage.getType(), chatMessage.getSender(), chatMessage.getContent() == null ? 0 : chatMessage.getContent().length());
        return chatMessage;
    }

    @MessageMapping("/chat.addUser")
    @SendTo("/topic/public")
    public ChatMessage addUser(@Payload ChatMessage chatMessage, SimpMessageHeaderAccessor headerAccessor) {
        validateAddUser(chatMessage);
        Map<String, Object> sessionAttributes = Objects.requireNonNull(headerAccessor.getSessionAttributes(), "Session attributes must be available");
        sessionAttributes.put("username", chatMessage.getSender());
        logger.info("Registering websocket user {}", chatMessage.getSender());
        return chatMessage;
    }

    private void validateChatMessage(ChatMessage chatMessage) {
        if (chatMessage == null) {
            throw new IllegalArgumentException("Chat message payload must not be null");
        }
        if (chatMessage.getType() == null) {
            throw new IllegalArgumentException("Message type is required");
        }
        if (chatMessage.getSender() == null || chatMessage.getSender().isBlank()) {
            throw new IllegalArgumentException("Sender is required");
        }
        if (chatMessage.getSender().length() > 100) {
            throw new IllegalArgumentException("Sender must be 100 characters or less");
        }
        if (chatMessage.getType() == ChatMessage.MessageType.CHAT) {
            if (chatMessage.getContent() == null || chatMessage.getContent().isBlank()) {
                throw new IllegalArgumentException("Chat content is required for CHAT messages");
            }
        }
        if (chatMessage.getContent() != null && chatMessage.getContent().length() > 1000) {
            throw new IllegalArgumentException("Message content must be 1000 characters or less");
        }
    }

    private void validateAddUser(ChatMessage chatMessage) {
        if (chatMessage == null) {
            throw new IllegalArgumentException("Add user payload must not be null");
        }
        if (chatMessage.getSender() == null || chatMessage.getSender().isBlank()) {
            throw new IllegalArgumentException("Sender is required to add a user");
        }
        if (chatMessage.getSender().length() > 100) {
            throw new IllegalArgumentException("Sender must be 100 characters or less");
        }
    }
}
