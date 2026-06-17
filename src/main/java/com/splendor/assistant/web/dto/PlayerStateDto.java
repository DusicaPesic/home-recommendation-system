package com.splendor.assistant.web.dto;

import com.splendor.assistant.model.GemColor;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

public class PlayerStateDto {
    private int prestigePoints;
    private Map<GemColor, Integer> tokens = new EnumMap<>(GemColor.class);
    private int goldTokens;
    private Map<GemColor, Integer> bonuses = new EnumMap<>(GemColor.class);
    private List<CardDto> reservedCards = new ArrayList<>();
    private List<CardDto> purchasedCards = new ArrayList<>();

    public int getPrestigePoints() {
        return prestigePoints;
    }

    public void setPrestigePoints(int prestigePoints) {
        this.prestigePoints = prestigePoints;
    }

    public Map<GemColor, Integer> getTokens() {
        return tokens;
    }

    public void setTokens(Map<GemColor, Integer> tokens) {
        this.tokens = tokens;
    }

    public int getGoldTokens() {
        return goldTokens;
    }

    public void setGoldTokens(int goldTokens) {
        this.goldTokens = goldTokens;
    }

    public Map<GemColor, Integer> getBonuses() {
        return bonuses;
    }

    public void setBonuses(Map<GemColor, Integer> bonuses) {
        this.bonuses = bonuses;
    }

    public List<CardDto> getReservedCards() {
        return reservedCards;
    }

    public void setReservedCards(List<CardDto> reservedCards) {
        this.reservedCards = reservedCards;
    }

    public List<CardDto> getPurchasedCards() {
        return purchasedCards;
    }

    public void setPurchasedCards(List<CardDto> purchasedCards) {
        this.purchasedCards = purchasedCards;
    }
}
