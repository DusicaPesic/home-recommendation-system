package com.splendor.assistant.game;

import com.splendor.assistant.model.BoardState;
import com.splendor.assistant.model.Card;
import com.splendor.assistant.model.GemColor;
import com.splendor.assistant.model.Noble;
import com.splendor.assistant.model.PlayerState;

import java.util.ArrayDeque;
import java.util.ArrayList;
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

    public SplendorGame midGamePreset() {
        List<Card> availableCards = new ArrayList<>(catalog.loadCards());
        PlayerState playerOne = new PlayerState(0);
        PlayerState playerTwo = new PlayerState(0);

        buyPresetCard(playerOne, takeCard(availableCards, 1, GemColor.RED, 1));
        buyPresetCard(playerOne, takeCard(availableCards, 1, GemColor.BLACK, 0));
        buyPresetCard(playerOne, takeCard(availableCards, 1, GemColor.BLACK, 0));
        buyPresetCard(playerOne, takeCard(availableCards, 1, GemColor.WHITE, 0));
        buyPresetCard(playerOne, takeCard(availableCards, 2, GemColor.BLUE, 2));
        buyPresetCard(playerOne, takeCard(availableCards, 2, GemColor.GREEN, 2));
        buyPresetCard(playerOne, takeCard(availableCards, 2, GemColor.WHITE, 1));
        playerOne.reserve(takeCard(availableCards, 2, GemColor.WHITE, 3));
        playerOne.setToken(GemColor.WHITE, 1);
        playerOne.setToken(GemColor.BLUE, 2);
        playerOne.setToken(GemColor.GREEN, 1);
        playerOne.setToken(GemColor.BLACK, 1);
        playerOne.setGoldTokens(1);

        buyPresetCard(playerTwo, takeCard(availableCards, 1, GemColor.BLUE, 1));
        buyPresetCard(playerTwo, takeCard(availableCards, 1, GemColor.BLUE, 0));
        buyPresetCard(playerTwo, takeCard(availableCards, 1, GemColor.BLACK, 0));
        buyPresetCard(playerTwo, takeCard(availableCards, 2, GemColor.RED, 2));
        buyPresetCard(playerTwo, takeCard(availableCards, 2, GemColor.BLACK, 2));
        playerTwo.reserve(takeCard(availableCards, 3, GemColor.RED, 4));
        playerTwo.setToken(GemColor.WHITE, 2);
        playerTwo.setToken(GemColor.GREEN, 2);
        playerTwo.setToken(GemColor.RED, 1);

        BoardState board = new BoardState();
        for (GemColor color : GemColor.values()) {
            int tokensInPlayers = playerOne.tokenCount(color) + playerTwo.tokenCount(color);
            board.setBankToken(color, 4 - tokensInPlayers);
        }
        board.setBankGoldTokens(5 - playerOne.getGoldTokens() - playerTwo.getGoldTokens());
        board.addNoble(noble("NOBLE-1"));
        board.addNoble(noble("NOBLE-2"));
        board.addNoble(noble("NOBLE-9"));

        for (int level = 1; level <= 3; level++) {
            for (int i = 0; i < 4; i++) {
                board.addVisibleCard(takeFirstLevelCard(availableCards, level));
            }
        }

        SplendorGame game = new SplendorGame(playerOne, playerTwo, board, decksFrom(availableCards));
        game.setLastEvent("Valid mid-game preset loaded. Player 1 is on turn.");
        return game;
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

    private Map<Integer, Deque<Card>> decksFrom(List<Card> cards) {
        Map<Integer, List<Card>> grouped = cards.stream()
                .collect(Collectors.groupingBy(Card::getLevel));
        Map<Integer, Deque<Card>> decks = new HashMap<>();
        for (int level = 1; level <= 3; level++) {
            decks.put(level, new ArrayDeque<>(grouped.getOrDefault(level, new ArrayList<>())));
        }
        return decks;
    }

    private List<Noble> randomNobles() {
        List<Noble> nobles = catalog.loadNobles();
        Collections.shuffle(nobles);
        return nobles.subList(0, 3);
    }

    private Noble noble(String id) {
        return catalog.loadNobles().stream()
                .filter(noble -> noble.getId().equals(id))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Missing noble " + id));
    }

    private void buyPresetCard(PlayerState player, Card card) {
        player.buy(card);
    }

    private Card takeFirstLevelCard(List<Card> cards, int level) {
        for (int i = 0; i < cards.size(); i++) {
            Card card = cards.get(i);
            if (card.getLevel() == level) {
                cards.remove(i);
                return card;
            }
        }
        throw new IllegalStateException("Missing level " + level + " card");
    }

    private Card takeCard(List<Card> cards, int level, GemColor bonus, int points) {
        for (int i = 0; i < cards.size(); i++) {
            Card card = cards.get(i);
            if (card.getLevel() == level
                    && card.getColorBonus() == bonus
                    && card.getPrestigePoints() == points) {
                cards.remove(i);
                return card;
            }
        }
        throw new IllegalStateException("Missing card level=" + level + ", bonus=" + bonus + ", points=" + points);
    }
}
