package com.splendor.assistant.model;

public class GameState {
    private final PlayerState player;
    private final PlayerState opponent;
    private final BoardState board;

    public GameState(PlayerState player, PlayerState opponent, BoardState board) {
        this.player = player;
        this.opponent = opponent;
        this.board = board;
    }

    public PlayerState getPlayer() {
        return player;
    }

    public PlayerState getOpponent() {
        return opponent;
    }

    public BoardState getBoard() {
        return board;
    }
}
