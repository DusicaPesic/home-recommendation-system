package com.splendor.assistant.model.facts.analysis;

import com.splendor.assistant.model.GemColor;
import java.util.Objects;

public class NeededColorFact {
    private final GemColor color;

    public NeededColorFact(GemColor color) {
        this.color = color;
    }

    public GemColor getColor() {
        return color;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof NeededColorFact)) return false;
        NeededColorFact that = (NeededColorFact) o;
        return color == that.color;
    }

    @Override
    public int hashCode() {
        return Objects.hash(color);
    }
}
