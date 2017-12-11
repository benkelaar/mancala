package com.bol.mancala.impl;

import com.bol.mancala.Game;
import com.bol.mancala.GamePlayer;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class GamePlayerImplTest {
    private GameImpl game = mock(GameImpl.class);
    private GamePlayerImpl player = new GamePlayerImpl(game, "name", 0);

    @Test
    public void testMySeedsShouldOK(){
        player.getMySeeds();
        verify(game, only()).getPlayersPits(eq(0));
    }

    @Test
    public void testGetOpponentSeedsShouldOK(){
        doReturn(1).when(game).opponentIdx(eq(0));

        player.getOpponentSeeds();
        verify(game, times(1)).opponentIdx(eq(0));
        verify(game, times(1)).getPlayersPits(eq(1));
    }

    @Test
    public void testGetOpponentNameIfThereIsNoOpponent(){
        Game game = new GameImpl(new DeskImpl());
        GamePlayer player = game.registerForGame("player1");

        assertEquals("player1", player.getName());
        assertEquals(null, player.getOpponentName());
    }

}