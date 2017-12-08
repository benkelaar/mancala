package com.bol.mancala.web;

public class GameState {
    private String message;

    public GameState() {
    }

    public GameState(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
