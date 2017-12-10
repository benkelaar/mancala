package com.bol.mancala;

import org.junit.Before;
import org.junit.Test;

import java.util.stream.IntStream;

import static org.junit.Assert.*;

/**
 * Created by kopernik on 05/12/2017.
 */
public class DeskTest {
    private Desk6x6 desk;

    @Before
    public void setup(){
        desk = new Desk6x6();
    }

    @Test
    public void testDefaultInitialStateShouldBeOK(){
        IntStream.range(0, 5).forEach(i -> {
            assertEquals(desk.getSeeds(0, i), 6);
            assertEquals(desk.getSeeds(1, i), 6);
        });
        assertEquals(desk.getBasket(0), 0);
        assertEquals(desk.getBasket(1), 0);
        assertArrayEquals(new int[]{
                6,6,6,6,6,6,0,
                6,6,6,6,6,6,0}, desk.getDesk());
    }

    @Test
    public void testCustomInitialStateShouldBeOK(){
        desk = new Desk6x6(10, 2);
        IntStream.range(0, 9).forEach(i -> {
            assertEquals(desk.getSeeds(0, i), 2);
            assertEquals(desk.getSeeds(1, i), 2);
        });
        assertEquals(desk.getBasket(0), 0);
        assertEquals(desk.getBasket(1), 0);
        assertArrayEquals(new int[]{
                2,2,2,2,2,2,2,2,2,2,0,
                2,2,2,2,2,2,2,2,2,2,0}, desk.getDesk());
    }

    @Test
    public void testMaxPlayersIs2Always(){
        assertEquals(2, desk.getMaxPlayers());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testWrongPitIdxLowerThanZero(){
        desk.getSeeds(0, -1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testWrongPitIdxGreaterThanMax(){
        desk.getSeeds(0, desk.getPitsPerPlayer());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetBasketForWrongPlayerShouldRaiseError(){
        desk.getBasket(2);
    }

    @Test
    public void testOppositePitIdxShouldBeOK(){
        // ordinal pits
        assertEquals(12, desk.getOppositePit(0));
        assertEquals(7, desk.getOppositePit(5));
        assertEquals(5, desk.getOppositePit(7));
        assertEquals(0, desk.getOppositePit(12));

        // baskets
        assertEquals(6, desk.getOppositePit(13));
        assertEquals(13, desk.getOppositePit(6));
    }

    @Test
    public void testProcessSeedsShouldBeCycled(){
        // initial state [6,6,6,6,6,6,0, 6,6,6,6,6,6,0]

        int lastIdx = desk.processSeeds(0, 0);
        //                >         |
        // initial state [0,7,7,7,7,7,1, 6,6,6,6,6,6,0]
        //                0 1 2 3 4 5 6  0 1 2 3 4 5 6
        assertEquals(6, lastIdx);
        assertEquals(0, desk.getSeeds(0, 0));
        IntStream.range(1,5).forEach(i -> assertEquals(7, desk.getSeeds(0, i)));
        assertEquals(1, desk.getBasket(0));
        assertEquals(6, desk.getSeeds(1, 0));

        lastIdx = desk.processSeeds(0, 4);
        //                        >              |
        // initial state [0,7,7,7,0,8,2, 7,7,7,7,7,6,0]
        //                0 1 2 3 4 5 6  0 1 2 3 4 5 6
        assertEquals(11, lastIdx);
        assertEquals(0, desk.getSeeds(0, 4));
        assertEquals(8, desk.getSeeds(0, 5));
        assertEquals(2, desk.getBasket(0));
        IntStream.range(0,4).forEach(i -> assertEquals(7, desk.getSeeds(1, i)));

        lastIdx = desk.processSeeds(1, 3);
        //                      |              >
        // initial state [1,8,8,8,0,8,2, 7,7,7,0,8,7,1]
        //                0 1 2 3 4 5 6  0 1 2 3 4 5 6
        assertEquals(3, lastIdx);
        assertEquals(0, desk.getSeeds(1, 3));
        assertEquals(8, desk.getSeeds(1, 4));
        assertEquals(7, desk.getSeeds(1, 5));
        assertEquals(1, desk.getBasket(1));
        assertEquals(1, desk.getSeeds(0, 0));
        IntStream.range(1,3).forEach(i -> assertEquals(8, desk.getSeeds(0, i)));
    }

    @Test
    public void testNoSeedShouldBeForOpponentBasket() {
        desk = new Desk6x6(new int[]{
                0, 1, 2, 3, 4, 10, 0,
                0, 1, 2, 3, 4, 5, 0});
        desk.processSeeds(0, 5);
        assertEquals(1, desk.getBasket(0));
        assertEquals(0, desk.getBasket(1));
        assertEquals(1, desk.getSeeds(0, 0));
    }

    @Test
    public void testLastSeedInEmptyPitShouldCaptureSeeds() {
        desk = new Desk6x6(new int[]{
                0, 1, 0, 3, 4, 5, 0,
                0, 1, 2, 3, 4, 5, 0});
        desk.processSeeds(0, 1);
        assertEquals(4, desk.getBasket(0));
        assertEquals(0, desk.getBasket(1));
        assertEquals(0, desk.getSeeds(1, 3));
    }

    @Test
    public void testLastSeedInBasketShouldntCaptureOpponentBasket() {
        //Funny case))
        desk = new Desk6x6(new int[]{
                0, 1, 2, 3, 4, 5, 0,
                0, 1, 2, 3, 4, 5, 6});
        desk.processSeeds(0, 3);
        assertEquals(1, desk.getBasket(0));
        assertEquals(6, desk.getBasket(1));
    }

    @Test
    public void testLastSeedOnOppositeSideShouldntCaptureSeeds() {
        desk = new Desk6x6(new int[]{
                0, 0, 0, 0, 0, 0, 0,
                0, 1, 2, 3, 4, 5, 0});
        desk.processSeeds(1, 4);
        assertEquals(0, desk.getBasket(0));
        assertEquals(1, desk.getBasket(1));
        assertEquals(1, desk.getSeeds(0, 1));
        assertEquals(1, desk.getSeeds(1, 1));
    }

}