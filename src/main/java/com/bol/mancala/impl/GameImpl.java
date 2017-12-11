package com.bol.mancala.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.IntStream;

import com.bol.mancala.Desk;
import com.bol.mancala.Game;
import com.bol.mancala.GameListener;
import com.bol.mancala.GamePlayer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Game implementation.
 * As the main purpose of the class is to synchronize player's calls we use
 * synchronized methods to do this. There shouldn't be high load here as only 2 users play
 * on a single game.
 *
 * @author nbogdanov
 */
public class GameImpl implements Game {
    private static final Logger logger = LoggerFactory.getLogger(GameImpl.class);

    // desk to play
    private Desk desk;

    // index of a player from whom we expect the next turn
    private int nextPlayerTurn = 0;

    //current players number
    private int playersCount = 0;

    // index of pit from previous turn
    private int previousPitIdx;

    private boolean started = false;
    private boolean finished = false;

    private List<GameListener> listeners = new CopyOnWriteArrayList<>();
    // because we don't store the players objects we need to store their names somewhere
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

    /**
     * Extension point to use different implementations for {@link GamePlayer}
     * @param name player's name
     * @param playerId player's index
     * @return new player's object
     */
    private GamePlayer createGamePlayer(String name, int playerId) {
        return new GamePlayerImpl(this, name, playerId);
    }

    /**
     * Check game is started.
     * Also will notify listeners if state is changed.
     *
     * @return true/false
     */
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

    /**
     * Make a turn for a player.
     *
     * @param playerId player index
     * @param pitIdx   pit index to process (if there are no seeds - nothing happens)
     */
    protected synchronized void turn(int playerId, int pitIdx) throws IllegalStateException, IllegalArgumentException{
        if (!started) {
            throw new IllegalStateException("Waiting for users");
        }
        if (!isPlayerTurn(playerId)) {
            throw new IllegalArgumentException("It's not your turn");
        }
        if (desk.getSeeds(playerId, pitIdx) == 0) {
            return;
        }
        // lets save pit index
        previousPitIdx = pitIdx;

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

    protected synchronized int getPreviousPitIdx(){
        return previousPitIdx;
    }

    /**
     * Test game is end.
     * There should be a player without seeds on his desk.
     * @param desk desk
     * @return true - the game is over, false - otherwise
     */
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
