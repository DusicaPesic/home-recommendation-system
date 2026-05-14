package com.splendor.assistant.model.facts.strategy;

import com.splendor.assistant.model.Card;
import com.splendor.assistant.model.GemColor;
import com.splendor.assistant.model.GoalType;
import java.util.Objects;

public class StrategicGoalFact {
    private final GoalType type;
    private final Card card;
    private final GemColor color;

    public StrategicGoalFact(GoalType type, Card card, GemColor color) {
        this.type = type;
        this.card = card;
        this.color = color;
    }

    public static StrategicGoalFact card(GoalType type, Card card) {
        return new StrategicGoalFact(type, card, null);
    }

    public static StrategicGoalFact color(GoalType type, GemColor color) {
        return new StrategicGoalFact(type, null, color);
    }

    public static StrategicGoalFact flag(GoalType type) {
        return new StrategicGoalFact(type, null, null);
    }

    public GoalType getType() {
        return type;
    }

    public Card getCard() {
        return card;
    }

    public GemColor getColor() {
        return color;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof StrategicGoalFact)) return false;
        StrategicGoalFact that = (StrategicGoalFact) o;
        return type == that.type && Objects.equals(card, that.card) && color == that.color;
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, card, color);
    }
}
