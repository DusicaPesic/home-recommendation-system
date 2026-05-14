package com.splendor.assistant.service;

import com.splendor.assistant.model.BoardState;
import com.splendor.assistant.model.CardEfficiency;
import com.splendor.assistant.model.Card;
import com.splendor.assistant.model.GameState;
import com.splendor.assistant.model.GemColor;
import com.splendor.assistant.model.GamePhase;
import com.splendor.assistant.model.GoalType;
import com.splendor.assistant.model.Move;
import com.splendor.assistant.model.Recommendation;
import com.splendor.assistant.model.explanation.ExplanationTreeNode;
import com.splendor.assistant.model.explanation.ExplanationView;
import com.splendor.assistant.model.explanation.ImpactCheck;
import com.splendor.assistant.model.explanation.ScoreLine;
import com.splendor.assistant.model.facts.explanation.DecisionFact;
import com.splendor.assistant.model.facts.scoring.MoveScoreFact;
import org.kie.api.KieServices;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.QueryResults;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class RecommendationService {
    private final KieContainer kieContainer;
    private final MoveGenerator moveGenerator;

    public RecommendationService() {
        this(KieServices.Factory.get().getKieClasspathContainer(), new MoveGenerator());
    }

    public RecommendationService(KieContainer kieContainer, MoveGenerator moveGenerator) {
        this.kieContainer = kieContainer;
        this.moveGenerator = moveGenerator;
    }

    public Recommendation recommend(GameState state) {
        List<Move> legalMoves = moveGenerator.generate(state);
        KieSession session = kieContainer.newKieSession("splendorKSession");
        try {
            insertCurrentState(session, state);
            insertColors(session);
            insertLegalMoves(session, legalMoves);
            session.fireAllRules();

            List<MoveScoreFact> scoreFacts = getMoveScoreFacts(session);

            List<Move> ranked = rankMoves(legalMoves);
            Map<Move, List<MoveScoreFact>> scoreFactsByMove = groupScoreFactsByMove(scoreFacts);
            Move best = ranked.isEmpty() ? null : ranked.get(0);
            List<DecisionFact> decisionFacts = getDecisionFacts(session);
            Map<Move, ExplanationView> explanationViews = buildExplanationViews(ranked, scoreFactsByMove, decisionFacts, session);
            return new Recommendation(ranked, best, explanationViews);
        } finally {
            session.dispose();
        }
    }

    private void insertCards(KieSession session, BoardState board) {
        for (Card card : board.getVisibleCards()) {
            session.insert(card);
        }
    }

    private void insertCurrentState(KieSession session, GameState state) {
        session.insert(state);
        session.insert(state.getPlayer());
        session.insert(state.getBoard());
        insertCards(session, state.getBoard());
        state.getPlayer().getReservedCards().forEach(session::insert);
    }

    private void insertColors(KieSession session) {
        for (GemColor color : GemColor.values()) {
            session.insert(color);
        }
    }

    private void insertLegalMoves(KieSession session, List<Move> legalMoves) {
        legalMoves.forEach(session::insert);
    }

    private List<MoveScoreFact> getMoveScoreFacts(KieSession session) {
        List<MoveScoreFact> scoreFacts = new ArrayList<>();
        session.getObjects(o -> o instanceof MoveScoreFact)
                .forEach(o -> scoreFacts.add((MoveScoreFact) o));
        return scoreFacts;
    }

    private List<DecisionFact> getDecisionFacts(KieSession session) {
        List<DecisionFact> facts = new ArrayList<>();
        session.getObjects(o -> o instanceof DecisionFact)
                .forEach(o -> facts.add((DecisionFact) o));
        return facts;
    }

    private List<Move> rankMoves(List<Move> legalMoves) {
        List<Move> ranked = new ArrayList<>(legalMoves);
        ranked.sort(Comparator.comparingInt(Move::getScore).reversed().thenComparing(Move::getId));
        return ranked;
    }

    private Map<Move, List<MoveScoreFact>> groupScoreFactsByMove(List<MoveScoreFact> scoreFacts) {
        Map<Move, List<MoveScoreFact>> scoreFactsByMove = new HashMap<>();
        for (MoveScoreFact scoreFact : scoreFacts) {
            Move move = scoreFact.getMove();
            if (!scoreFactsByMove.containsKey(move)) {
                scoreFactsByMove.put(move, new ArrayList<>());
            }
            scoreFactsByMove.get(move).add(scoreFact);
        }
        return scoreFactsByMove;
    }

    private Map<Move, ExplanationView> buildExplanationViews(
            List<Move> rankedMoves,
            Map<Move, List<MoveScoreFact>> scoreFactsByMove,
            List<DecisionFact> decisionFacts,
            KieSession session) {
        Map<Move, ExplanationView> views = new LinkedHashMap<>();
        for (Move move : rankedMoves) {
            views.put(move, buildExplanationView(move, scoreFactsByMove, decisionFacts, session));
        }
        return views;
    }
    private ExplanationView buildExplanationView(
            Move selectedMove,
            Map<Move, List<MoveScoreFact>> scoreFactsByMove,
            List<DecisionFact> decisionFacts,
            KieSession session) {
        if (selectedMove == null) {
            return new ExplanationView(
                    0,
                    new ArrayList<>(),
                    new ArrayList<>(),
                    new ExplanationTreeNode("There is no legal move to recommend."),
                    new ArrayList<>());
        }

        List<MoveScoreFact> scoreFacts = scoreFactsByMove.getOrDefault(selectedMove, new ArrayList<>());
        List<ScoreLine> positiveLines = buildScoreLines(scoreFacts, true);
        List<ScoreLine> negativeLines = buildScoreLines(scoreFacts, false);
        ExplanationTreeNode scoreTree = buildScoreTree(selectedMove, scoreFacts, decisionFacts);
        List<ImpactCheck> impactChecks = buildImpactChecks(selectedMove, session);
        return new ExplanationView(selectedMove.getScore(), positiveLines, negativeLines, scoreTree, impactChecks);
    }

    private List<ImpactCheck> buildImpactChecks(Move best, KieSession session) {
        List<ImpactCheck> checks = new ArrayList<>();
        String moveKey = DecisionFact.move(best);
        Card card = best.getCard();

        checks.add(new ImpactCheck(
                "The selected card is very efficient",
                card != null && isExplainedBy(session, moveKey, DecisionFact.analysis("CARD_EFFICIENCY", card, CardEfficiency.VERY_HIGH))));
        checks.add(new ImpactCheck(
                "The move is penalized for low efficiency",
                card != null && isExplainedBy(session, moveKey, DecisionFact.analysis("CARD_EFFICIENCY", card, CardEfficiency.LOW))));
        checks.add(new ImpactCheck(
                "The selected card supports the engine",
                card != null && isExplainedBy(session, moveKey, DecisionFact.analysis("SUPPORTS_EFFICIENCY_ENGINE", card))));
        checks.add(new ImpactCheck(
                "The move develops the dominant color",
                moveDevelopsDominantColor(best, session, moveKey)));
        checks.add(new ImpactCheck(
                "The move collects needed tokens",
                moveCollectsNeededTokens(best, session, moveKey)));
        checks.add(new ImpactCheck(
                "The move blocks the opponent",
                card != null && isExplainedBy(session, moveKey, DecisionFact.goal(GoalType.BLOCK_OPPONENT, card))));
        checks.add(new ImpactCheck(
                "The move creates a token limit problem",
                isExplainedBy(session, moveKey,
                        DecisionFact.base("player would have more than 10 tokens after this move"))));

        return checks;
    }

    private boolean moveDevelopsDominantColor(Move best, KieSession session, String moveKey) {
        Card card = best.getCard();
        if (card != null) {
            return isExplainedBy(session, moveKey, DecisionFact.goal(GoalType.BUILD_DOMINANT_COLOR, card.getColorBonus()))
                    || isExplainedBy(session, moveKey, DecisionFact.analysis("DOMINANT_COLOR", card.getColorBonus()));
        }

        for (GemColor color : GemColor.values()) {
            if (best.takes(color) && isExplainedBy(session, moveKey, DecisionFact.analysis("DOMINANT_COLOR", color))) {
                return true;
            }
        }
        return false;
    }

    private boolean moveCollectsNeededTokens(Move best, KieSession session, String moveKey) {
        for (GemColor color : GemColor.values()) {
            if (best.takes(color)
                    && (isExplainedBy(session, moveKey, DecisionFact.goal(GoalType.COLLECT_TOKENS, color))
                    || isExplainedBy(session, moveKey, DecisionFact.analysis("NEEDED_COLOR", color)))) {
                return true;
            }
        }
        return false;
    }

    private boolean isExplainedBy(KieSession session, String conclusion, String prerequisite) {
        QueryResults results = session.getQueryResults("explainsDecision", conclusion, prerequisite);
        return results.iterator().hasNext();
    }
    private List<ScoreLine> buildScoreLines(List<MoveScoreFact> scoreFacts, boolean positive) {
        List<ScoreLine> lines = new ArrayList<>();
        for (MoveScoreFact scoreFact : scoreFacts) {
            boolean scoreIsPositive = scoreFact.getPoints() >= 0;
            if (scoreIsPositive == positive) {
                lines.add(new ScoreLine(scoreFact.getPoints(), scoreFact.getReason()));
            }
        }

        if (positive) {
            lines.sort(Comparator.comparingInt(ScoreLine::getPoints).reversed().thenComparing(ScoreLine::getReason));
        } else {
            lines.sort(Comparator.comparingInt(ScoreLine::getPoints).thenComparing(ScoreLine::getReason));
        }
        return lines;
    }

    private ExplanationTreeNode buildScoreTree(
            Move move,
            List<MoveScoreFact> scoreFacts,
            List<DecisionFact> facts) {
        ExplanationTreeNode root = new ExplanationTreeNode(
                "Accumulate total score for " + move.getId() + " = " + move.getScore());

        List<MoveScoreFact> sortedScoreFacts = new ArrayList<>(scoreFacts);
        sortedScoreFacts.sort(Comparator.comparingInt(MoveScoreFact::getPoints).reversed().thenComparing(MoveScoreFact::getReason));

        for (MoveScoreFact scoreFact : sortedScoreFacts) {
            if (!move.equals(scoreFact.getMove())) {
                continue;
            }

            ExplanationTreeNode scoreNode = new ExplanationTreeNode(
                    "Score " + formatSignedPoints(scoreFact.getPoints()) + ": " + scoreFact.getReason());
            root.addChild(scoreNode);

            for (String prerequisiteKey : scoreFact.getPrerequisiteKeys()) {
                scoreNode.addChild(buildDependencyTree(prerequisiteKey, facts, new LinkedHashSet<>()));
            }
        }
        return root;
    }

    private ExplanationTreeNode buildDependencyTree(String key, List<DecisionFact> facts, Set<String> visited) {
        ExplanationTreeNode node = new ExplanationTreeNode(formatDecisionStep(key));
        if (visited.contains(key)) {
            node.addChild(new ExplanationTreeNode("already visited node, stopping recursion"));
            return node;
        }

        if (key.startsWith("base:")) {
            return node;
        }

        visited.add(key);
        for (DecisionFact fact : facts) {
            if (fact.getConclusion().equals(key)) {
                node.addChild(buildDependencyTree(fact.getPrerequisite(), facts, visited));
            }
        }
        visited.remove(key);
        return node;
    }

    private String formatDecisionStep(String key) {
        String description = DecisionFact.describe(key);
        if (key.startsWith("base:")) {
            return "Base fact: " + description;
        }
        if (key.startsWith("analysis:")) {
            return "Analysis: " + description;
        }
        if (key.startsWith("goal:")) {
            return "Strategic goal: " + description;
        }
        return description;
    }

    private String formatSignedPoints(int points) {
        if (points > 0) {
            return "+" + points;
        }
        return String.valueOf(points);
    }
}
