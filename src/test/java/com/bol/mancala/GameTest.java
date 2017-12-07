package com.bol.mancala;

import org.junit.Test;

/**
 * Created by kopernik on 06/12/2017.
 */
public class GameTest {

    @Test(expected = IllegalStateException.class)
    public void testCannotPlayWithoutPlayers(){
        Game game = new Game(new Desk());
        game.turn(0,1);
    }

    @Test(expected = IllegalStateException.class)
    public void testCannotPlayAlong(){
        Game game = new Game(new Desk());
        game.registerForGame("player1");
        game.turn(0,1);
    }

    @Test
    public void testGameShoulBeOK(){
        Game game = new Game(new Desk(), 0);
        game.registerForGame("player1");
        game.registerForGame("player2");
        game.turn(0,1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCannotDoSeveralTurns(){
        Game game = new Game(new Desk(), 0);
        game.registerForGame("player1");
        game.registerForGame("player2");
        game.turn(0,2);
        game.turn(0,3);
    }

    @Test
    public void testCanDoSeveralTurnsIfLeaveInMyBasket(){
        Game game = new Game(new Desk(), 0);
        game.registerForGame("player1");
        game.registerForGame("player2");
        game.turn(0,0);
        game.turn(0,1);
    }
}
