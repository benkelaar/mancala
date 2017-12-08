package com.bol.mancala;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.logging.LoggingSystem;

/**
 * Created by kopernik on 06/12/2017.
 */
public class Game {
    private static final Logger logger = LoggerFactory.getLogger(Game.class);

    private Desk desk;
    private int nextPlayerTurn;
    private boolean started = false;
    private boolean finished = false;

    private GameUser firstUser = null;
    private GameUser secondUser = null;

    private List<GameListener> listeners = new CopyOnWriteArrayList<>();

    protected Game() {
    }

    public Game(Desk desk) {
        this(desk, (int)(Math.random() * 2));
    }

    public Game(Desk desk, int firstTurn) {
        this.desk = desk;
        nextPlayerTurn = firstTurn;
    }

    public synchronized GameUser registerForGame(String name) {
        if (firstUser == null) {
            firstUser = new GameUser(this, 0, name);
            checkStarted();
            return firstUser;
        } else if (secondUser == null) {
            secondUser = new GameUser(this, 1, name);
            checkStarted();
            return secondUser;
        }
        throw new IllegalStateException("Game is fool");
    }

    private void checkStarted() {
        started = firstUser != null && secondUser != null;
        if(started){
            notifyListeners();
        }
    }

    private void notifyListeners() {
        listeners.stream().forEach(x -> x.stateChanged(this));
    }

    public boolean isPlayerTurn(int player) {
        return started && !finished && nextPlayerTurn == player;
    }

    public synchronized int getSeedsOnTheDesk(){
        return desk.getTotalSeedsOnDesk();
    }

    public synchronized int getSeeds(int player, int pit){
        return desk.getSeeds(player, pit);
    }

    public synchronized int getBasket(int player) {
        return desk.getBasket(player);
    }

    protected synchronized Game turn(int player, int pitIdx) {
        if (!started) {
            throw new IllegalStateException("Waiting for users");
        }
        if (!isPlayerTurn(player)) {
            throw new IllegalArgumentException("It's not your turn");
        }
        if (desk.getSeeds(player, pitIdx) == 0) {
            return this;
        }

        int lastProcessed = desk.processSeeds(player, pitIdx);
        notifyListeners();
        logger.debug("Player {} did a turn {} pit, desk after the turn - {}", player, pitIdx, Arrays.toString(desk.getDesk()));
        nextPlayerTurn = desk.isBasket(lastProcessed)? nextPlayerTurn : desk.nextPlayer(player);
        finished = testEndGame(desk);
        return this;
    }

    private boolean testEndGame(Desk desk) {
        if(desk.getSeedsOnDeskForPlayer(0)==0 ||
                desk.getSeedsOnDeskForPlayer(1)== 0){
            desk.processSeeds((idx, seeds) -> {
                if(!desk.isBasket(idx) && seeds>0){
                    desk.putIntoBasket(desk.getPitOwner(idx), idx);
                }
            });
            return true;
        }
        return false;
    }

    public boolean isStarted() {
        return started;
    }

    public boolean isFinished() {
        return finished;
    }

    public synchronized int getConfigPitsPerUser() {
        return desk.getPitsPerPlayer();
    }

    public synchronized void addGameListener(GameListener listener) {
        listeners.add(listener);
    }

    public synchronized void disconnect(int player){
        finish();
    }

    private void finish() {
        finished = true;
        notifyListeners();
    }

    protected Desk getDesk() {
        return desk;
    }
}
