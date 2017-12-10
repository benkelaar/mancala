package com.bol.mancala.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;

import com.bol.mancala.Desk;
import com.bol.mancala.Game;
import com.bol.mancala.GameListener;
import com.bol.mancala.GamePlayer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by kopernik on 06/12/2017.
 */
public class GameImpl implements Game {
    private static final Logger logger = LoggerFactory.getLogger(GameImpl.class);

    private Desk desk;

    private int nextPlayerTurn = 0;

    private int playersCount = 0;

    private int lastTurn;

    private boolean started = false;
    private boolean finished = false;

    private List<GameListener> listeners = new CopyOnWriteArrayList<>();
    private List<String> playerNames = new ArrayList<>();

    /**
     * Create game based on a desk (it might be some predefined state)
     * The first turn will be calculated randomly.
     * @param desk desk to play on
     */
    public GameImpl(Desk desk) {
        this(desk, (int)(Math.random() * desk.getMaxPlayers()));
    }

    /**
     * Constructor with predefined first turn (for tests mostly).
     * @param desk desk
     * @param firstPlayerTurn first player id
     */
    public GameImpl(Desk desk, int firstPlayerTurn) {
        this.desk = desk;
        this.nextPlayerTurn = firstPlayerTurn % desk.getMaxPlayers();
    }

    @Override
    public synchronized GamePlayer registerForGame(String name) {
        if(playersCount >= desk.getMaxPlayers()) {
            throw new IllegalStateException("Game is full.");
        }
        GamePlayer player = createGamePlayer(name, playersCount++);
        playerNames.add(name);
        checkStarted();
        return player;
    }

    private GamePlayer createGamePlayer(String name, int idx) {
        return new GamePlayerImpl(this, name, idx);
    }

    private boolean checkStarted() {
        if(!started && !finished && playersCount == desk.getMaxPlayers()){
            started = true;
            notifyListeners();
            return true;
        }
        return false;
    }

    private void notifyListeners() {
        listeners.stream().forEach(x -> {
            try{
                x.stateChanged();
            }catch (RuntimeException e) {
                //its not our business, listener should'n produce errors
                logger.warn("Some problem inside the game listener.", e);
            }
        });
    }

    protected synchronized boolean isPlayerTurn(int playerId) {
        return started && !finished && playerId == nextPlayerTurn;
    }

    protected synchronized int getSeedsOnTheDesk(){
        return desk.getTotalSeedsOnDesk();
    }

    protected synchronized int[] getPlayersPits(int player){
        return IntStream.range(0, desk.getPitsPerPlayer())
                .map(i -> desk.getSeeds(player, i))
                .toArray();
    }

    protected synchronized int opponentIdx(int playerId){
        return (++playerId) % desk.getMaxPlayers();
    }


    protected synchronized int getSeeds(int playerId, int pit){
        return desk.getSeeds(playerId, pit);
    }

    protected synchronized int getBasket(int player) {
        return desk.getBasket(player);
    }

    protected synchronized void turn(int playerId, int pitIdx) {
        if (!started) {
            throw new IllegalStateException("Waiting for users");
        }
        if (!isPlayerTurn(playerId)) {
            throw new IllegalArgumentException("It's not your turn");
        }
        if (desk.getSeeds(playerId, pitIdx) == 0) {
            return;
        }

        lastTurn = pitIdx;

        if(!desk.processSeeds(playerId, pitIdx)) {
            changeTurn();
        }

        logger.debug("Player {} did a turn {} pit, desk after the turn - {}",
                playerNames.get(playerId),
                pitIdx,
                desk.toString());

        if (testEndGame(desk)) {
            finished = true;
        }

        notifyListeners();
    }

    private void changeTurn() {
        nextPlayerTurn = (++nextPlayerTurn) % desk.getMaxPlayers();
    }

    protected synchronized int lastTurn(){
        return lastTurn;
    }

    private boolean testEndGame(Desk desk) {
        if(IntStream.range(0,desk.getMaxPlayers())
            .map(i -> desk.getSeedsOnDeskForPlayer(i))
            .anyMatch(seeds -> seeds==0)){
            desk.putAllSeedsFromDeskToBaskets();
            return true;
        }
        return false;
    }

    @Override
    public synchronized boolean isStarted() {
        return started;
    }

    @Override
    public synchronized boolean isFinished() {
        return finished;
    }

    @Override
    public int getPitsCountPerPlayer() {
        return desk.getPitsPerPlayer();
    }

    protected synchronized void disconnect(int playerId){
        logger.debug("Player '{}' has disconnected from the game", playerNames.get(playerId));
        finish();
    }

    private void finish() {
        if(!finished) {
            finished=true;
            notifyListeners();
        }
    }

    protected synchronized int getTotalSeeds() {
        return desk.getTotalSeedsOnDesk();
    }


    protected synchronized String getPlayerName(int playerId) {
        return playerId<playerNames.size()?playerNames.get(playerId):null;
    }

    @Override
    public void addGameListener(GameListener listener) {
        listeners.add(listener);
    }

    @Override
    public void removeGameListener(GameListener listener) {
        listeners.remove(listener);
    }
}
