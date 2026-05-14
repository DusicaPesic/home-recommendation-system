package com.splendor.assistant.model.facts.analysis;

import com.splendor.assistant.model.GamePhase;
import java.util.Objects;

public class GamePhaseFact {
    private final GamePhase phase;

    public GamePhaseFact(GamePhase phase) {
        this.phase = phase;
    }

    public GamePhase getPhase() {
        return phase;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof GamePhaseFact)) return false;
        GamePhaseFact that = (GamePhaseFact) o;
        return phase == that.phase;
    }

    @Override
    public int hashCode() {
        return Objects.hash(phase);
    }
}
