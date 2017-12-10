package com.bol.mancala.web;

import com.bol.mancala.Game;
import com.bol.mancala.GamePlayer;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

/**
 * Session handler for player's game session.
 * There is only one handler for all requests, so lets avoid to store any state here.
 *
 * @author nbogdanov
 */
public class WebGameSession extends TextWebSocketHandler {
    private static final Logger logger = LoggerFactory.getLogger(WebGameSession.class);

    @Autowired
    private WebGamePool gamePool;

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        logger.info("Connection established sessionId = "+session.getId());
        String username = session.getUri().getQuery();
        username = username==null || username.isEmpty()? "unknown" : username;
        GamePlayer player = new GamePlayer(username) {
            @Override
            public void stateChanged(Game game) {
                sendGameState(session, new GameState(this));
            }
        };

        gamePool.createGameForUser(session.getId(), player);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        logger.info("Connection closed "+session.getId());
        gamePool.cleanupUserData(session.getId());
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        logger.info("message received "+message.getPayload());
        GamePlayer user = gamePool.findUser(session.getId());
        try {
            user.turn(Integer.parseInt(message.getPayload()));
        }catch (Exception e){
            sendGameState(session, new GameState(e, user));
        }
    }

    protected void sendGameState(WebSocketSession session, GameState state){
        try {
            if(session.isOpen()) {
                session.sendMessage(new TextMessage(new ObjectMapper().writeValueAsString(state)));
            }
        }catch (Exception e){
            throw new RuntimeException(e);
        }
    }
}
