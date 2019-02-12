package com.example;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.Random;

public class Crazy8Game {

    private LinkedList<Card> drawPile;

    private LinkedList<Card> discardDeck;

    private Player[] players;

    private static final int NUM_SUITS = 4;

    private static final int NUM_RANKS = 13;

    public Crazy8Game() {

        //initializing fields
        this.drawPile = new LinkedList<>();
        this.discardDeck = new LinkedList<>();
        this.players = new Player[4];

        //Creating a standard card deck
        for (int i = 0; i < NUM_SUITS; i++) {
            for (int j = 0; j < NUM_RANKS; j++) {
                drawPile.add(new Card(Card.Suit.values()[i], Card.Rank.values()[j]));
            }
        }

        //Shuffling the deck
        Collections.shuffle(drawPile);

        //Give each player 5 random cards
        for (int player = 0; player < players.length; player++) {
            players[player] = new Player();

            LinkedList<Card> playerDeck = new LinkedList<>();

            //Take 5 cards from draw pile
            for (int i = 0; i < 5; i++) {
                playerDeck.add(drawCard());
            }

            players[player].setCurrentDeck(playerDeck);
        }

    }

    private Card drawCard() {

        Card drawnCard = drawPile.get(0);
        drawPile.remove(0);
        return drawnCard;

    }


}
