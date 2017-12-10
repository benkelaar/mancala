package com.bol.mancala;

/**
 * API for a player to access to a game.
 *
 */
public abstract class GamePlayer implements GameListener{

    private Game game;
    private int player;
    private String name;

    protected GamePlayer(String name) {
        this.name = name;
    }

    protected void setGame(Game game){
        this.game = game;
    }

    protected void setPlayer(int player){
        this.player = player;
    }

    public void turn(int pit) {
        this.game.turn(this, pit);
    }

    public int getSeeds(int pit){
        return this.game.getSeeds(this, pit);
    }

    public int getBasket() {
        return this.game.getBasket(getPlayer());
    }

    public boolean isMyTurn(){
        return this.game.isPlayerTurn(this);
    }

    public int[] getMySeeds(){
        return game.getPlayersPits(player);
    }

    public int[] getOpponentSeeds(){
        return game.getPlayersPits(game.opponentIdx(getPlayer()));
    }

    public int getOpponentBasket(){
        return game.getBasket(game.opponentIdx(getPlayer()));
    }

    public void disconnect() {
        game.disconnect(this);
    }

    public String getName() {
        return name;
    }

    @Override
    public final boolean equals(Object obj) {
        return super.equals(obj);
    }

    @Override
    public final int hashCode() {
        return super.hashCode();
    }

    public int getPlayer() {
        return player;
    }

    public String getOpponentName() {
        return game.getPlayerName(game.opponentIdx(player));
    }

    public boolean isGameFinished() {
        return game.isFinished();
    }

    public boolean isGameStarted() {
        return game.isStarted();
    }

    public int getTotalSeeds() {
        return game.getTotalSeeds();
    }
}
