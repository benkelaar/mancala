package com.bol.mancala.web;

import com.bol.mancala.GamePlayer;

/**
 * Simple game state object to send updated to user's side.
 * This class will be serialized by Json.
 */
public class GameState {
    // use this field to send any errors descriptions to user
    private String error = "";

    // the rest field describe the current game's state
    private boolean gameStarted = false;
    private boolean gameFinished = false;
    private boolean yourTurn = false;
    private String yourName;
    private String opponentName;
    private int totalSeeds;
    private int[] yourSeeds;
    private int yourBasket;
    private int[] opponentSeeds;
    private int opponentBasket;
    private int lastTurn;

    public GameState(Throwable exception) {
        this.error = exception.getLocalizedMessage();
    }

    public GameState(Throwable error, GamePlayer user) {
        this(user);
        this.error = error.getLocalizedMessage();
    }

    public GameState(GamePlayer player) {
        if (player != null) {
            this.gameFinished = player.isGameFinished();
            this.gameStarted = player.isGameStarted();
            this.yourTurn = player.isMyTurn();
            this.yourName = player.getName();
            this.opponentName = player.getOpponentName() == null ?
                    "Waiting for second player..." :
                    player.getOpponentName();
            this.yourSeeds = player.getMySeeds();
            this.yourBasket = player.getBasket();
            this.opponentSeeds = player.getOpponentSeeds();
            this.opponentBasket = player.getOpponentBasket();
            this.totalSeeds = player.getTotalSeeds();
            this.lastTurn = player.getLastTurn();
        }
    }

    public String getError() {
        return error;
    }

    public boolean isGameStarted() {
        return gameStarted;
    }

    public boolean isGameFinished() {
        return gameFinished;
    }

    public boolean isYourTurn() {
        return yourTurn;
    }

    public int getTotalSeeds() {
        return totalSeeds;
    }

    public int[] getYourSeeds() {
        return yourSeeds;
    }

    public int getYourBasket() {
        return yourBasket;
    }

    public int[] getOpponentSeeds() {
        return opponentSeeds;
    }

    public int getOpponentBasket() {
        return opponentBasket;
    }

    public String getYourName() {
        return yourName;
    }

    public String getOpponentName() {
        return opponentName;
    }

    public int getLastTurn() {
        return lastTurn;
    }

}