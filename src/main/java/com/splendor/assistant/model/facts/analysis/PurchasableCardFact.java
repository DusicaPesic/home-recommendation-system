package com.splendor.assistant.model.facts.analysis;

import com.splendor.assistant.model.Card;
import java.util.Objects;

public class PurchasableCardFact {
    private final Card card;

    public PurchasableCardFact(Card card) {
        this.card = card;
    }

    public Card getCard() {
        return card;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PurchasableCardFact)) return false;
        PurchasableCardFact that = (PurchasableCardFact) o;
        return Objects.equals(card, that.card);
    }

    @Override
    public int hashCode() {
        return Objects.hash(card);
    }
}
