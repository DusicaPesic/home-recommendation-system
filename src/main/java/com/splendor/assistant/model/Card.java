package com.splendor.assistant.model;

import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;
import java.util.Objects;

public class Card {
    private final String id;
    private final int level;
    private final GemColor colorBonus;
    private final int prestigePoints;
    private final EnumMap<GemColor, Integer> cost;

    public Card(String id, int level, GemColor colorBonus, int prestigePoints, Map<GemColor, Integer> cost) {
        this.id = id;
        this.level = level;
        this.colorBonus = colorBonus;
        this.prestigePoints = prestigePoints;
        this.cost = new EnumMap<>(GemColor.class);
        for (GemColor color : GemColor.values()) {
            this.cost.put(color, cost.getOrDefault(color, 0));
        }
    }

    public String getId() {
        return id;
    }

    public int getLevel() {
        return level;
    }

    public GemColor getColorBonus() {
        return colorBonus;
    }

    public int getPrestigePoints() {
        return prestigePoints;
    }

    public Map<GemColor, Integer> getCost() {
        return Collections.unmodifiableMap(cost);
    }

    public int costOf(GemColor color) {
        return cost.getOrDefault(color, 0);
    }

    public int getTotalCost() {
        return cost.values().stream().mapToInt(Integer::intValue).sum();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Card)) return false;
        Card card = (Card) o;
        return Objects.equals(id, card.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return id + "(" + colorBonus + ", points=" + prestigePoints + ", cost=" + cost + ")";
    }
}
