package com.bol.mancala;

import org.junit.Before;
import org.junit.Test;

import java.util.stream.IntStream;

import static org.junit.Assert.*;

/**
 * Created by kopernik on 05/12/2017.
 */
public class DeskTest {
    private Desk desk;

    @Before
    public void setup(){
        desk = new Desk();
    }

    @Test
    public void testDeskDefultInitialState(){
        IntStream.range(0, 5).forEach(i -> {
            assertEquals(desk.getSeeds(0, i), 6);
            assertEquals(desk.getSeeds(1, i), 6);
        });
        assertEquals(desk.getBasket(0), 0);
        assertEquals(desk.getBasket(1), 0);
    }

    @Test
    public void testDeskCustomInitialState(){
        desk = new Desk(10, 2);
        IntStream.range(0, 9).forEach(i -> {
            assertEquals(desk.getSeeds(0, i), 2);
            assertEquals(desk.getSeeds(1, i), 2);
        });
        assertEquals(desk.getBasket(0), 0);
        assertEquals(desk.getBasket(1), 0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testWrongPitIdxLowerThanZero(){
        desk.getSeeds(0, -1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testWrongPitIdxGreaterThanMax(){
        desk.getSeeds(0, desk.getPitsPerPlayer());
    }


}