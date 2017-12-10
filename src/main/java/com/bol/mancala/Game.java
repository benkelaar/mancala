package com.bol.mancala;

/**
 * The main purpose of the class is to sync access of 2 players and
 * provide methods to change game state and get notifications.
 *
 */
public interface Game {

    /**
     * Register to the game.
     * Will accept first 2 player and return them API objects to access to the game.
     * @param name player name
     * @return new object to have an access to the game
     * @throws IllegalStateException if the game is already full
     */
    GamePlayer registerForGame(String name) throws IllegalStateException;

    /**
     * Is game started (there are 2 players in the game)
     */
    boolean isStarted();

    /**
     * Is game finished (the players reach the final turn and got the results or
     * one of them has left the game)
     */
    boolean isFinished();

    int getPitsCountPerPlayer();

    /**
     * Register for game notifications.
     * @param listener listener to add
     */
    void addGameListener(GameListener listener);

    /**
     * Unregister from game notifications.
     * @param listener listener to add
     */
    void removeGameListener(GameListener listener);
}
