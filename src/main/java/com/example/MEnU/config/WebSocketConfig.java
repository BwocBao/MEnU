package com.example.MEnU.config;

import com.example.MEnU.security.JwtHandshakeInterceptor;
import com.example.MEnU.websocket.WebSocketHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.*;

@Configuration
@EnableWebSocket
@RequiredArgsConstructor
public class WebSocketConfig implements WebSocketConfigurer {

  private final JwtHandshakeInterceptor jwtHandshakeInterceptor;
  private final WebSocketHandler webSocketHandler;

  @Override
  public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
    registry
        .addHandler(webSocketHandler, "/ws")
        .addInterceptors(jwtHandshakeInterceptor)
        .setAllowedOrigins("*");
  }
}

// @Configuration
// @EnableWebSocketMessageBroker
// public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {
//
//    @Override
//    public void configureMessageBroker(MessageBrokerRegistry config) {
//        // Simple broker supports /topic and /queue for broadcasting and user queues
//        config.enableSimpleBroker("/topic", "/queue");
//        // client gửi message
//        config.setApplicationDestinationPrefixes("/app");
//        // Prefix used for user-specific destinations (convertAndSendToUser)
//        config.setUserDestinationPrefix("/user");
//    }
//
//    @Override
//    public void registerStompEndpoints(StompEndpointRegistry registry) {
//        // endpoint for websocket connections; enable SockJS fallback
//        registry.addEndpoint("/ws")
//                .setAllowedOriginPatterns("*")
//                .withSockJS();
//    }
// }
