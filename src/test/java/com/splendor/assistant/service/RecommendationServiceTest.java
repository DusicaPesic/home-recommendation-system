package com.splendor.assistant.service;

import com.splendor.assistant.DemoApplication;
import com.splendor.assistant.model.Move;
import com.splendor.assistant.model.Recommendation;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RecommendationServiceTest {
    @Test
    void recommendsRankedMoveWithExplanation() {
        Recommendation recommendation = new RecommendationService().recommend(DemoApplication.sampleState());

        assertFalse(recommendation.getRankedMoves().isEmpty());
        Move best = recommendation.getRecommendedMove();
        assertNotNull(best);
        assertTrue(best.getScore() >= recommendation.getRankedMoves().get(recommendation.getRankedMoves().size() - 1).getScore());
        assertNotNull(recommendation.getExplanationView());
        assertFalse(recommendation.getExplanationView().getPositiveScoreLines().isEmpty(), "user-friendly explanation should include score lines");
        assertNotNull(recommendation.getExplanationView().getScoreTree());
        assertFalse(recommendation.getExplanationView().getImpactChecks().isEmpty(), "impact checks should be built with backward query results");
    }
}
