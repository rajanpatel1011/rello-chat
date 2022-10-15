package com.rajan.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import com.rajan.model.ChatMessage;

import java.util.Objects;

@Component
public class WebSocketEventListener {

	private static final Logger logger = LoggerFactory.getLogger(WebSocketEventListener.class);
	private static final String method = "_METHOD_";
	
	@Autowired
	private SimpMessageSendingOperations messagingTemplate;
	
	@EventListener
	public void handleWebSocketConnectListener(SessionConnectEvent event) {
//		logger.info("Received a new Connection"+event.getUser().toString());
	}
	@EventListener
	public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
		StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
		String username = (String) Objects.requireNonNull(
				headerAccessor.getSessionAttributes()
		).get("username");

	  logger.info("webSocketEvent Message:"+ headerAccessor.getSessionAttributes().get("message"));
		try {
			if (username != null) {
				logger.info("User left : " + username);
				ChatMessage chatMessage = new ChatMessage();
				chatMessage.setType(ChatMessage.MessageType.LEAVE);
				chatMessage.setSender(username);
				messagingTemplate.convertAndSend("/topic/public", chatMessage);
			}
		}catch (Exception e){
			logger.error(method +" Exception "+e);
		}
	}
}
