package com.bol.mancala;

/**
 * Mechanical part of the game:
 * - storing the seeds
 * - get seeds for pit
 * - make sure the total number of seeds unchanged
 * - transition seeds between the pits
 *
 */
public interface Desk {

    /**
     * Get seeds count in player's pit.
     * @param player player id
     * @param pit pit id
     * @return number of seeds in the pit
     */
    int getSeeds(int player, int pit);

    /**
     * Get seeds count in player's basket
     * @param player player id
     * @return number of seeds in the basket
     */
    int getBasket(int player);

    /**
     * Get total number of seeds in all players pits (except basket pit)
     * @param player player id
     * @return number of seeds for player
     */
    int getSeedsOnDeskForPlayer(int player);

    /**
     * Total seeds on the desk
     * @return number of the seeds
     */
    int getTotalSeedsOnDesk();

    /**
     * Get the seeds from the player's pit and put them in the right pits, one in each pit.
     *
     * @param player player id
     * @param pit  player's pit index
     * @return has the last seed been land in player's basket?
     */
    boolean processSeeds(int player, int pit);

    /**
     * Return the number of players
     */
    int getMaxPlayers();

    /**
     * Return pit's count per player (only working pit's without basket)
     */
    int getPitsPerPlayer();

    /**
     * Move all the seeds from work pit's to players basket seeds.
     * Usually, it's the last operation after a game is over.
     */
    void putAllSeedsFromDeskToBaskets();
}
