package com.splendor.assistant.web.dto;

public class GameStateDto {
    private PlayerStateDto player = new PlayerStateDto();
    private PlayerStateDto opponent = new PlayerStateDto();
    private BoardStateDto board = new BoardStateDto();

    public PlayerStateDto getPlayer() {
        return player;
    }

    public void setPlayer(PlayerStateDto player) {
        this.player = player;
    }

    public PlayerStateDto getOpponent() {
        return opponent;
    }

    public void setOpponent(PlayerStateDto opponent) {
        this.opponent = opponent;
    }

    public BoardStateDto getBoard() {
        return board;
    }

    public void setBoard(BoardStateDto board) {
        this.board = board;
    }
}
