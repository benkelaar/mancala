package com.bol.mancala.web;

import com.bol.mancala.GameUser;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

public class WebGameSession extends TextWebSocketHandler {
    private static final Logger logger = LoggerFactory.getLogger(WebGameSession.class);

    @Autowired
    private WebGamePool gamePool;

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        logger.info("Connection established "+session.getId()+session + " "+this);
        GameUser player = gamePool.findOrCreateGameForUser(session.getId());
        player.addGameListener(game -> {
            try {
                session.sendMessage(new TextMessage(new ObjectMapper().writeValueAsString(game)));
            }catch (Exception e){
                throw new RuntimeException(e);
            }
        });
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        logger.info("Connection closed "+session.getId());
        gamePool.cleanupUserData(session.getId());
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        logger.info("message received "+message.getPayload());
        try {
            GameUser user = gamePool.findOrCreateGameForUser(session.getId());
            user.turn(Integer.parseInt(message.getPayload()));
        }catch (Exception e){
            session.sendMessage(new TextMessage("Error - "+e.getLocalizedMessage()));
        }
    }
}
