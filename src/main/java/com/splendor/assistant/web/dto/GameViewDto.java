package com.splendor.assistant.web.dto;

import java.util.HashMap;
import java.util.Map;

public class GameViewDto {
    private PlayerStateDto playerOne;
    private PlayerStateDto playerTwo;
    private BoardStateDto board;
    private Map<Integer, Integer> deckCounts = new HashMap<>();
    private int currentPlayerNumber;
    private boolean finished;
    private Integer winnerPlayerNumber;
    private String lastEvent;
    private RecommendationResponseDto recommendation;

    public PlayerStateDto getPlayerOne() {
        return playerOne;
    }

    public void setPlayerOne(PlayerStateDto playerOne) {
        this.playerOne = playerOne;
    }

    public PlayerStateDto getPlayerTwo() {
        return playerTwo;
    }

    public void setPlayerTwo(PlayerStateDto playerTwo) {
        this.playerTwo = playerTwo;
    }

    public BoardStateDto getBoard() {
        return board;
    }

    public void setBoard(BoardStateDto board) {
        this.board = board;
    }

    public Map<Integer, Integer> getDeckCounts() {
        return deckCounts;
    }

    public void setDeckCounts(Map<Integer, Integer> deckCounts) {
        this.deckCounts = deckCounts;
    }

    public int getCurrentPlayerNumber() {
        return currentPlayerNumber;
    }

    public void setCurrentPlayerNumber(int currentPlayerNumber) {
        this.currentPlayerNumber = currentPlayerNumber;
    }

    public boolean isFinished() {
        return finished;
    }

    public void setFinished(boolean finished) {
        this.finished = finished;
    }

    public Integer getWinnerPlayerNumber() {
        return winnerPlayerNumber;
    }

    public void setWinnerPlayerNumber(Integer winnerPlayerNumber) {
        this.winnerPlayerNumber = winnerPlayerNumber;
    }

    public String getLastEvent() {
        return lastEvent;
    }

    public void setLastEvent(String lastEvent) {
        this.lastEvent = lastEvent;
    }

    public RecommendationResponseDto getRecommendation() {
        return recommendation;
    }

    public void setRecommendation(RecommendationResponseDto recommendation) {
        this.recommendation = recommendation;
    }
}
