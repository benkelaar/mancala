package com.bol.mancala;

/**
 * Interface to get update for the game.
 */
public interface GameListener {
    /**
     * State of the game has been changed.
     * So here you can analyze it/ send some response.
     * Or it is possible to make a new turn right from the body - in this case
     * it will be a sync game.
     *
     * You need the {@link GamePlayer} object ot get an access to the game.
     * This method just notifies you.
     */
    void stateChanged();
}
