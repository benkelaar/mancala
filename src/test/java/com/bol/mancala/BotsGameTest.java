package com.bol.mancala;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class BotsGameTest {

    @Test(timeout = 1000)
    public void test2Bots() {
        Game game = new Game(new Desk(), 0);
        GameUser user1 = game.registerForGame("player1");
        GameUser user2 = game.registerForGame("player2");
        int totalSeeds = game.getSeedsOnTheDesk();
        System.out.println("Seeds on the desk - " + totalSeeds);
        GameUser user = user1.isMyTurn() ? user1 : user2;
        while(!game.isFinished()) {
            int pitIdx = 0;
            int seeds = 0;
            while(seeds == 0) {
                pitIdx = (int) (Math.random() * game.getConfigPitsPerUser());
                seeds = user.getSeeds(pitIdx);
            }
            user.turn(pitIdx);
            if(!user.isMyTurn()) {
                user = user == user1 ? user2 : user1;
            }
        }
        System.out.println(
                String.format("Scores: %s - %d, %s - %d", user1.getName(), user1.getBasket(), user2.getName(),
                        user2.getBasket()));
        assertEquals(totalSeeds, user1.getBasket() + user2.getBasket());
    }

}
