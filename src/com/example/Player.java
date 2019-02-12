package com.example;

import java.util.LinkedList;

public class Player {

    LinkedList<Card> currentDeck;

    public LinkedList<Card> getCurrentDeck() {
        return currentDeck;
    }

    public void setCurrentDeck(LinkedList<Card> currentDeck) {
        this.currentDeck = currentDeck;
    }

}
