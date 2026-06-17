package com.splendor.assistant.service;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ScoringTemplateRulesTest {
    @Test
    void generatesScoringRulesFromTemplateTable() {
        String drl = new ScoringTemplateRules().generateDrl();

        assertTrue(drl.contains("rule \"Score buy high efficiency card\""));
        assertTrue(drl.contains("rule \"Score reserve blocking card\""));
        assertTrue(drl.contains("rule \"Score buy for managing token limit\""));
        assertTrue(drl.contains("StrategicGoalFact(type == GoalType.BUY_CARD, card == $card)"));
        assertTrue(drl.contains("DecisionFact.goal(GoalType.BUILD_DOMINANT_COLOR, $card.getColorBonus())"));
        assertFalse(drl.contains("rule \"Score take needed color\""));
        assertTrue(drl.contains("new MoveScoreFact"));
    }
}
