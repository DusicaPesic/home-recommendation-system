package com.splendor.assistant.model.explanation;

public class ImpactCheck {
    private final String question;
    private final boolean impacted;

    public ImpactCheck(String question, boolean impacted) {
        this.question = question;
        this.impacted = impacted;
    }

    public String getQuestion() {
        return question;
    }

    public boolean isImpacted() {
        return impacted;
    }

    public String getAnswer() {
        return impacted ? "YES" : "NO";
    }
}