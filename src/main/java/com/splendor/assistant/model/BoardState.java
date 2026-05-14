package com.splendor.assistant.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

public class BoardState {
    private final List<Card> visibleCards = new ArrayList<>();
    private final List<Noble> nobles = new ArrayList<>();
    private final EnumMap<GemColor, Integer> bankTokens = new EnumMap<>(GemColor.class);
    private int bankGoldTokens;

    public BoardState() {
        for (GemColor color : GemColor.values()) {
            bankTokens.put(color, 0);
        }
    }

    public List<Card> getVisibleCards() {
        return Collections.unmodifiableList(visibleCards);
    }

    public void addVisibleCard(Card card) {
        visibleCards.add(card);
    }

    public List<Noble> getNobles() {
        return Collections.unmodifiableList(nobles);
    }

    public void addNoble(Noble noble) {
        nobles.add(noble);
    }

    public Map<GemColor, Integer> getBankTokens() {
        return Collections.unmodifiableMap(bankTokens);
    }

    public int bankTokenCount(GemColor color) {
        return bankTokens.getOrDefault(color, 0);
    }

    public void setBankToken(GemColor color, int count) {
        bankTokens.put(color, count);
    }

    public int getBankGoldTokens() {
        return bankGoldTokens;
    }

    public void setBankGoldTokens(int bankGoldTokens) {
        this.bankGoldTokens = bankGoldTokens;
    }
}
