package com.splendor.assistant.game;

import com.splendor.assistant.model.BoardState;
import com.splendor.assistant.model.Card;
import com.splendor.assistant.model.GameState;
import com.splendor.assistant.model.PlayerState;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.Map;

public class SplendorGame {
    private final PlayerState playerOne;
    private final PlayerState playerTwo;
    private final BoardState board;
    private final Map<Integer, Deque<Card>> decksByLevel;
    private int currentPlayerNumber = 1;
    private boolean finished;
    private Integer winnerPlayerNumber;
    private String lastEvent = "New game started.";
    private boolean waitingForDiscard;
    private int discardPlayerNumber;
    private int discardCount;

    public SplendorGame(PlayerState playerOne, PlayerState playerTwo, BoardState board, Map<Integer, Deque<Card>> decksByLevel) {
        this.playerOne = playerOne;
        this.playerTwo = playerTwo;
        this.board = board;
        this.decksByLevel = new HashMap<>();
        decksByLevel.forEach((level, cards) -> this.decksByLevel.put(level, new ArrayDeque<>(cards)));
    }

    public PlayerState getPlayerOne() {
        return playerOne;
    }

    public PlayerState getPlayerTwo() {
        return playerTwo;
    }

    public BoardState getBoard() {
        return board;
    }

    public Map<Integer, Deque<Card>> getDecksByLevel() {
        return decksByLevel;
    }

    public int getCurrentPlayerNumber() {
        return currentPlayerNumber;
    }

    public boolean isFinished() {
        return finished;
    }

    public Integer getWinnerPlayerNumber() {
        return winnerPlayerNumber;
    }

    public String getLastEvent() {
        return lastEvent;
    }

    public void setLastEvent(String lastEvent) {
        this.lastEvent = lastEvent;
    }

    public boolean isWaitingForDiscard() {
        return waitingForDiscard;
    }

    public int getDiscardPlayerNumber() {
        return discardPlayerNumber;
    }

    public int getDiscardCount() {
        return discardCount;
    }

    public void requireDiscard(int playerNumber, int count) {
        waitingForDiscard = true;
        discardPlayerNumber = playerNumber;
        discardCount = count;
    }

    public void clearDiscard() {
        waitingForDiscard = false;
        discardPlayerNumber = 0;
        discardCount = 0;
    }

    public PlayerState currentPlayer() {
        return currentPlayerNumber == 1 ? playerOne : playerTwo;
    }

    public PlayerState opponent() {
        return currentPlayerNumber == 1 ? playerTwo : playerOne;
    }

    public GameState recommendationState() {
        return new GameState(currentPlayer(), opponent(), board);
    }

    public void switchTurn() {
        currentPlayerNumber = currentPlayerNumber == 1 ? 2 : 1;
    }

    public void finishWithWinner(int playerNumber) {
        finished = true;
        winnerPlayerNumber = playerNumber;
    }
}
