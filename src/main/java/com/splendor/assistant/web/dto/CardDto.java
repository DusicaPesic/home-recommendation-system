package com.splendor.assistant.web.dto;

import com.splendor.assistant.model.GemColor;

import java.util.EnumMap;
import java.util.Map;

public class CardDto {
    private String id;
    private int level;
    private GemColor colorBonus;
    private int prestigePoints;
    private Map<GemColor, Integer> cost = new EnumMap<>(GemColor.class);

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public GemColor getColorBonus() {
        return colorBonus;
    }

    public void setColorBonus(GemColor colorBonus) {
        this.colorBonus = colorBonus;
    }

    public int getPrestigePoints() {
        return prestigePoints;
    }

    public void setPrestigePoints(int prestigePoints) {
        this.prestigePoints = prestigePoints;
    }

    public Map<GemColor, Integer> getCost() {
        return cost;
    }

    public void setCost(Map<GemColor, Integer> cost) {
        this.cost = cost;
    }
}
