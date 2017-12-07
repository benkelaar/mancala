package com.bol.mancala;

import org.junit.Before;
import org.junit.Test;

import java.util.stream.IntStream;

import static org.junit.Assert.*;

/**
 * Created by kopernik on 06/12/2017.
 */
public class GameTest {
//    public class DeskTest {
//        private Game game;
//
//        @Before
//        public void setup(){
//            game = new Game();
//        }
//
//        @Test
//        public void testGameInitialState(){
//            assertTrue(game.isPlayerTurn(FIRST) ^ game.isPlayerTurn(SECOND));
//            IntStream.range(0, 5).forEach(i -> {
//                assertEquals(game.getSeeds(FIRST, i), 6);
//                assertEquals(game.getSeeds(SECOND, i), 6);
//            });
//            assertEquals(game.getBasket(FIRST), 0);
//            assertEquals(game.getBasket(SECOND), 0);
//        }
//
//        @Test(expected = IllegalArgumentException.class)
//        public void testWrongPitIdxLowerThanZero(){
//            game.getSeeds(SECOND, -1);
//        }
//
//        @Test(expected = IllegalArgumentException.class)
//        public void testWrongPitIdxGreaterThanFive(){
//            game.getSeeds(FIRST, 6);
//        }
//
//        @Test
//        public void testLastSeedInEnemyPit(){
//            Desk game = new Desk(true, Desk.DEFAULT_DESK);
//            assertTrue(game.isPlayerTurn(FIRST));
//            game.turn(FIRST, 1);
//            assertFalse(game.isPlayerTurn(FIRST));
//            assertEquals(6, game.getSeeds(FIRST, 0));
//            assertEquals(0, game.getSeeds(FIRST, 1));
//            IntStream.range(2,5).forEach(i ->
//                    assertEquals(7, game.getSeeds(FIRST, i)));
//            assertEquals(1, game.getBasket(FIRST));
//            assertEquals(0, game.getBasket(SECOND));
//        }
//
//        @Test
//        public void testLastSeedInBasket(){
//            Desk game = new Desk(true, Desk.DEFAULT_DESK);
//            assertTrue(game.isPlayerTurn(FIRST));
//            game.turn(FIRST, 0);
//            //it still a first players turn
//            assertTrue(game.isPlayerTurn(FIRST));
//            assertArrayEquals(new byte[]{0,7,7,7,7,7,1,6,6,6,6,6,6,0}, game.getDesk());
//
//        }
//    }
}
