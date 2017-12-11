package com.bol.mancala.web;

import org.springframework.context.annotation.Bean;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.config.annotation.*;
import org.springframework.context.annotation.Configuration;

/**
 * Simple spring websocket configuration.
 * Just one endpoint - 'http://server:port/ws'.
 *
 * @see WebGameSession
 */
@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(gameHandler(), "/ws");
    }

    @Bean
    public WebSocketHandler gameHandler() {
        return new WebGameSession();
    }
}