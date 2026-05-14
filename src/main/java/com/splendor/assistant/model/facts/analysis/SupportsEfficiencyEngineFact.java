package com.splendor.assistant.model.facts.analysis;

import com.splendor.assistant.model.Card;
import java.util.Objects;

public class SupportsEfficiencyEngineFact {
    private final Card card;

    public SupportsEfficiencyEngineFact(Card card) {
        this.card = card;
    }

    public Card getCard() {
        return card;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SupportsEfficiencyEngineFact)) return false;
        SupportsEfficiencyEngineFact that = (SupportsEfficiencyEngineFact) o;
        return Objects.equals(card, that.card);
    }

    @Override
    public int hashCode() {
        return Objects.hash(card);
    }
}
