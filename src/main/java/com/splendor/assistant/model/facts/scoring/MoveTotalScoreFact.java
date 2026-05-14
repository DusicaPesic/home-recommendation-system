package com.splendor.assistant.model.facts.scoring;

import com.splendor.assistant.model.Move;
import java.util.Objects;

public class MoveTotalScoreFact {
    private final Move move;
    private final int totalScore;

    public MoveTotalScoreFact(Move move, int totalScore) {
        this.move = move;
        this.totalScore = totalScore;
    }

    public Move getMove() {
        return move;
    }

    public int getTotalScore() {
        return totalScore;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MoveTotalScoreFact)) return false;
        MoveTotalScoreFact that = (MoveTotalScoreFact) o;
        return totalScore == that.totalScore && Objects.equals(move, that.move);
    }

    @Override
    public int hashCode() {
        return Objects.hash(move, totalScore);
    }
}
