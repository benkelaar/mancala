package com.bol.mancala;

/**
 * Created by kopernik on 06/12/2017.
 */
public class Game {
    private final Desk desk;
    private int nextPlayerTurn;
    private boolean gameStarted = false;
    private boolean gameFinished = false;

    private GameUser firstUser = null;
    private GameUser secondUser = null;

    public Game(Desk desk) {
        this(desk, (int)(Math.random() * 2));
    }

    public Game(Desk desk, int firstTurn) {
        this.desk = desk;
        nextPlayerTurn = firstTurn;
    }

    public GameUser registerForGame(String name) {
        if (firstUser == null) {
            firstUser = new GameUser(this, 0, name);
            gameStarted = firstUser != null && secondUser != null;
            return firstUser;
        } else if (secondUser == null) {
            secondUser = new GameUser(this, 1, name);
            gameStarted = firstUser != null && secondUser != null;
            return secondUser;
        }
        throw new IllegalStateException("Game is fool");
    }

    public boolean isPlayerTurn(int player) {
        return nextPlayerTurn == player;
    }

    public int getSeeds(int player, int pit){
        return desk.getSeeds(player, pit);
    }

    public int getBasket(int player) {
        return desk.getBasket(player);
    }

    protected Game turn(int player, int pitIdx) {
        if (!gameStarted) {
            throw new IllegalStateException("Waiting for users");
        }
        if (!isPlayerTurn(player)) {
            throw new IllegalArgumentException("It's not your turn");
        }
        if (desk.getSeeds(player, pitIdx) == 0) {
            return this;
        }

        int lastProcessed = desk.processSeeds(player, pitIdx);

        nextPlayerTurn = desk.isBasket(lastProcessed)? nextPlayerTurn : desk.nextPlayer(player);
        gameFinished = testEndGame(desk);
        return this;
    }

    private boolean testEndGame(Desk desk) {
        if(desk.getSeedsOnDeskForPlayer(0)==0 ||
                desk.getSeedsOnDeskForPlayer(1)== 0){
            return true;
        }
        return false;
    }
}
