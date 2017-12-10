package com.bol.mancala.impl;

import com.bol.mancala.Game;
import com.bol.mancala.GamePlayer;

/**
 * API for a player to access to a game.
 *
 */
public class GamePlayerImpl implements GamePlayer {

    private GameImpl game;
    private int player;
    private String name;

    protected GamePlayerImpl(GameImpl game, String name, int idx) {
        this.game = game;
        this.name = name;
        this.player = idx;
    }

    @Override
    public void turn(int pit) {
        game.turn(getPlayerId(), pit);
    }

    @Override
    public int getSeeds(int pit){
        return game.getSeeds(getPlayerId(), pit);
    }

    @Override
    public int getBasket() {
        return game.getBasket(getPlayerId());
    }

    @Override
    public boolean isMyTurn(){
        return game.isPlayerTurn(getPlayerId());
    }

    @Override
    public int[] getMySeeds(){
        return game.getPlayersPits(player);
    }

    @Override
    public int[] getOpponentSeeds(){
        return game.getPlayersPits(game.opponentIdx(getPlayerId()));
    }

    @Override
    public int getOpponentBasket(){
        return game.getBasket(game.opponentIdx(getPlayerId()));
    }

    @Override
    public void disconnect() {
        game.disconnect(getPlayerId());
    }

    @Override
    public String getName() {
        return name;
    }

    public int getPlayerId() {
        return player;
    }

    @Override
    public String getOpponentName() {
        return game.getPlayerName(game.opponentIdx(getPlayerId()));
    }

    @Override
    public boolean isGameFinished() {
        return game.isFinished();
    }

    @Override
    public boolean isGameStarted() {
        return game.isStarted();
    }

    @Override
    public int getTotalSeeds() {
        return game.getTotalSeeds();
    }

    @Override
    public int getLastTurn(){
        return game.lastTurn();
    }

    @Override
    public Game getGame() {
        return game;
    }
}
