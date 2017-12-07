package com.bol.mancala;

/**
 * Created by kopernik on 06/12/2017.
 */
public class Game {
    private final Desk desk;
    private int nextPlayerTurn;

    public Game(Desk desk) {
        this.desk = desk;
        nextPlayerTurn = 0;
    }

    public boolean isPlayerTurn(int player) {
        return nextPlayerTurn == player;
    }

    public Game turn(int player, int pitIdx) {
        if (!isPlayerTurn(player)) {
            throw new IllegalArgumentException("It's not your turn");
        }
        if (desk.getSeeds(player, pitIdx) == 0) {
            throw new IllegalArgumentException("Empty pit.Choose another one.");
        }

        int lastProcessed = desk.processSeeds(player, pitIdx);
        if (desk.getPitOwner(lastProcessed) == player && desk.getSeeds(lastProcessed) == 1) {
            desk.putIntoBasket(player, lastProcessed);
            desk.putIntoBasket(player, desk.getOppositePit(lastProcessed));
        }
        if (desk.getBasket(player) != lastProcessed) {
            nextPlayerTurn = desk.nextPlayer(player);
        }
        return this;
    }
}
