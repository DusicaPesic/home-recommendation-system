package com.splendor.assistant.model;

import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;
import java.util.Objects;

public class Move {
    private final String id;
    private final MoveType type;
    private final Card card;
    private final EnumMap<GemColor, Integer> takenTokens;
    private int score;

    public Move(String id, MoveType type, Card card, Map<GemColor, Integer> takenTokens) {
        this.id = id;
        this.type = type;
        this.card = card;
        this.takenTokens = new EnumMap<>(GemColor.class);
        for (GemColor color : GemColor.values()) {
            this.takenTokens.put(color, takenTokens == null ? 0 : takenTokens.getOrDefault(color, 0));
        }
    }

    public static Move buy(Card card, boolean reserved) {
        return new Move((reserved ? "buy-reserved:" : "buy-visible:") + card.getId(), MoveType.BUY_CARD, card, null);
    }

    public static Move reserve(Card card) {
        return new Move("reserve:" + card.getId(), MoveType.RESERVE_CARD, card, null);
    }

    public static Move take(String id, Map<GemColor, Integer> tokens) {
        return new Move("take:" + id, MoveType.TAKE_TOKENS, null, tokens);
    }

    public String getId() {
        return id;
    }

    public MoveType getType() {
        return type;
    }

    public Card getCard() {
        return card;
    }

    public Map<GemColor, Integer> getTakenTokens() {
        return Collections.unmodifiableMap(takenTokens);
    }

    public boolean takes(GemColor color) {
        return takenTokens.getOrDefault(color, 0) > 0;
    }

    public int getTakenTokenCount() {
        return takenTokens.values().stream().mapToInt(Integer::intValue).sum();
    }

    public boolean takesThreeDifferentColors() {
        return getTakenTokenCount() == 3 && takenTokens.values().stream().filter(v -> v > 0).count() == 3;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Move)) return false;
        Move move = (Move) o;
        return Objects.equals(id, move.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        if (type == MoveType.TAKE_TOKENS) {
            return id + " score=" + score + " tokens=" + takenTokens;
        }
        return id + " score=" + score + " card=" + card;
    }
}
