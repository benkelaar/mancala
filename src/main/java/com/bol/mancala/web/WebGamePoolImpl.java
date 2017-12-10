package com.bol.mancala.web;

import com.bol.mancala.Desk6x6;
import com.bol.mancala.Game;
import com.bol.mancala.GamePlayer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@Scope("singleton")
public class WebGamePoolImpl implements WebGamePool {
    private static final Logger logger = LoggerFactory.getLogger(WebGamePoolImpl.class);

    private Map<String, GamePlayer> users = new ConcurrentHashMap<>();
    private Game gameWaitingForPlayer = null;

    @Override
    public GamePlayer createGameForUser(String sessionId, GamePlayer player) {
        logger.info("POOL");
        GamePlayer user = users.get(sessionId);
        if (user != null) {
            return user;
        }
        synchronized (this) {
            if (gameWaitingForPlayer == null) {
                gameWaitingForPlayer = new Game(new Desk6x6());
                user = gameWaitingForPlayer.registerForGame(player);
            } else {
                user = gameWaitingForPlayer.registerForGame(player);
                gameWaitingForPlayer = null;
            }
        }
        users.put(sessionId, user);
        return user;
    }

    @Override
    public GamePlayer findUser(String sessionId) {
        return users.get(sessionId);
    }
    @Override
    public void cleanupUserData(String sessionId) {
        GamePlayer user = users.remove(sessionId);
        if(user!=null){
            user.disconnect();
        }
    }
}
