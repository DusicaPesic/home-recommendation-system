package com.splendor.assistant.model.explanation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ExplanationView {
    private final int totalScore;
    private final List<ScoreLine> positiveScoreLines;
    private final List<ScoreLine> negativeScoreLines;
    private final ExplanationTreeNode scoreTree;
    private final List<ImpactCheck> impactChecks;

    public ExplanationView(
            int totalScore,
            List<ScoreLine> positiveScoreLines,
            List<ScoreLine> negativeScoreLines,
            ExplanationTreeNode scoreTree,
            List<ImpactCheck> impactChecks) {
        this.totalScore = totalScore;
        this.positiveScoreLines = new ArrayList<>(positiveScoreLines);
        this.negativeScoreLines = new ArrayList<>(negativeScoreLines);
        this.scoreTree = scoreTree;
        this.impactChecks = new ArrayList<>(impactChecks);
    }

    public int getTotalScore() {
        return totalScore;
    }

    public List<ScoreLine> getPositiveScoreLines() {
        return Collections.unmodifiableList(positiveScoreLines);
    }

    public List<ScoreLine> getNegativeScoreLines() {
        return Collections.unmodifiableList(negativeScoreLines);
    }

    public ExplanationTreeNode getScoreTree() {
        return scoreTree;
    }

    public List<ImpactCheck> getImpactChecks() {
        return Collections.unmodifiableList(impactChecks);
    }
}