package com.bol.mancala.bot;

import static org.junit.Assert.assertEquals;

import com.bol.mancala.Desk6x6;
import com.bol.mancala.Game;
import com.bol.mancala.GamePlayer;
import org.junit.Test;

public class BotsGameTest {

    @Test(timeout = 2000)
    public void test2BotsSync() {
        Game game = new Game(new Desk6x6());

        int totalSeeds = game.getSeedsOnTheDesk();
        System.out.println("Seeds on the desk - " + totalSeeds);

        GamePlayer user1 = game.registerForGame(new BotPlayer("bot1"));
        GamePlayer user2 = game.registerForGame(new BotPlayer("bot2"));

        System.out.println(
                String.format("Scores: %s - %d, %s - %d", user1.getName(), user1.getBasket(), user2.getName(),
                        user2.getBasket()));
        assertEquals(totalSeeds, user1.getBasket() + user2.getBasket());
    }

    class BotPlayer extends GamePlayer {

        public BotPlayer(String name) {
            super(name);
        }
        @Override
        public void stateChanged(Game game) {
            if(isMyTurn()) {
                int pitIdx = 0;
                int seeds = 0;
                while(seeds == 0) {
                    pitIdx = (int) (Math.random() * game.getConfigPitsPerUser());
                    seeds = getSeeds(pitIdx);
                }
                turn(pitIdx);
            }
        }
    }

}
