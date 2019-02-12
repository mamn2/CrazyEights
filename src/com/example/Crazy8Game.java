package com.example;

import java.util.*;
import java.util.List;


public class Crazy8Game {

    private List<Card> drawPile;
    private List<Card> discardDeck;
    private Player[] players;
    private Card.Suit currentSuit;
    private Card.Rank currentRank;
    private LinkedList<PlayerTurn> allPlayerActions = new LinkedList<>();

    /**
     * Constructor for a new Crazy 8 game.
     * Gets a card deck, shuffles, distributes 5 cards to each player, and places first card in discard pile.
     */
    public Crazy8Game(int numPlayers) throws IllegalArgumentException {

        if (numPlayers > 8) {
            throw new IllegalArgumentException("Too many players");
        }

        //initializing fields
        this.players = new Player[numPlayers];

        //Initialize each player
        for (int playerNum = 0; playerNum < players.length; playerNum++) {

            players[playerNum] = new Player();

            //get the IDs for the other players
            List<Integer> otherPlayerIDs = new LinkedList<>();
            for (int otherPlayerNum = 0; otherPlayerNum < players.length; otherPlayerNum++) {
                if (otherPlayerNum != playerNum) {
                    otherPlayerIDs.add(otherPlayerNum + 1);
                }
            }

            //initialize the player by setting its player number and giving it otherPlayerIDs
            players[playerNum].init(playerNum + 1, otherPlayerIDs);

        }

        prepareNewRound();

    }

    /**
     * Automatically plays a Crazy 8 Game
     * @return the winner of the game.
     */
    public Player playGame() {

        Player winner = null;

        while (winner == null) {

            playRound();
            winner = findWinner();

            if (winner != null) {
                System.out.println("\n--------------------------------------");
                System.out.println("CONGRATULATIONS PLAYER " + winner.getPlayerId() + ", YOU HAVE WON THE GAME");
                return winner;
            }

            prepareNewRound();

        }

        return winner;

    }

    /**
     * Plays a round of Crazy 8s and modifies the player scores accordingly
     * @return the winner for the round, null if the round was a tie.
     * Exits the game if there is a cheater detected.
     */
    public Player playRound() {

        Player roundWinner = null;

        boolean roundEnded = false;
        while (!roundEnded) {
            for (int playerNum = 1; playerNum <= players.length; playerNum++) {
                getPlayer(playerNum).playTurn();
                //Check if the players turn caused the round to end
                roundEnded = checkRoundEnded();
                if (roundEnded) {
                    System.out.println("--------------------------------------");
                    System.out.println("THIS ROUND HAS ENDED");
                    break;
                } else if (checkCheating()) {
                    System.out.println("The game has ended because someone cheated.");
                    System.exit(0);
                    return null;
                }
            }
        }

        //Now that the round ended, we need to add points based on a tie game or someone winning
        adjustPlayerScores(findRoundWinner());

        return roundWinner;

    }

    /**
     * Adjusts the player scores after the round is over
     * @param roundWinner the winner of the round if there is one, null if the round was a tie.
     */
    public void adjustPlayerScores(Player roundWinner) {

        if (roundWinner == null) {
            //In this case of a tie game, add points of all opponents cards for each player
            for (Player player : getPlayers()) {
                player.setPlayerScore(player.getPlayerScore() + player.sumOfOtherPlayerCards());
            }
            System.out.println("The round was a tie.");
            //returns null if there is not yet a winner
        } else {
            //in the case of one round winner, add points of all opponents card for only that winner
            roundWinner = findRoundWinner();
            roundWinner.setPlayerScore(roundWinner.getPlayerScore() + roundWinner.sumOfOtherPlayerCards());

            System.out.println("The winner of this round is Player " + roundWinner.getPlayerId());
            System.out.println("--------------------------------------");
            //returns null if there is not yet a winner

        }

    }

    /**
     * Getter for all the players in the game.
     * @return all the players in the game.
     */
    public Player[] getPlayers() {
        return players;
    }

    /**
     * Gets one of the players in the players array
     * @param playerId is the player number. Ex: 1 is player 1, but index 0 in the player array.
     * @return a player with the given player ID.
     */
    public Player getPlayer(int playerId) {
        return players[playerId - 1];
    }

    /**
     * Gets the current drawPile
     * @return drawPile
     */
    public List<Card> getDrawPile() {
        return drawPile;
    }

    /**
     * Sets a draw pile to a different drawpile, used only for testing purposes
     * @param drawPile is the new drawpile.
     */
    public void setDrawPile(LinkedList<Card> drawPile) {

        this.drawPile = drawPile;

    }

    /**
     * Used only for testing purposes, Sets all player actions.
     * @param allPlayerActions player actions list.
     */
    public void setAllPlayerActions(LinkedList<PlayerTurn> allPlayerActions) {
        this.allPlayerActions = allPlayerActions;
    }

    /**
     * Searches if the game has a winner (person with over 200 points).
     * If there are multiple with over 200 points, the person with the highest number wins.
     * @return winner of the game
     */
    public Player findWinner() {

        int highestScore = 0;
        Player playerWithHighestScore = null;

        for (Player player : players) {
            if (player.getPlayerScore() > highestScore) {
                highestScore = player.getPlayerScore();
                playerWithHighestScore = player;
            }
        }

        //If the highest score of all the players is 200 or more, then we have a winner
        if (highestScore >= 200) {
            return playerWithHighestScore;
        }

        //If the highest score at the moment is less than 200, nobody won yet.
        return null;

    }

    /**
     * Checks who won this specific round (if the game isn't a draw).
     * Winners will have no cards left.
     * @return the winner of the round.
     */
    public Player findRoundWinner() {

        Player winner = null;
        for (Player player : players) {
            //If the player has no cards left, they won the round.
            if (player.getCurrentDeck().size() == 0) {
                winner = player;
            }
        }
        return winner;

    }

    /**
     * Adds a card to the discard deck and resets the rank/suit
     * @param toAdd the card you are adding to the deck
     */
    private void addToDiscardDeck(Card toAdd) {

        discardDeck.add(toAdd);
        currentRank = toAdd.getRank();
        currentSuit = toAdd.getSuit();

    }

    /**
     * Sets up a new round of Crazy8. Gives players new cards, shuffles the deck and adds card to discard pile.
     */
    public void prepareNewRound() {

        allPlayerActions = new LinkedList<>();

        drawPile = Card.getDeck();
        Collections.shuffle(drawPile);
        //Gives each player 5 cards from the draw pile
        for (Player player : players) {
            LinkedList<Card> playerDeck = new LinkedList<>();
            for (int i = 0; i < 5; i++) {
                playerDeck.add(drawPile.get(0));
                drawPile.remove(0);
            }
            player.receiveInitialCards(playerDeck);
        }

        //Take the card from the top of the pile and add it to the discard pile (can't be an 8).
        Card topCard = drawPile.get(0);
        drawPile.remove(topCard);
        //ensure the card is not 8
        while (topCard.getRank() == Card.Rank.EIGHT) {
            //shuffles the 8 card back into the draw pile
            drawPile.add(new Random().nextInt(drawPile.size()), topCard);

            topCard = drawPile.get(0);
            drawPile.remove(topCard);
        }

        discardDeck = new LinkedList<>();
        //add the non-8 card to the discard pile.
        addToDiscardDeck(topCard);
        System.out.println("The first card is  " + topCard.getRank() + " of " +topCard.getSuit());

    }

    /**
     * Checks if one of the players are cheating, if they are the game ends.
     * @return true if there is a cheater, false if not.
     */
    public boolean checkCheating() {

        if (allPlayerActions.size() <= 1) {
            return false;
        }

        Card cardBeforePlay = allPlayerActions.get(allPlayerActions.size() - 2).playedCard;
        Card cardAfterPlay = allPlayerActions.get(allPlayerActions.size() - 1).playedCard;

        //Checks to make sure the PlayerTurn is valid
        if (cardBeforePlay == null || cardAfterPlay == null) {
            return false;
        } else if (cardBeforePlay.getSuit() != cardAfterPlay.getSuit() && cardBeforePlay.getRank()
                != cardAfterPlay.getRank() && cardAfterPlay.getRank() != Card.Rank.EIGHT && cardBeforePlay.getRank()
                != Card.Rank.EIGHT) {
            return true;
        }

        return false;

    }

    /**
     * Checks if any condition to end the round is satisfied.
     * @return true if there is a draw, a player won the round, or somebody is cheating
     */
    public boolean checkRoundEnded() {

        if (drawPile.size() == 0) {
            return true;
        }

        for (Player player : players) {
            if (player.currentDeck.size() == 0) {
                return true;
            }
        }

        return false;

    }

    /**
     * Class that stores state and behavior of a player in the game, implements from PlayerStrategy
     */
    class Player implements PlayerStrategy {

        private List<Card> currentDeck;
        private int playerId;
        private List<Integer> opponentIDs;
        private int playerScore;

        private int playerDeckIndex = -1;

        /**
         * Getter for the current deck of this player
         * @return the current deck of this player
         */
        public List<Card> getCurrentDeck() {
            return currentDeck;
        }

        /**
         * Gets a list of all the opponents in the game
         * @return list of opponents
         */
        public List<Player> getOpponents() {
            List<Player> opponents = new LinkedList<>();
            for (Integer opponentIDs : opponentIDs) {
                opponents.add(getPlayer(opponentIDs));
            }
            return opponents;
        }

        /**
         * Gets the players current score.
         * @return the players score
         */
        public int getPlayerScore() {
            return playerScore;
        }

        /**
         * Getter for playerID
         * @return the players ID EX: 1, 2, 3, etc.
         */
        public int getPlayerId() {
            return playerId;
        }

        /**
         * Readjusts the players score
         * @param playerScore the new score for the player
         */
        public void setPlayerScore(int playerScore) {
            this.playerScore = playerScore;
        }

        /**
         * Receives initial cards for this player at beginning of each round
         * @param initialDeck consists of the deck at the start of the round
         */
        @Override
        public void receiveInitialCards(List<Card> initialDeck) {
            reset();
            this.currentDeck = initialDeck;
        }

        /**
         * Initializes a player
         * @param playerId The id for this player
         * @param opponentIDs a list of the opponent's IDs
         */
        @Override
        public void init(int playerId, List<Integer> opponentIDs) {
            this.playerId = playerId;
            this.opponentIDs = opponentIDs;
            this.playerScore = 0;
        }

        /**
         * Makes a turn for this player based on the current deck and top of the discard pile.
         * @return a PlayerTurn object consisting of the properties of this turn
         */
        public PlayerTurn playTurn() {

            PlayerTurn playerTurn = new PlayerTurn();
            playerTurn.playerId = this.playerId;

            //Gets the current card at the top of the discard deck
            Card topCard = discardDeck.get(discardDeck.size() - 1);

            if (shouldDrawCard(topCard, currentSuit)) {

                receiveCard(drawCard());
                playerTurn.declaredSuit = null;
                playerTurn.drewACard = true;
                playerTurn.playedCard = null;
                allPlayerActions.add(playerTurn);
                System.out.println("Player " + playerId + " drew a card");
                return playerTurn;

            }

            //This is an index that tells the Player object which card in its current deck is being checked
            playerDeckIndex = 0;

            for (Card card : currentDeck) {

                //If it finds a suitable card to place, the card will be played
                if (card.getSuit() == currentSuit || card.getRank() == currentRank
                        && card.getRank() != Card.Rank.EIGHT) {

                    playerTurn.drewACard = false;
                    playerTurn.declaredSuit = null;
                    playerTurn.playedCard = playCard();
                    allPlayerActions.add(playerTurn);
                    System.out.println("Player " + playerId + " placed a " + playerTurn.playedCard.getRank() +
                            " of " + playerTurn.playedCard.getSuit());
                    return playerTurn;

                } else if (card.getRank() == Card.Rank.EIGHT) {

                    playerTurn.drewACard  = false;
                    playerTurn.playedCard = playCard();
                    playerTurn.declaredSuit = declareSuit();
                    allPlayerActions.add(playerTurn);
                    System.out.println("Player " + playerId + " placed an 8 and declared a new suit: " +
                            playerTurn.declaredSuit);
                    return playerTurn;

                }

                //Notifies the player class that the next index will be checked
                playerDeckIndex++;

            }

            return playerTurn;

        }

        /**
         * Checks if the player should draw a card
         * @param topPileCard The card currently at the top of the pile
         * @param pileSuit The suit that the pile was changed to as the result of an "8" being played.
         * Will be null if no "8" was played.
         * @return true if the player should draw a card, false otherwise
         */
        @Override
        public boolean shouldDrawCard(Card topPileCard, Card.Suit pileSuit) {

            for (Card card : currentDeck) {
                if (pileSuit != null && card.getSuit() == pileSuit) {
                    return false;
                } else if (topPileCard.getRank() == card.getRank() || topPileCard.getSuit() == card.getSuit()) {
                    return false;
                } else if (card.getRank() == Card.Rank.EIGHT) {
                    return false;
                }
            }

            return true;

        }

        /**
         * draws a card from the drawPile
         * @return the card that was drawn
         */
        private Card drawCard() {

            Card drawnCard = drawPile.get(0);
            drawPile.remove(0);
            currentDeck.add(drawnCard);
            return drawnCard;

        }

        /**
         * Gets the sum of the values of the other players cards
         * @return the sum of the values of the other players cards.
         */
        public int sumOfOtherPlayerCards() {
            int sumOfOtherPlayersCards = 0;
            for (Player player : getOpponents()) {
                List<Card> opponentsDeck = player.getCurrentDeck();
                for (Card card : opponentsDeck) {
                    sumOfOtherPlayersCards += card.getPointValue();
                }
            }
            return sumOfOtherPlayersCards;
        }

        /**
         * Adds a card to the deck
         * @param drawnCard The card that this player has drawn
         */
        @Override
        public void receiveCard(Card drawnCard) {

            currentDeck.add(drawnCard);

        }

        /**
         * Takes a card from the player and adds it to the discard deck
         * @return the card that was played
         */
        @Override
        public Card playCard() {

            Card playingCard = currentDeck.get(playerDeckIndex);
            addToDiscardDeck(playingCard);
            currentDeck.remove(playingCard);

            return playingCard;

        }

        /**
         * changes the current suit of the game.
         * Can only be called if the player play's an 8
         * @return the suit that the player declared.
         */
        @Override
        public Card.Suit declareSuit() {

            Random random = new Random();
            //There are 4 suits in a game
            int randomSuitIndex = random.nextInt(4);
            currentSuit = Card.Suit.values()[randomSuitIndex];
            currentRank = Card.Rank.EIGHT;
            return currentSuit;

        }

        /**
         * processes opponent actions
         * in this case, the opponents actions do not affect the behavior of this player
         * @param opponentActions A list of what the opponents did on each of their turns
         */
        @Override
        public void processOpponentActions(List<PlayerTurn> opponentActions) {

        }

        /**
         * Resets a player to restart the round.
         */
        @Override
        public void reset() {

            currentDeck = new LinkedList<>();
            playerDeckIndex = -1;

        }
    }

}
