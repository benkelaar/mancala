package com.bol.mancala;

import java.util.Arrays;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.stream.IntStream;

/**
 * Created by kopernik on 05/12/2017.
 */
public class Desk {
    private static final int DEFAULT_PITS_COUNT = 6;
    private static final byte DEFAULT_INITIAL_SEEDS = 6;
    private static final byte MAX_PLAYERS_COUNT = 2;

    private final int[] desk;
    private final int pitsPerPlayer;

    public Desk() {
        this(DEFAULT_PITS_COUNT, DEFAULT_INITIAL_SEEDS);
    }

    public Desk(int pits, int initialSeeds) {
        if (pits * MAX_PLAYERS_COUNT * initialSeeds > Integer.MAX_VALUE) {
            throw new IllegalArgumentException("Max seeds count is exeided");
        }
        pitsPerPlayer = pits;
        desk = new int[(pitsPerPlayer + 1) * MAX_PLAYERS_COUNT];
        for (int i = 0; i < desk.length; i++) {
            desk[i] = isBasket(i) ? 0 : (byte) initialSeeds;
        }
    }

    protected Desk(int[] state) {
        if (state.length < 4 || state.length % 2 != 0) {
            throw new IllegalArgumentException("Wrong desk state. Should be at least 2 pits and 2 baskets");
        }
        pitsPerPlayer = (state.length - 2) / 2;
        desk = Arrays.copyOf(state, state.length);
    }

    public boolean isBasket(int globalPit) {
        return (globalPit + 1) % (pitsPerPlayer + 1) == 0;
    }

    public int getSeeds(int player, int pit) {
        checkPitRange(pit);
        return desk[getPlayersPit(player, pit)];
    }

    public int getSeeds(int globalPit) {
        return desk[globalPit];
    }

    private int getPlayersPit(int player, int pit) {
        checkPlayerRange(player);
        return (pitsPerPlayer + 1) * player + pit;
    }

    public int getBasket(int player) {
        return desk[getBasketIdx(player)];
    }

    public int getBasketIdx(int player) {
        return getPlayersPit(player, pitsPerPlayer);
    }

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


    public int processSeeds(int player, int pit, BiFunction<Integer, Integer, Integer> processor,
                            BiConsumer<Integer, Integer> lastSeedProcessor) {
        checkPlayerRange(player);
        int pitIdx = getPlayersPit(player, pit);
        int seeds = desk[pitIdx];
        if (seeds == 0) {
            return pitIdx;
        }
        desk[pitIdx] = 0;
        for (; seeds > 0; ) {
            pitIdx = (++pitIdx) % desk.length;
            int seedsToPut = processor.apply(pitIdx, seeds);
            if (seedsToPut > seeds) {
                throw new IllegalArgumentException("Cannot put more seeds when you have");
            }
            desk[pitIdx] += seedsToPut;
            seeds -= seedsToPut;
        }
        lastSeedProcessor.accept(player, pitIdx);
        return pitIdx;
    }

    public int getSeedsOnDeskForPlayer(int player) {
        return IntStream.range(0, desk.length)
                .filter(i -> player == getPitOwner(i) && !isBasket(i))
                .map(i -> desk[i])
                .sum();
    }

    public int getTotalSeedsOnDesk() {
        return Arrays.stream(desk).sum();
    }

    public int processSeeds(int player, int pit) {
        return processSeeds(player,
                pit,
                (i, seeds) -> i != getOppositePit(getBasketIdx(player)) ? 1 : 0,
                (play, idx) -> {
                    if (getPitOwner(idx) == play
                            && !isBasket(idx)
                            && getSeeds(idx) == 1
                            && getSeeds(getOppositePit(idx)) > 0) {
                        putIntoBasket(play, idx);
                        putIntoBasket(play, getOppositePit(idx));
                    }
                });
    }

    public void processSeeds(BiConsumer<Integer, Integer> processor) {
        IntStream.range(0, desk.length).forEach(i -> processor.accept(i, desk[i]));
    }

    public void putIntoBasket(int player, int globalPit) {
        desk[getBasketIdx(player)] += desk[globalPit];
        desk[globalPit] = 0;
    }

    public int getPitOwner(int pit) {
        return pit / (pitsPerPlayer + 1);
    }

    public int getOppositePit(int pit) {
        return (pit + pitsPerPlayer + 1) % desk.length;
    }

    protected int[] getDesk() {
        return Arrays.copyOf(desk, desk.length);
    }

    public int nextPlayer(int player) {
        return (++player) % MAX_PLAYERS_COUNT;
    }

    public int getMaxPlayers() {
        return MAX_PLAYERS_COUNT;
    }

    public int getPitsPerPlayer() {
        return pitsPerPlayer;
    }
}
