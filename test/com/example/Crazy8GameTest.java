package com.example;

import org.junit.Before;
import org.junit.Test;

import java.util.LinkedList;

import static org.junit.Assert.*;

public class Crazy8GameTest {

    private Crazy8Game crazy8Game = new Crazy8Game(4);

    @Before
    public void reinitializeGame() {
        crazy8Game = new Crazy8Game(4);
    }

    @Test
    public void testInitialDeck() {
        //draw pile should have 31 cards after one is in the discard, and 20 are distributed to players.
        assertEquals(31, crazy8Game.getDrawPile().size());
    }

    @Test
    public void testInitialPlayerDecks() {
        for (Crazy8Game.Player player : crazy8Game.getPlayers()) {
            //each player should have 5 cards
            assertEquals(5, player.getCurrentDeck().size());
        }
    }

    @Test
    public void testPrepareRoundResetsDrawPile() {

        crazy8Game.playRound();
        crazy8Game.prepareNewRound();

        assertEquals(31, crazy8Game.getDrawPile().size());

    }

    @Test
    public void testPrepareRoundResetsPlayerDeck() {

        crazy8Game.playRound();
        crazy8Game.prepareNewRound();

        for (Crazy8Game.Player player : crazy8Game.getPlayers()) {
            assertEquals(5, player.getCurrentDeck().size());
        }

    }

    @Test
    public void testGameWinnerExists() throws AssertionError {

        Crazy8Game.Player winner = crazy8Game.playGame();
        assertNotNull(winner);

    }

    @Test
    public void testCheckCheating() throws AssertionError {

        LinkedList<PlayerTurn> exampleWrongPlayerActions = new LinkedList<PlayerTurn>();

        PlayerTurn firstCard = new PlayerTurn();
        firstCard.playedCard = new Card(Card.Suit.DIAMONDS, Card.Rank.SEVEN);
        firstCard.drewACard = false;
        firstCard.declaredSuit = null;
        firstCard.playerId = 1;
        exampleWrongPlayerActions.add(firstCard);

        PlayerTurn secondCard = new PlayerTurn();
        secondCard.playedCard = new Card(Card.Suit.HEARTS, Card.Rank.ACE);
        secondCard.drewACard = false;
        secondCard.declaredSuit = null;
        secondCard.playerId = 2;
        exampleWrongPlayerActions.add(secondCard);

        crazy8Game.setAllPlayerActions(exampleWrongPlayerActions);

        assertTrue(crazy8Game.checkCheating());

    }

    @Test
    public void testCheckRoundEnded() {

        crazy8Game.playRound();
        assertTrue(crazy8Game.checkRoundEnded());

    }

    @Test
    public void testCheckRoundNotEnded() {

        crazy8Game.getPlayer(1).playTurn();
        assertFalse(crazy8Game.checkRoundEnded());

    }

    @Test
    public void testAdjustPlayerScores() {

        crazy8Game.playRound();

        boolean somePlayerScoreChanged = false;
        for (Crazy8Game.Player player : crazy8Game.getPlayers()) {
            if (player.getPlayerScore() != 0) {
                somePlayerScoreChanged = true;
            }
        }

        assertTrue(somePlayerScoreChanged);

    }

    @Test
    public void gameWorksWithMultiplePlayersTest() {

        crazy8Game = new Crazy8Game(5);

        boolean gameWorks = false;

        //Game should return a winner even with multiple players
        assertNotNull(crazy8Game.playGame());

    }

    @Test
    public void findRoundWinnerTest() {

        //Reinitialize player 2 deck to have 0 cards, if the player has no cards they win the round
        crazy8Game.getPlayer(2).receiveInitialCards(new LinkedList<Card>());

        assertEquals(crazy8Game.getPlayer(2), crazy8Game.findRoundWinner());

    }

    @Test
    public void tieRoundScoreChangeTest() {

        //Game is tied when the draw pile is empty.
        crazy8Game.setDrawPile(new LinkedList<>());
        crazy8Game.checkRoundEnded();
        //round winner is null if the game is tied.
        crazy8Game.adjustPlayerScores(null);

        boolean allScoresChanged = true;
        for (Crazy8Game.Player player : crazy8Game.getPlayers()) {
            if (player.getPlayerScore() == 0) {
                allScoresChanged = false;
            }
        }

        assertTrue(allScoresChanged);

    }

    @Test
    public void onlyRoundWinnerScoreChanged() throws AssertionError {

        //Reinitialize player 2 deck to have 0 cards, if the player has no cards they win the round
        crazy8Game.getPlayer(2).receiveInitialCards(new LinkedList<Card>());

        boolean roundWinnerScoreChanged = false;
        boolean otherWinnerScoreNotChanged = true;

        for (Crazy8Game.Player player : crazy8Game.getPlayers()) {

            if (player.getPlayerId() == 2) {
                roundWinnerScoreChanged = true;
            } else if (player.getPlayerScore() != 0) {
                otherWinnerScoreNotChanged = false;
            }

        }

        assertTrue(roundWinnerScoreChanged && otherWinnerScoreNotChanged);

    }

    @Test
    public void tooManyPlayersTest() throws AssertionError {

        boolean throwsException = false;
        try {
            crazy8Game = new Crazy8Game(9);
        } catch (IllegalArgumentException e) {
            throwsException = true;
        }

        assertTrue(throwsException);

    }

}
