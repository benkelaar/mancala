package com.bol.mancala.web;

import com.bol.mancala.Desk;
import com.bol.mancala.Game;
import com.bol.mancala.GameUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@Scope("singleton")
public class WebGamePool {
    private static final Logger logger = LoggerFactory.getLogger(WebGamePool.class);
    private Game game = null;
    private Map<String, GameUser> users = new HashMap<>();

    public synchronized GameUser findOrCreateGameForUser(String id){
        logger.info("POOL");
        GameUser user = users.get(id);
        if(user!=null){
            return user;
        }
        if(game == null){
            game = new Game(new Desk());
        }
        user = game.registerForGame(id);
        users.put(id, user);
        return user;
    }

    public void cleanupUserData(String id) {
        GameUser user = users.remove(id);
        if(user!=null){
            user.disconnect();
        }
        game = null;
    }
}
