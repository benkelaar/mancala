package com.bol.mancala;

public interface GamePlayer {
    void turn(int pit);

    int getSeeds(int pit);

    int getBasket();

    boolean isMyTurn();

    int[] getMySeeds();

    int[] getOpponentSeeds();

    int getOpponentBasket();

    void disconnect();

    String getName();

    String getOpponentName();

    boolean isGameFinished();

    boolean isGameStarted();

    int getTotalSeeds();

    int getLastTurn();

    Game getGame();
}
