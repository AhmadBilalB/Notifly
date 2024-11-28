package com.example.notifly.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        // Enables a simple broker for sending messages to clients
        registry.enableSimpleBroker("/topic", "/queue");
        // Prefix for application destination mapping
        registry.setApplicationDestinationPrefixes("/app");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // Registers the endpoint for WebSocket connections
        registry.addEndpoint("/notifications")
                .setAllowedOrigins("*")
                .withSockJS(); // Fallback option for browsers that don’t support WebSockets
    }
}
