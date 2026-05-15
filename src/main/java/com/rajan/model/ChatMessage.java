package com.rajan.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class ChatMessage {

    @NotNull(message = "Message type is required")
    private MessageType type;

    @Size(max = 1000, message = "Message content must be 1000 characters or less")
    private String content;

    @NotBlank(message = "Sender is required")
    @Size(max = 100, message = "Sender must be 100 characters or less")
    private String sender;

    public enum MessageType {
        CHAT,
        JOIN,
        LEAVE
    }

    public ChatMessage() {
    }

    public ChatMessage(MessageType type, String content, String sender) {
        this.type = type;
        this.content = normalize(content);
        this.sender = normalize(sender);
    }

    public MessageType getType() {
        return type;
    }

    public void setType(MessageType type) {
        this.type = type;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = normalize(content);
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = normalize(sender);
    }

    private String normalize(String value) {
        return value == null ? null : value.trim();
    }
}
