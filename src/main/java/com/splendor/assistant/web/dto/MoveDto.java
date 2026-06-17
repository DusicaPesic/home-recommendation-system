package com.splendor.assistant.web.dto;

import com.splendor.assistant.model.GemColor;
import com.splendor.assistant.model.MoveType;
import com.splendor.assistant.model.explanation.ExplanationView;

import java.util.Map;

public class MoveDto {
    private String id;
    private MoveType type;
    private CardDto card;
    private Map<GemColor, Integer> takenTokens;
    private int score;
    private ExplanationView explanation;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public MoveType getType() {
        return type;
    }

    public void setType(MoveType type) {
        this.type = type;
    }

    public CardDto getCard() {
        return card;
    }

    public void setCard(CardDto card) {
        this.card = card;
    }

    public Map<GemColor, Integer> getTakenTokens() {
        return takenTokens;
    }

    public void setTakenTokens(Map<GemColor, Integer> takenTokens) {
        this.takenTokens = takenTokens;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public ExplanationView getExplanation() {
        return explanation;
    }

    public void setExplanation(ExplanationView explanation) {
        this.explanation = explanation;
    }
}
