package com.bol.mancala.impl;

import com.bol.mancala.Desk;

import java.util.Arrays;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.stream.IntStream;

/**
 * Desk with 6 pits per player and 6 initial seeds for each pit at startup.
 * This object isn't synchronized because the idea was to delegate player's synchronization
 * to a different class (see {@link com.bol.mancala.Game})
 *
 * @author nbogdanov
 */
public class DeskImpl implements Desk {
    private static final int DEFAULT_PITS_COUNT = 6;
    private static final byte DEFAULT_INITIAL_SEEDS = 6;
    private static final byte MAX_PLAYERS_COUNT = 2;

    private final int[] desk;
    private final int pitsPerPlayer;

    /**
     * Default constructor
     */
    public DeskImpl() {
        this(DEFAULT_PITS_COUNT, DEFAULT_INITIAL_SEEDS);
    }

    /**
     * Here we can construct the desk with different parameters.
     * For example there might be 7-8pits for user, but with 3 seeds in each.
     * @param pits pit's number for each player (without basket's pits)
     * @param initialSeeds initial number of seeds in each pit
     */
    public DeskImpl(int pits, int initialSeeds) {
        if (pits * MAX_PLAYERS_COUNT * initialSeeds > Integer.MAX_VALUE) {
            throw new IllegalArgumentException("Max seeds count is exceeded");
        }
        pitsPerPlayer = pits;
        desk = new int[(pitsPerPlayer + 1) * MAX_PLAYERS_COUNT];
        for (int i = 0; i < desk.length; i++) {
            desk[i] = isBasket(i) ? 0 : (byte) initialSeeds;
        }
    }

    /**
     * Here we can create manual desk with manual state.
     * This constructor is mostly for tests.
     * @param state the desk data
     */
    protected DeskImpl(int[] state) {
        if (state.length < 4 || state.length % 2 != 0) {
            throw new IllegalArgumentException("Wrong desk state. Should be at least 2 pits and 2 baskets");
        }
        pitsPerPlayer = (state.length - 2) / 2;
        desk = Arrays.copyOf(state, state.length);
    }

    @Override
    public int getSeeds(int player, int pit) {
        checkPlayerRange(player);
        checkPitRange(pit);
        return desk[getGlobalPitIdx(player, pit)];
    }

    @Override
    public int getBasket(int player) {
        checkPlayerRange(player);
        return desk[getGlobalBasketIdx(player)];
    }

    private boolean isBasket(int globalPit) {
        return (globalPit + 1) % (pitsPerPlayer + 1) == 0;
    }

    private int getSeeds(int globalPit) {
        return desk[globalPit];
    }

    /**
     * Calculate a global pit index based on player and player's pit
     */
    private int getGlobalPitIdx(int player, int pit) {
        checkPlayerRange(player);
        return (pitsPerPlayer + 1) * player + pit;
    }

    /**
     * Calculate global index for player's basket.
     */
    private int getGlobalBasketIdx(int player) {
        return getGlobalPitIdx(player, pitsPerPlayer);
    }

    /**
     * Check local pit range [0, pitsPerPlayer)
     * @param pit local pit index
     */
    private void checkPitRange(int pit) {
        if (pit < 0 || pit >= pitsPerPlayer) {
            throw new IllegalArgumentException(
                    String.format("Wrong pit position %d, should be in range [0,%d]", pit, pitsPerPlayer));
        }
    }

    private void checkPlayerRange(int player) {
        if (player < 0 || player >= MAX_PLAYERS_COUNT) {
            throw new IllegalArgumentException(
                    String.format("Wrong player number %d, should be in range [0,%d]", player, MAX_PLAYERS_COUNT - 1));
        }
    }


    /**
     * Process seeds from player's pit. It will automatically process all the pits which are right
     * of the selected pit, to determinate now many seeds do we need to put in each pit we use a
     * special processor.
     *
     * @param player            player's index
     * @param pit               pit's index
     * @param processor         algorithm how we'd like to process them. For input (globalPitIdx , seedsInTheHand)
     *                          we need to return how many seeds we'd like to put into {@code globalPitIdx}
     * @param lastSeedProcessor processor to handle the last seed case. For (player, globalLastPitIdx)
     *                          we can write some code to handle it.
     * @return global last pit index
     */
    private int processSeeds(int player, int pit, BiFunction<Integer, Integer, Integer> processor,
                             BiConsumer<Integer, Integer> lastSeedProcessor) {
        int globalPitIdx = getGlobalPitIdx(player, pit);
        int seeds = desk[globalPitIdx];
        if (seeds == 0) {
            return globalPitIdx;
        }
        desk[globalPitIdx] = 0;
        for (; seeds > 0; ) {
            globalPitIdx = (++globalPitIdx) % desk.length;
            int seedsToPut = processor.apply(globalPitIdx, seeds);
            if (seedsToPut > seeds) {
                throw new IllegalArgumentException("Cannot put more seeds than you have");
            }
            desk[globalPitIdx] += seedsToPut;
            seeds -= seedsToPut;
        }
        lastSeedProcessor.accept(player, globalPitIdx);
        return globalPitIdx;
    }

    @Override
    public int getSeedsOnDeskForPlayer(int player) {
        checkPlayerRange(player);
        return IntStream.range(0, desk.length)
                .filter(i -> player == getPitOwner(i) && !isBasket(i))
                .map(i -> desk[i])
                .sum();
    }

    @Override
    public int getTotalSeedsOnDesk() {
        return Arrays.stream(desk).sum();
    }

    /**
     * There is an item in the specification:
     *   Always when the last stone lands in an own empty pit, the player captures his own stone and all stones
     *   in the opposite pit (the other players pit) and puts them in his own pit.
     * The original rule here is the player captures the stones only if the last stone lands
     * in an OWN EMPTY pit and THERE ARE STONES in the opposite pit.
     *
     * There we use {@link #processSeeds(int, int, BiFunction, BiConsumer)} with some default
     * processors:
     * - for seeds processor we are putting 1 seed in each pit except an opponent's pit
     * - for the last seed we check the ownership of the last pit and the opposite pit seeds
     */
    @Override
    public boolean processSeeds(int player, int pit) {
        int lastPitIdx =
            processSeeds(player,
                pit,
                // 1 seed per pit
                (i, seeds) -> i != getOppositePit(getGlobalBasketIdx(player)) ? 1 : 0,
                //last seed process
                (play, idx) -> {
                    if (getPitOwner(idx) == play
                            && !isBasket(idx)
                            && getSeeds(idx) == 1
                            && getSeeds(getOppositePit(idx)) > 0) {
                        putIntoBasket(play, idx);
                        putIntoBasket(play, getOppositePit(idx));
                    }
                });
        return getGlobalBasketIdx(player) == lastPitIdx;
    }


    /**
     * Transfer seeds from global pit to the player's basket
     * @param player  player index
     * @param globalPit global pit index
     */
    protected void putIntoBasket(int player, int globalPit) {
        checkPlayerRange(player);
        desk[getGlobalBasketIdx(player)] += desk[globalPit];
        desk[globalPit] = 0;
    }

    /**
     * Determinate the owner for a global pit index
     * @param globalPitIdx pit index
     * @return player index
     */
    private int getPitOwner(int globalPitIdx) {
        return globalPitIdx / (pitsPerPlayer + 1);
    }

    /**
     * Get opposite pit (for grabbing seeds in case of last seed in empty own pit)
     * @param globalPitIdx pit index
     * @return the opposite pit index (for player's basket it return the opponents basket)
     */
    protected int getOppositePit(int globalPitIdx) {
        return (globalPitIdx + 1) % (pitsPerPlayer + 1) == 0 ?
                (globalPitIdx + (pitsPerPlayer + 1)) % desk.length :
                desk.length - globalPitIdx - 2;
    }

    @Override
    public void putAllSeedsFromDeskToBaskets() {
        IntStream.range(0, desk.length)
                .filter(i -> !isBasket(i) && getSeeds(i)>0)
                .forEach(i -> putIntoBasket(getPitOwner(i), i));
    }

    protected int[] getDesk() {
        return Arrays.copyOf(desk, desk.length);
    }

    @Override
    public int getMaxPlayers() {
        return MAX_PLAYERS_COUNT;
    }

    @Override
    public int getPitsPerPlayer() {
        return pitsPerPlayer;
    }

    @Override
    public String toString() {
        return Arrays.toString(desk);
    }
}

