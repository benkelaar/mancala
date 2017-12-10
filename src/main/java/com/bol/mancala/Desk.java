package com.bol.mancala;

import java.util.function.BiConsumer;
import java.util.function.BiFunction;

/**
 * Mechanical part of the game:
 * - storing the seeds
 * - get seeds for pit
 * - make sure the total number of seeds unchanged
 *
 *
 */
public interface Desk {
    /**
     * Check is it a basket or not.
     * @param globalPit global
     * @return
     */
    boolean isBasket(int globalPit);

    int getSeeds(int player, int pit);

    int getSeeds(int globalPit);

    int getBasket(int player);

    int getBasketIdx(int player);

    int processSeeds(int player, int pit, BiFunction<Integer, Integer, Integer> processor,
                     BiConsumer<Integer, Integer> lastSeedProcessor);

    int getSeedsOnDeskForPlayer(int player);

    int getTotalSeedsOnDesk();

    int processSeeds(int player, int pit);

    void processSeeds(BiConsumer<Integer, Integer> processor);

    void putIntoBasket(int player, int globalPit);

    int getPitOwner(int pit);

    int getOppositePit(int pit);

    int getMaxPlayers();

    int getPitsPerPlayer();
}
