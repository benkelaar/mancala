package com.bol.mancala;

public class GameUser {

    private Game game;
    private int player;
    private String name;

    protected GameUser(Game game, int player, String name) {
        this.game = game;
        this.player = player;
        this.name = name;
    }
}
