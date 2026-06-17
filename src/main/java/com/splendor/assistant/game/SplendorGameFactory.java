package com.splendor.assistant.game;

import com.splendor.assistant.model.BoardState;
import com.splendor.assistant.model.Card;
import com.splendor.assistant.model.GemColor;
import com.splendor.assistant.model.Noble;
import com.splendor.assistant.model.PlayerState;

import java.util.ArrayDeque;
import java.util.Collections;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class SplendorGameFactory {
    private final SplendorCatalog catalog;

    public SplendorGameFactory() {
        this(new SplendorCatalog());
    }

    public SplendorGameFactory(SplendorCatalog catalog) {
        this.catalog = catalog;
    }

    public SplendorGame newGame() {
        Map<Integer, Deque<Card>> decks = buildDecks();
        BoardState board = new BoardState();
        for (GemColor color : GemColor.values()) {
            board.setBankToken(color, 4);
        }
        board.setBankGoldTokens(5);

        for (Noble noble : randomNobles()) {
            board.addNoble(noble);
        }
        for (int level = 1; level <= 3; level++) {
            for (int i = 0; i < 4; i++) {
                board.addVisibleCard(decks.get(level).removeFirst());
            }
        }
        return new SplendorGame(new PlayerState(0), new PlayerState(0), board, decks);
    }

    private Map<Integer, Deque<Card>> buildDecks() {
        Map<Integer, List<Card>> grouped = catalog.loadCards().stream()
                .collect(Collectors.groupingBy(Card::getLevel));
        Map<Integer, Deque<Card>> decks = new HashMap<>();
        for (int level = 1; level <= 3; level++) {
            List<Card> cards = grouped.get(level);
            Collections.shuffle(cards);
            decks.put(level, new ArrayDeque<>(cards));
        }
        return decks;
    }

    private List<Noble> randomNobles() {
        List<Noble> nobles = catalog.loadNobles();
        Collections.shuffle(nobles);
        return nobles.subList(0, 3);
    }
}
