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

    public void turn(int pit) {
        this.game.turn(player, pit);
    }

    public int getSeeds(int pit){
        return this.game.getSeeds(player, pit);
    }

    public int getBasket() {
        return this.game.getBasket(player);
    }

    public boolean isMyTurn(){
        return this.game.isPlayerTurn(player);
    }

    public String getName() {
        return name;
    }

    public void addGameListener(GameListener listener) {
        game.addGameListener(listener);
    }

    public void disconnect() {
        game.disconnect(player);
    }
}
