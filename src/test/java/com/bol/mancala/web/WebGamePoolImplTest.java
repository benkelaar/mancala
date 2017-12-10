package com.bol.mancala.web;

import com.bol.mancala.Game;
import com.bol.mancala.GamePlayer;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

public class WebGamePoolImplTest {
    private WebGamePoolImpl pool = spy(new WebGamePoolImpl());

    @Test
    public void testPoolShouldCreateGameForFirstPlayer(){
        GamePlayer server = pool.createGameForUser("1", "player");
        verify(pool, times(1)).createGame();
        assertEquals("player", server.getName());
        assertEquals("player", pool.findUser("1").getName());
    }

    @Test
    public void testPoolShouldUseGameForSecondPlayer(){
        GamePlayer first = pool.createGameForUser("1", "player1");
        reset(pool);
        GamePlayer second = pool.createGameForUser("2", "player2");
        verify(pool, times(0)).createGame();
        assertEquals("player1", pool.findUser("1").getName());
        assertEquals("player2", pool.findUser("2").getName());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testFindShouldRaiseErrorIfNoPlayer(){
        pool.findUser("wrong");
    }

    @Test
    public void testCleanupShouldDisconnectFromGame(){
        Game gameMock = mock(Game.class);
        GamePlayer playerMock = mock(GamePlayer.class);
        doReturn(playerMock).when(gameMock).registerForGame(anyString());

        WebGamePoolImpl pool = new WebGamePoolImpl() {
            @Override
            protected Game createGame() {
                return gameMock;
            }
        };
        GamePlayer player = pool.createGameForUser("1", "player");

        //cleanup
        pool.cleanupUserData("1");
        verify(player, times(1)).disconnect();
    }

    @Test
    public void testCleanupShouldntRaiseErrors(){
        pool.cleanupUserData("wrong");
    }
}