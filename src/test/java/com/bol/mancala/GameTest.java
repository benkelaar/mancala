package com.bol.mancala;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

/**
 * Created by kopernik on 06/12/2017.
 */
public class GameTest {

    @Test(expected = IllegalStateException.class)
    public void testCannotPlayWithoutPlayers(){
        Game game = new Game(new Desk6x6());
        assertFalse(game.isStarted());
        game.turn(null,1);
    }

    @Test(expected = IllegalStateException.class)
    public void testCannotPlayAlong(){
        Game game = new Game(new Desk6x6());
        GamePlayer user1 = game.registerForGame(new DefaultGamePlayer("player1"));
        assertFalse(game.isStarted());
        user1.turn(1);
    }

    @Test
    public void testGameShouldBeOK(){
        Game game = new Game(new Desk6x6(), 0);
        GamePlayer user1 = game.registerForGame(new DefaultGamePlayer("player1"));
        GamePlayer user2 = game.registerForGame(new DefaultGamePlayer("player2"));
        assertTrue(game.isStarted());
        user1.turn(1);
        assertEquals(0, user1.getSeeds(1));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCannotDoSeveralTurns(){
        Game game = new Game(new Desk6x6(), 0);
        GamePlayer user1 = game.registerForGame(new DefaultGamePlayer("player1"));
        GamePlayer user2 = game.registerForGame(new DefaultGamePlayer("player2"));
        user1.turn(2);
        user1.turn(3);
    }

    @Test
    public void testCanDoSeveralTurnsIfLeaveInMyBasket(){
        Game game = new Game(new Desk6x6(), 0);
        GamePlayer user1 = game.registerForGame(new DefaultGamePlayer("player1"));
        GamePlayer user2 = game.registerForGame(new DefaultGamePlayer("player2"));
        user1.turn(0);
        user1.turn(1);
        assertEquals(2, user1.getBasket());
        assertEquals(0, user2.getBasket());
    }

}
