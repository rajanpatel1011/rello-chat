package com.rajan.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Controller;

import com.rajan.model.ChatMessage;

@Controller
public class ChatController {
	private static final Logger logger = LoggerFactory.getLogger(ChatController.class);

	@MessageMapping("/chat.sendMessage")
	@SendTo("/topic/public")
	public ChatMessage sendMessage(@Payload ChatMessage chatMessage) {
		logger.info("Contenct: "+chatMessage.getContent()+" Sender :"+chatMessage.getSender());
		
		return chatMessage;
	}
	
	
	@MessageMapping("/chat.addUser")
	@SendTo("/topic/public")
	public ChatMessage addUser(@Payload ChatMessage chatMessage,SimpMessageHeaderAccessor headerAccessor) {
		headerAccessor.getSessionAttributes().put("username",chatMessage.getSender());	
		logger.info(" addUser Method > Contenct: "+chatMessage.getContent()+" Sender :"+chatMessage.getSender());

		return chatMessage;
		}
}
