package com.bol.mancala.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
public class GameController {
    private static final Logger logger = LoggerFactory.getLogger(GameController.class);

    @MessageMapping("/turn")
    @SendTo("/queue/status")
    public GameState turn(TurnMessage message) throws Exception {
        logger.info("Got message "+message);
        Thread.sleep(1000); // simulated delay

        return new GameState("Hello, " + message.getPit() + "!");
    }
}
