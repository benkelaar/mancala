package com.bol.mancala.web;

import com.bol.mancala.GameListener;
import com.bol.mancala.impl.GameImpl;
import com.bol.mancala.GamePlayer;
import com.bol.mancala.impl.GamePlayerImpl;
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
 * There is only one handler for all requests, so lets avoid storing any state here.
 *
 * General scenario:
 * 1. Player connects to http://server:port/ws?username
 * 2. We ask {@code gamePool} for an available game (new one or an existing). Also we will create
 *    GameListener to notify user for any game state change.
 * 3. Player should receive an API object {@link GameState} as feedback.
 * 4. User can send a messages "pitIdx". We will parse them as number of pit he'd like to proceed.
 * 5. If user is left the connection we will disconnect him from the game (second user will be notified).
 *    Also we will cleanup the session information and listeners.
 *
 * @author nbogdanov
 */
public class WebGameSession extends TextWebSocketHandler {
    private static final Logger logger = LoggerFactory.getLogger(WebGameSession.class);

    @Autowired
    private WebGamePool gamePool;

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        String username = session.getUri().getQuery();
        logger.info("Connection established (sessionId = '{}', username = '{}')", session.getId(), username);
        username = username == null || username.isEmpty() ? "unknown" : username;

        try {
            //register for some game
            GamePlayer player = gamePool.createGameForUser(session.getId(), username);

            //create listener
            player.getGame().addGameListener(() -> sendGameState(session, new GameState(player)));

            //send first notification
            sendGameState(session, new GameState(player));
        }catch (Exception e){
            sendGameState(session, new GameState(e));
            session.close();
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        logger.info("Connection closed (sessionId = '{}')", session.getId());

        //just delete everything about this session
        gamePool.cleanupUserData(session.getId());
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        logger.info("Message received (sessionId = '{}', message = '{}')", session.getId(), message.getPayload());
        GamePlayer user = null;
        try {
            user = gamePool.findUser(session.getId());
            user.turn(Integer.parseInt(message.getPayload()));
        }catch (Exception e){
            sendGameState(session, new GameState(e, user));
        }
    }

    /**
     * Sending message back to user.
     * @param session user's session
     * @param state game state object
     */
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
