package com.ai.service.websocket.config;

import com.ai.service.websocket.handle.MessageHandler;
import com.ai.service.websocket.interceptor.MyHandshakeInterceptor;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
@AllArgsConstructor
public class WebSocketConfig implements WebSocketConfigurer {

    private MyHandshakeInterceptor myHandshakeInterceptor;
    private MessageHandler messageHandler;

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry
                .addHandler(messageHandler, "ws")
                .addInterceptors(myHandshakeInterceptor)
                .setAllowedOriginPatterns("*");
    }
}
