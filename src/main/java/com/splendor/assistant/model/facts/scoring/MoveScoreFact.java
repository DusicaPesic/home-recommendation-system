package com.splendor.assistant.model.facts.scoring;

import com.splendor.assistant.model.Move;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class MoveScoreFact {
    private final Move move;
    private final int points;
    private final String reason;
    private final List<String> prerequisiteKeys;

    public MoveScoreFact(Move move, int points, String reason) {
        this(move, points, reason, new String[0]);
    }

    public MoveScoreFact(Move move, int points, String reason, String... prerequisiteKeys) {
        this.move = move;
        this.points = points;
        this.reason = reason;
        this.prerequisiteKeys = Arrays.asList(prerequisiteKeys);
    }

    public Move getMove() {
        return move;
    }

    public int getPoints() {
        return points;
    }

    public String getReason() {
        return reason;
    }

    public List<String> getPrerequisiteKeys() {
        return Collections.unmodifiableList(prerequisiteKeys);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MoveScoreFact)) return false;
        MoveScoreFact that = (MoveScoreFact) o;
        return points == that.points
                && Objects.equals(move, that.move)
                && Objects.equals(reason, that.reason);
    }

    @Override
    public int hashCode() {
        return Objects.hash(move, points, reason);
    }
}
