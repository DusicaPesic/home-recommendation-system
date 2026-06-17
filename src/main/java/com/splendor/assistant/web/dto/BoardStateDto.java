package com.splendor.assistant.web.dto;

import com.splendor.assistant.model.GemColor;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

public class BoardStateDto {
    private List<CardDto> visibleCards = new ArrayList<>();
    private List<NobleDto> nobles = new ArrayList<>();
    private Map<GemColor, Integer> bankTokens = new EnumMap<>(GemColor.class);
    private int bankGoldTokens;

    public List<CardDto> getVisibleCards() {
        return visibleCards;
    }

    public void setVisibleCards(List<CardDto> visibleCards) {
        this.visibleCards = visibleCards;
    }

    public List<NobleDto> getNobles() {
        return nobles;
    }

    public void setNobles(List<NobleDto> nobles) {
        this.nobles = nobles;
    }

    public Map<GemColor, Integer> getBankTokens() {
        return bankTokens;
    }

    public void setBankTokens(Map<GemColor, Integer> bankTokens) {
        this.bankTokens = bankTokens;
    }

    public int getBankGoldTokens() {
        return bankGoldTokens;
    }

    public void setBankGoldTokens(int bankGoldTokens) {
        this.bankGoldTokens = bankGoldTokens;
    }
}
