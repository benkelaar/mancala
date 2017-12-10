package com.bol.mancala;

import java.util.Arrays;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.IntStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by kopernik on 06/12/2017.
 */
public class Game {
    private static final Logger logger = LoggerFactory.getLogger(Game.class);
    private int firstTurn;

    private Desk6x6 desk;

    private volatile byte playersCount;
    private AtomicBoolean started = new AtomicBoolean();
    private AtomicBoolean finished = new AtomicBoolean();

    private ArrayBlockingQueue<GamePlayer> players = new ArrayBlockingQueue<>(2);

    public Game(Desk6x6 desk) {
        this(desk, (int)(Math.random() * desk.getMaxPlayers()));
    }

    public Game(Desk6x6 desk, int firstTurn) {
        this.desk = desk;
        this.firstTurn = firstTurn % desk.getMaxPlayers();
        players = new ArrayBlockingQueue<>(desk.getMaxPlayers());
    }

    public GamePlayer registerForGame(GamePlayer player) {
        if(!players.offer(player)) {
            throw new IllegalStateException("Game is fool");
        }
        player.setGame(this);
        player.setPlayer(playersCount++);
        if(!checkStarted()){
            player.stateChanged(this);
        }
        return player;
    }

    private boolean checkStarted() {
        boolean check = !started.get() && !finished.get() && players.size() == desk.getMaxPlayers();
        if(check && this.started.compareAndSet(false, true)){
            if(firstTurn>0){
                nextPlayerTurn();
            };
            notifyListeners();
            return true;
        }
        return false;
    }

    private void notifyListeners() {
        players.stream().forEach(x -> {
            try{
                x.stateChanged(this);
            }catch (RuntimeException e) {
                //its not our business, listener should'n produxe errors
                logger.warn("Some problem in listeners", e);
            }
        });
    }

    public boolean isPlayerTurn(GamePlayer player) {
        return started.get() && !finished.get() && players.peek() == player;
    }

    public int getSeedsOnTheDesk(){
        return desk.getTotalSeedsOnDesk();
    }

    protected int[] getPlayersPits(int player){
        return IntStream.range(0, desk.getPitsPerPlayer())
                .map(i -> desk.getSeeds(player, i))
                .toArray();
    }

    protected int opponentIdx(int player){
        return (++player) % desk.getMaxPlayers();
    }


    public int getSeeds(GamePlayer player, int pit){
        return desk.getSeeds(player.getPlayer(), pit);
    }

    public int getBasket(int player) {
        return desk.getBasket(player);
    }

    private void checkPlayer(GamePlayer player) {
        if(!players.contains(player)){
            throw new IllegalArgumentException(
                    String.format("Player '%s' do not play in this game", player.getName()));
        }
    }

    protected synchronized void turn(GamePlayer player, int pitIdx) {
        if (!started.get()) {
            throw new IllegalStateException("Waiting for users");
        }
        if (!isPlayerTurn(player)) {
            throw new IllegalArgumentException("It's not your turn");
        }
        if (desk.getSeeds(player.getPlayer(), pitIdx) == 0) {
            return;
        }

        int lastProcessed = desk.processSeeds(player.getPlayer(), pitIdx);

        logger.debug("Player {} did a turn {} pit, desk after the turn - {}",
                player.getName(),
                pitIdx,
                Arrays.toString(desk.getDesk()));

        if(!desk.isBasket(lastProcessed)) {
            nextPlayerTurn();
        }

        if(testEndGame(desk)){
            finished.set(true);
        }

        notifyListeners();
    }

    private void nextPlayerTurn() {
        players.add(players.poll());
    }

    private boolean testEndGame(Desk desk) {

        if(IntStream.range(0,desk.getMaxPlayers())
            .map(i -> desk.getSeedsOnDeskForPlayer(i))
            .anyMatch(seeds -> seeds==0)){
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
        return started.get();
    }

    public boolean isFinished() {
        return finished.get();
    }

    public int getConfigPitsPerUser() {
        return desk.getPitsPerPlayer();
    }

    public void disconnect(GamePlayer player){
        logger.debug("Player '{}' has disconnected from the game", player.getName());
        finish();
    }

    private void finish() {
        if(finished.compareAndSet(false, true)) {
            notifyListeners();
        }
    }

    protected Desk getDesk() {
        return desk;
    }

    public int getTotalSeeds() {
        return desk.getTotalSeedsOnDesk();
    }

    public String getPlayerName(int i) {
        return players.stream()
                .filter(player -> player.getPlayer()==i)
                .map(player -> player.getName())
                .findFirst().orElse("...");
    }
}
