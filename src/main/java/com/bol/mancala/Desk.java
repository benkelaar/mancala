package com.bol.mancala;

/**
 * Mechanical part of the game:
 * - storing the seeds
 * - get seeds for pit
 * - make sure the total number of seeds unchanged
 *
 *
 */
public interface Desk {

    int getSeeds(int player, int pit);

    int getBasket(int player);

    int getSeedsOnDeskForPlayer(int player);

    int getTotalSeedsOnDesk();

    boolean processSeeds(int player, int pit);

    int getMaxPlayers();

    int getPitsPerPlayer();

    void putAllSeedsFromDeskToBaskets();
}
