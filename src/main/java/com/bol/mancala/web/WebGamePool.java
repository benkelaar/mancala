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
     * Find user info by sessionId
     * @param sessionId session id
     * @return player info (cannot be null)
     * @throws RuntimeException if there is no information for user
     */
    GamePlayer findUser(String sessionId) throws RuntimeException;

    /**
     * Cleanup any data for the session.
     * 1. disconnect from the game, if the user is in started game.
     * 2. delete ant reference to the player info.
     * @param sessionId session id
     */
    void cleanupUserData(String sessionId);
}
