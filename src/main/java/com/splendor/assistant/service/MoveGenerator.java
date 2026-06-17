package com.splendor.assistant.service;

import com.splendor.assistant.model.BoardState;
import com.splendor.assistant.model.Card;
import com.splendor.assistant.model.GameState;
import com.splendor.assistant.model.GemColor;
import com.splendor.assistant.model.Move;
import com.splendor.assistant.model.PlayerState;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

public class MoveGenerator {
    public List<Move> generate(GameState state) {
        List<Move> moves = new ArrayList<>();
        PlayerState player = state.getPlayer();
        BoardState board = state.getBoard();

        for (Card card : board.getVisibleCards()) {
            // buy
            if (player.canPay(card)) {
                moves.add(Move.buy(card, false));
            }
            // reserve
            if (player.getReservedCards().size() < 3) {
                moves.add(Move.reserve(card));
            }
        }

        for (Card card : player.getReservedCards()) {
            // buy reserved
            if (player.canPay(card)) {
                moves.add(Move.buy(card, true));
            }
        }

        generateTakeThreeDifferent(player, board, moves);
        generateTakeTwoSame(player, board, moves);
        return moves;
    }

    private void generateTakeThreeDifferent(PlayerState player, BoardState board, List<Move> moves) {
        GemColor[] colors = GemColor.values();
        for (int i = 0; i < colors.length; i++) {
            for (int j = i + 1; j < colors.length; j++) {
                for (int k = j + 1; k < colors.length; k++) {
                    if (board.bankTokenCount(colors[i]) > 0
                            && board.bankTokenCount(colors[j]) > 0
                            && board.bankTokenCount(colors[k]) > 0) {
                        EnumMap<GemColor, Integer> tokens = emptyTokens();
                        tokens.put(colors[i], 1);
                        tokens.put(colors[j], 1);
                        tokens.put(colors[k], 1);
                        moves.add(Move.take(colors[i] + "+" + colors[j] + "+" + colors[k], tokens));
                    }
                }
            }
        }
    }

    private void generateTakeTwoSame(PlayerState player, BoardState board, List<Move> moves) {
        for (GemColor color : GemColor.values()) {
            if (board.bankTokenCount(color) >= 4) {
                Map<GemColor, Integer> tokens = emptyTokens();
                tokens.put(color, 2);
                moves.add(Move.take(color + "+" + color, tokens));
            }
        }
    }

    private EnumMap<GemColor, Integer> emptyTokens() {
        EnumMap<GemColor, Integer> tokens = new EnumMap<>(GemColor.class);
        for (GemColor color : GemColor.values()) {
            tokens.put(color, 0);
        }
        return tokens;
    }
}
