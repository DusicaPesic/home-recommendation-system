package com.splendor.assistant.model;

import com.splendor.assistant.model.explanation.ExplanationView;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public class Recommendation {
    private final List<Move> rankedMoves;
    private final Move recommendedMove;
    private final Map<Move, ExplanationView> explanationViews;

    public Recommendation(
            List<Move> rankedMoves,
            Move recommendedMove,
            Map<Move, ExplanationView> explanationViews) {
        this.rankedMoves = rankedMoves;
        this.recommendedMove = recommendedMove;
        this.explanationViews = explanationViews;
    }

    public List<Move> getRankedMoves() {
        return Collections.unmodifiableList(rankedMoves);
    }

    public Move getRecommendedMove() {
        return recommendedMove;
    }

    public ExplanationView explainView(Move move) {
        return explanationViews.get(move);
    }

    public ExplanationView getExplanationView() {
        return explainView(recommendedMove);
    }
}