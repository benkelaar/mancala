package com.bol.mancala.bot;

import static org.junit.Assert.assertEquals;

import com.bol.mancala.GameListener;
import com.bol.mancala.impl.DeskImpl;
import com.bol.mancala.impl.GameImpl;
import com.bol.mancala.GamePlayer;
import com.bol.mancala.impl.GamePlayerImpl;
import org.junit.Test;

public class GameSyncTest {

    @Test(timeout = 2000)
    public void test2BotsSync() {
        DeskImpl desk = new DeskImpl();
        GameImpl game = new GameImpl(desk);

        int pitsPerPlayer = desk.getPitsPerPlayer();
        int totalSeeds = desk.getTotalSeedsOnDesk();

        System.out.println("Seeds on the desk - " + totalSeeds);

        GamePlayer user1 = game.registerForGame("bot1");
        GamePlayer user2 = game.registerForGame("bot2");

        game.addGameListener(() -> {
            if(user1.isMyTurn()){
                generateSomeTurn(user1, pitsPerPlayer);
                return;
            }
            if(user2.isMyTurn()) {
                generateSomeTurn(user2, pitsPerPlayer);
                return;
            }
        });

        if(user1.isMyTurn()){
            generateSomeTurn(user1, pitsPerPlayer);
        } else {
            generateSomeTurn(user2, pitsPerPlayer);
        }

        System.out.println(
                String.format("Scores: %s - %d, %s - %d", user1.getName(), user1.getBasket(), user2.getName(),
                        user2.getBasket()));
        assertEquals(totalSeeds, user1.getBasket() + user2.getBasket());
    }

    private void generateSomeTurn(GamePlayer player, int pitsPerPlayer) {
        int pitIdx = 0;
        int seeds = 0;
        while (seeds == 0) {
            pitIdx = (int) (Math.random() * pitsPerPlayer);
            seeds = player.getSeeds(pitIdx);
        }
        player.turn(pitIdx);
    }

}
