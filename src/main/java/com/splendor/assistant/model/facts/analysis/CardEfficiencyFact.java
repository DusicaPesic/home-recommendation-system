package com.splendor.assistant.model.facts.analysis;

import com.splendor.assistant.model.Card;
import com.splendor.assistant.model.CardEfficiency;
import java.util.Objects;

public class CardEfficiencyFact {
    private final Card card;
    private final CardEfficiency efficiency;

    public CardEfficiencyFact(Card card, CardEfficiency efficiency) {
        this.card = card;
        this.efficiency = efficiency;
    }

    public Card getCard() {
        return card;
    }

    public CardEfficiency getEfficiency() {
        return efficiency;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CardEfficiencyFact)) return false;
        CardEfficiencyFact that = (CardEfficiencyFact) o;
        return Objects.equals(card, that.card) && efficiency == that.efficiency;
    }

    @Override
    public int hashCode() {
        return Objects.hash(card, efficiency);
    }
}
