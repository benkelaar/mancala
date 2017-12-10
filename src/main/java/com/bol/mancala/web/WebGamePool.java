package com.bol.mancala.web;

import com.bol.mancala.GamePlayer;

/**
 * Here we'd like to store players.
 * This should be a singleton bean.
 */
public interface WebGamePool {
    /**
     * Find or create a game for the new user. Its always possible to do.
     *
     * @param sessionId  session id for user
     * @param player     game API object for player
     * @return just return input parameter player
     */
    GamePlayer createGameForUser(String sessionId, GamePlayer player);

    /**
     * Find
     * @param sessionId
     * @return
     */
    GamePlayer findUser(String sessionId);

    void cleanupUserData(String sessionId);
}
