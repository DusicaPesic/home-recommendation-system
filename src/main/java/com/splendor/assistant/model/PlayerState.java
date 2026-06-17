package com.splendor.assistant.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

public class PlayerState {
    private int prestigePoints;
    private final EnumMap<GemColor, Integer> tokens = new EnumMap<>(GemColor.class);
    private int goldTokens;
    private final EnumMap<GemColor, Integer> bonuses = new EnumMap<>(GemColor.class);
    private final List<Card> reservedCards = new ArrayList<>();
    private final List<Card> purchasedCards = new ArrayList<>();

    public PlayerState(int prestigePoints) {
        this.prestigePoints = prestigePoints;
        for (GemColor color : GemColor.values()) {
            tokens.put(color, 0);
            bonuses.put(color, 0);
        }
    }

    public int getPrestigePoints() {
        return prestigePoints;
    }

    public void setPrestigePoints(int prestigePoints) {
        this.prestigePoints = prestigePoints;
    }

    public void addPrestigePoints(int points) {
        this.prestigePoints += points;
    }

    public Map<GemColor, Integer> getTokens() {
        return Collections.unmodifiableMap(tokens);
    }

    public int tokenCount(GemColor color) {
        return tokens.getOrDefault(color, 0);
    }

    public void setToken(GemColor color, int count) {
        tokens.put(color, count);
    }

    public void addToken(GemColor color, int count) {
        tokens.put(color, tokenCount(color) + count);
    }

    public int getGoldTokens() {
        return goldTokens;
    }

    public void setGoldTokens(int goldTokens) {
        this.goldTokens = goldTokens;
    }

    public void addGoldTokens(int count) {
        this.goldTokens += count;
    }

    public Map<GemColor, Integer> getBonuses() {
        return Collections.unmodifiableMap(bonuses);
    }

    public int bonusCount(GemColor color) {
        return bonuses.getOrDefault(color, 0);
    }

    public void setBonus(GemColor color, int count) {
        bonuses.put(color, count);
    }

    public void addBonus(GemColor color, int count) {
        bonuses.put(color, bonusCount(color) + count);
    }

    public List<Card> getReservedCards() {
        return Collections.unmodifiableList(reservedCards);
    }

    public void reserve(Card card) {
        reservedCards.add(card);
    }

    public boolean removeReserved(Card card) {
        return reservedCards.remove(card);
    }

    public List<Card> getPurchasedCards() {
        return Collections.unmodifiableList(purchasedCards);
    }

    public void purchase(Card card) {
        purchasedCards.add(card);
    }

    public void buy(Card card) {
        purchase(card);
        addBonus(card.getColorBonus(), 1);
        addPrestigePoints(card.getPrestigePoints());
    }

    public int getTotalTokens() {
        return goldTokens + tokens.values().stream().mapToInt(Integer::intValue).sum();
    }

    public boolean hasReserved(Card card) {
        return reservedCards.contains(card);
    }

    public int missingTokensFor(Card card) {
        int missing = 0;
        for (GemColor color : GemColor.values()) {
            int requiredAfterBonus = Math.max(0, card.costOf(color) - bonusCount(color));
            int missingForColor = Math.max(0, requiredAfterBonus - tokenCount(color));
            missing += missingForColor;
        }
        return Math.max(0, missing - goldTokens);
    }

    public boolean canPay(Card card) {
        return missingTokensFor(card) == 0;
    }

    public boolean needsColorFor(Card card, GemColor color) {
        int requiredAfterBonus = Math.max(0, card.costOf(color) - bonusCount(color));
        return requiredAfterBonus > tokenCount(color);
    }
}
