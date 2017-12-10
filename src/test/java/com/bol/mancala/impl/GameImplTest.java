package com.bol.mancala.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import com.bol.mancala.Game;
import com.bol.mancala.GamePlayer;
import org.junit.Test;

/**
 * Created by kopernik on 06/12/2017.
 */
public class GameImplTest {

    @Test(expected = IllegalStateException.class)
    public void testCannotPlayWithoutPlayers(){
        GameImpl game = new GameImpl(new DeskImpl());
        assertFalse(game.isStarted());
        game.turn(0,1);
    }

    @Test(expected = IllegalStateException.class)
    public void testCannotPlayAlong(){
        Game game = new GameImpl(new DeskImpl());
        GamePlayer user1 = game.registerForGame("player1");
        assertFalse(game.isStarted());
        user1.turn(1);
    }

    @Test
    public void testGameShouldBeOK(){
        Game game = new GameImpl(new DeskImpl(), 0);
        GamePlayer user1 = game.registerForGame("player1");
        GamePlayer user2 = game.registerForGame("player2");

        assertTrue(game.isStarted());
        assertEquals(6, game.getPitsCountPerPlayer());
        user1.turn(1);
        assertEquals(0, user1.getSeeds(1));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCannotDoSeveralTurns(){
        Game game = new GameImpl(new DeskImpl(), 0);
        GamePlayer user1 = game.registerForGame("player1");
        GamePlayer user2 = game.registerForGame("player2");
        user1.turn(2);
        user1.turn(3);
    }

    @Test
    public void testCanDoSeveralTurnsIfLeaveInMyBasket(){
        Game game = new GameImpl(new DeskImpl(), 0);
        GamePlayer user1 = game.registerForGame("player1");
        GamePlayer user2 = game.registerForGame("player2");
        user1.turn(0);
        user1.turn(1);
        assertEquals(2, user1.getBasket());
        assertEquals(0, user2.getBasket());
    }

    @Test
    public void testDisconnectShouldFinishGame(){
        GameImpl game = new GameImpl(new DeskImpl(), 0);
        GamePlayer user1 = game.registerForGame("player1");
        GamePlayer user2 = game.registerForGame("player2");

        assertEquals(true, game.isStarted());
        assertEquals(false, game.isFinished());
        user1.disconnect();
        assertEquals(true, game.isFinished());
    }

}
