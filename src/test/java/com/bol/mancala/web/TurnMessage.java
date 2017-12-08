package com.bol.mancala.web;

public class TurnMessage {
    private int pit;

    public TurnMessage() {
    }

    public TurnMessage(int pit) {
        this.pit = pit;
    }

    public int getPit() {
        return pit;
    }

    public void setPit(int pit) {
        this.pit = pit;
    }

    @Override
    public String toString() {
        return "TurnMessage{" +
                "pit=" + pit +
                '}';
    }
}
