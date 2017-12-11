package com.bol.mancala.web;

import com.bol.mancala.impl.DeskImpl;
import com.bol.mancala.Game;
import com.bol.mancala.impl.GameImpl;
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

    private final Map<String, GamePlayer> players = new ConcurrentHashMap<>();

    // only one available game
    // next new player will register for this game and go to players pool
    private Game gameWaitingForPlayer = null;

    @Override
    public synchronized GamePlayer createGameForUser(String sessionId, String playerName) {
        GamePlayer serverPlayer = players.get(sessionId);
        if (serverPlayer != null) {
            return serverPlayer;
        }
        // find or create a game
        if (gameWaitingForPlayer == null) {
            logger.info("Creating a game for player '{}'", playerName);
            gameWaitingForPlayer = createGame();
            serverPlayer = gameWaitingForPlayer.registerForGame(playerName);
        } else {
            logger.info("Found already available game for player '{}'", playerName);
            serverPlayer = gameWaitingForPlayer.registerForGame(playerName);
            gameWaitingForPlayer = null;
        }
        players.put(sessionId, serverPlayer);
        return serverPlayer;
    }

    /**
     * Here is an extension point.
     * We can create any different game/desk implementation.
     * @return new game
     */
    protected Game createGame() {
        return new GameImpl(new DeskImpl());
    }

    @Override
    public GamePlayer findUser(String sessionId) {
        GamePlayer serverPlayer = players.get(sessionId);
        if (serverPlayer == null) {
            throw new IllegalArgumentException("Cannot find player info for sessionId = " + sessionId);
        }
        return serverPlayer;
    }

    @Override
    public void cleanupUserData(String sessionId) {
        GamePlayer serverPlayer = players.remove(sessionId);
        if (serverPlayer != null) {
            serverPlayer.disconnect();
        }
    }
}
