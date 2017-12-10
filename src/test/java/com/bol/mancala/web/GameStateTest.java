package com.bol.mancala.web;

import com.bol.mancala.GamePlayer;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

public class GameStateTest {

    @Test
    public void testCreate(){
        GamePlayer player = mock(GamePlayer.class);
        doReturn(true).when(player).isMyTurn();
        doReturn(true).when(player).isGameStarted();
        doReturn(false).when(player).isGameFinished();
        doReturn("bot").when(player).getName();
        doReturn("notbot").when(player).getOpponentName();
        doReturn(34).when(player).getBasket();
        doReturn(27).when(player).getOpponentBasket();
        doReturn(new int[]{1,2,3,4,5,6}).when(player).getMySeeds();
        doReturn(new int[]{6,5,4,3,2,1}).when(player).getOpponentSeeds();
        doReturn(72).when(player).getTotalSeeds();

        GameState state = new GameState(new RuntimeException("Exception"), player);
        assertEquals("Exception", state.getError());
        assertEquals(player.isMyTurn(), state.isYourTurn());
        assertEquals(player.isGameStarted(), state.isGameStarted());
        assertEquals(player.isGameFinished(), state.isGameFinished());
        assertEquals(player.getName(), state.getYourName());
        assertEquals(player.getOpponentName(), state.getOpponentName());
        assertEquals(player.getBasket(), state.getYourBasket());
        assertEquals(player.getOpponentBasket(), state.getOpponentBasket());
        assertArrayEquals(player.getMySeeds(), state.getYourSeeds());
        assertArrayEquals(player.getOpponentSeeds(), state.getOpponentSeeds());
        assertEquals(player.getTotalSeeds(), state.getTotalSeeds());
    }
}