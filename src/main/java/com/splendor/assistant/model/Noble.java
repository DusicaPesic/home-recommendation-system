package com.splendor.assistant.model;

import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;

public class Noble {
    private final String id;
    private final EnumMap<GemColor, Integer> requiredBonuses;
    private final int prestigePoints;

    public Noble(String id, Map<GemColor, Integer> requiredBonuses, int prestigePoints) {
        this.id = id;
        this.requiredBonuses = new EnumMap<>(GemColor.class);
        for (GemColor color : GemColor.values()) {
            this.requiredBonuses.put(color, requiredBonuses.getOrDefault(color, 0));
        }
        this.prestigePoints = prestigePoints;
    }

    public static Noble noble(String id, int white, int blue, int green, int red, int black) {
        Map<GemColor, Integer> requiredBonuses = new EnumMap<>(GemColor.class);
        requiredBonuses.put(GemColor.WHITE, white);
        requiredBonuses.put(GemColor.BLUE, blue);
        requiredBonuses.put(GemColor.GREEN, green);
        requiredBonuses.put(GemColor.RED, red);
        requiredBonuses.put(GemColor.BLACK, black);
        return new Noble(id, requiredBonuses, 3);
    }

    public String getId() {
        return id;
    }

    public Map<GemColor, Integer> getRequiredBonuses() {
        return Collections.unmodifiableMap(requiredBonuses);
    }

    public int getPrestigePoints() {
        return prestigePoints;
    }
}
