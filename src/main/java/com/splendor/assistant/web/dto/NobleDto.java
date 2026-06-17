package com.splendor.assistant.web.dto;

import com.splendor.assistant.model.GemColor;

import java.util.EnumMap;
import java.util.Map;

public class NobleDto {
    private String id;
    private Map<GemColor, Integer> requiredBonuses = new EnumMap<>(GemColor.class);
    private int prestigePoints = 3;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Map<GemColor, Integer> getRequiredBonuses() {
        return requiredBonuses;
    }

    public void setRequiredBonuses(Map<GemColor, Integer> requiredBonuses) {
        this.requiredBonuses = requiredBonuses;
    }

    public int getPrestigePoints() {
        return prestigePoints;
    }

    public void setPrestigePoints(int prestigePoints) {
        this.prestigePoints = prestigePoints;
    }
}
