package com.splendor.assistant.web.dto;

import com.splendor.assistant.model.GemColor;

import java.util.EnumMap;
import java.util.Map;

public class DiscardRequestDto {
    private Map<GemColor, Integer> tokens = new EnumMap<>(GemColor.class);
    private int goldTokens;

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
}
