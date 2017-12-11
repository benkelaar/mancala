package com.bol.mancala;

/**
 * This class provides the API to access and modify game state for a player.
 *
 */
public interface GamePlayer {
    /**
     * Make a turn - lets process stones from pit
     * If the pit is empty - nothing will be happen.
     * If its not the players turn - there will be an error.
     *
     * @param pit pit's number
     * @throws IllegalStateException if the game isn't started
     * @throws IllegalArgumentException if it isn't player's turn
     */
    void turn(int pit) throws IllegalStateException, IllegalArgumentException;

    /**
     * Get number of seeds in the pit
     * @param pit pit index
     * @return number of seeds
     */
    int getSeeds(int pit);

    /**
     * Get number of seeds in the player's basket
     * @return number of seeds
     */
    int getBasket();

    /**
     * Check is it player's turn or not?
     * @return true - it's player's turn, false - otherwise
     */
    boolean isMyTurn();

    /**
     * Get the seeds for every player's pit.
     * @return an arrays of number of seeds in players pits [seeds_in_pit_1, ...]
     */
    int[] getMySeeds();

    /**
     * Get the seeds for every player's pit, but for opponent.
     * @return an arrays of number of seeds [seeds_in_pit_1, ...]
     */
    int[] getOpponentSeeds();

    /**
     * Get number of seeds in the opponent's basket.
     * @return number of seeds
     */
    int getOpponentBasket();

    /**
     * Disconnect from the game.
     */
    void disconnect();

    /**
     * Get current player's name
     * @return name
     */
    String getName();

    /**
     * Get opponent's name.
     * @return name
     */
    String getOpponentName();

    /**
     * Check if game is finished.
     * Game is finished in 2 cases:
     * - players reached the final turn
     * - someone has disconnected from the game
     *
     * @return true/false
     */
    boolean isGameFinished();

    /**
     * Check if game is started (there are 2 players)
     * @return try/false
     */
    boolean isGameStarted();

    /**
     * Total seeds on the desk.
     * @return number of seeds
     */
    int getTotalSeeds();

    /**
     * Get an index of a pit, which has been processed at the last turn.
     * This information might be helpful to update UI.
     * @return pitIdx from last turn
     */
    int getLastTurn();

    /**
     * Get the current game
     * @return game
     */
    Game getGame();
}
