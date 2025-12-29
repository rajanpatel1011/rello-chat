package com.rajan.config;

import com.rajan.error.ErrorHandler;
import org.springframework.lang.NonNull;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.*;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer{

    @Override
    public void registerStompEndpoints(@NonNull StompEndpointRegistry registry) {
        registry.addEndpoint("/ws").withSockJS();
        registry.setErrorHandler(new ErrorHandler());
    }

    @Override
	public void configureMessageBroker(@NonNull MessageBrokerRegistry registry) {
		registry.setApplicationDestinationPrefixes("/app");
		registry.enableSimpleBroker("/topic");
        registry.setCacheLimit(2048);
	}
}
