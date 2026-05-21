package com.splendor.assistant;

import com.splendor.assistant.model.BoardState;
import com.splendor.assistant.model.Card;
import com.splendor.assistant.model.GameState;
import com.splendor.assistant.model.GemColor;
import com.splendor.assistant.model.Move;
import com.splendor.assistant.model.Noble;
import com.splendor.assistant.model.PlayerState;
import com.splendor.assistant.model.Recommendation;
import com.splendor.assistant.model.explanation.ExplanationTreeNode;
import com.splendor.assistant.model.explanation.ExplanationView;
import com.splendor.assistant.model.explanation.ImpactCheck;
import com.splendor.assistant.model.explanation.ScoreLine;
import com.splendor.assistant.service.RecommendationService;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class DemoApplication {
    public static void main(String[] args) {
        GameState state = sampleState();
        Recommendation recommendation = new RecommendationService().recommend(state);
        System.out.println("Recommended move: " + recommendation.getRecommendedMove());
        printRankedMoves(recommendation.getRankedMoves());

        Move selectedMove = chooseMove(recommendation);
        System.out.println("\nSelected move: " + selectedMove);
        printUserFriendlyExplanation(recommendation.explainView(selectedMove));

    }

    private static void printRankedMoves(List<Move> rankedMoves) {
        System.out.println("\nRanked moves:");
        for (int i = 0; i < rankedMoves.size(); i++) {
            System.out.println(" " + (i + 1) + ". " + rankedMoves.get(i));
        }
    }

    private static Move chooseMove(Recommendation recommendation) {
        List<Move> rankedMoves = recommendation.getRankedMoves();
        if (rankedMoves.isEmpty()) {
            return null;
        }

        System.out.print("\nChoose move number for detailed explanation: ");
        Scanner scanner = new Scanner(System.in);
        if (!scanner.hasNextLine()) {
            return recommendation.getRecommendedMove();
        }

        String input = scanner.nextLine().trim();
        if (input.isEmpty()) {
            return recommendation.getRecommendedMove();
        }

        try {
            int selectedIndex = Integer.parseInt(input) - 1;
            if (selectedIndex >= 0 && selectedIndex < rankedMoves.size()) {
                return rankedMoves.get(selectedIndex);
            }
        } catch (NumberFormatException ignored) {
            // Fall back to recommended move for invalid input.
        }
        return recommendation.getRecommendedMove();
    }

    private static void printUserFriendlyExplanation(ExplanationView explanation) {
        System.out.println("Total score: " + explanation.getTotalScore());

        System.out.println("\nPositive reasons:");
        if (explanation.getPositiveScoreLines().isEmpty()) {
            System.out.println(" - none");
        }
        for (ScoreLine line : explanation.getPositiveScoreLines()) {
            System.out.println(" - " + line.getFormattedPoints() + " " + line.getReason());
        }

        System.out.println("\nNegative reasons:");
        if (explanation.getNegativeScoreLines().isEmpty()) {
            System.out.println(" - none");
        }
        for (ScoreLine line : explanation.getNegativeScoreLines()) {
            System.out.println(" - " + line.getFormattedPoints() + " " + line.getReason());
        }

        System.out.println("\nImpacts:");
        int index = 1;
        for (ImpactCheck check : explanation.getImpactChecks()) {
            System.out.println(" " + index + ". " + check.getQuestion() + ": " + check.getAnswer());
            index++;
        }

        System.out.println("\nScore tree:");
        printTree(explanation.getScoreTree(), "", true);
    }

    private static void printTree(ExplanationTreeNode node, String prefix, boolean last) {
        System.out.println(prefix + (last ? "`-- " : "|-- ") + node.getLabel());
        String childPrefix = prefix + (last ? "    " : "|   ");
        for (int i = 0; i < node.getChildren().size(); i++) {
            boolean childIsLast = i == node.getChildren().size() - 1;
            printTree(node.getChildren().get(i), childPrefix, childIsLast);
        }
    }

    public static GameState sampleState() {

        // player
        PlayerState player = new PlayerState(6);
        player.setToken(GemColor.WHITE, 2);
        player.setToken(GemColor.BLUE, 1);
        player.setToken(GemColor.GREEN, 1);
        player.setGoldTokens(1);
        player.setBonus(GemColor.BLACK, 2);
        player.setBonus(GemColor.RED, 1);
        player.reserve(card("RES-WHITE-3", 2, GemColor.WHITE, 3, 2, 2, 0, 1, 2));

        // opponent
        PlayerState opponent = new PlayerState(8);
        opponent.setToken(GemColor.WHITE, 1);
        opponent.setToken(GemColor.BLUE, 2);
        opponent.setToken(GemColor.GREEN, 2);
        opponent.setGoldTokens(1);
        opponent.setBonus(GemColor.BLACK, 1);
        opponent.setBonus(GemColor.BLUE, 2);

        // bank
        BoardState board = new BoardState();
        for (GemColor color : GemColor.values()) {
            board.setBankToken(color, 4 - player.tokenCount(color) - opponent.tokenCount(color));
        }
        board.setBankGoldTokens(3);

        // nobels
        board.addNoble(Noble.noble("NOBLE-WHITE-BLUE-GREEN", 4, 4, 4, 0, 0));
        board.addNoble(Noble.noble("NOBLE-BLUE-GREEN-RED", 0, 4, 4, 4, 0));
        board.addNoble(Noble.noble("NOBLE-GREEN-RED-BLACK", 0, 0, 4, 4, 4));

        // dev cards
        board.addVisibleCard(card("L1-RED-1", 1, GemColor.RED, 1, 1, 1, 1, 1, 0));
        board.addVisibleCard(card("L1-BLUE-0", 1, GemColor.BLUE, 0, 0, 2, 1, 0, 0));
        board.addVisibleCard(card("L1-GREEN-0", 1, GemColor.GREEN, 0, 1, 0, 2, 1, 0));
        board.addVisibleCard(card("L1-WHITE-0", 1, GemColor.WHITE, 0, 0, 1, 1, 0, 2));

        board.addVisibleCard(card("L2-BLACK-4", 2, GemColor.BLACK, 4, 0, 2, 2, 2, 1));
        board.addVisibleCard(card("L2-GREEN-2", 2, GemColor.GREEN, 2, 3, 0, 2, 2, 1));
        board.addVisibleCard(card("L2-WHITE-3", 2, GemColor.WHITE, 3, 0, 3, 3, 0, 2));
        board.addVisibleCard(card("L2-BLUE-2", 2, GemColor.BLUE, 2, 2, 0, 3, 2, 0));

        board.addVisibleCard(card("L3-RED-5", 3, GemColor.RED, 5, 3, 3, 0, 0, 3));
        board.addVisibleCard(card("L3-BLACK-4", 3, GemColor.BLACK, 4, 0, 4, 4, 0, 3));
        board.addVisibleCard(card("L3-GREEN-4", 3, GemColor.GREEN, 4, 3, 0, 0, 4, 4));
        board.addVisibleCard(card("L3-WHITE-3", 3, GemColor.WHITE, 3, 0, 3, 5, 3, 0));
        return new GameState(player, opponent, board);
    }

    private static Card card(String id, int level, GemColor bonus, int points, int white, int blue, int green, int red, int black) {
        Map<GemColor, Integer> cost = new EnumMap<>(GemColor.class);
        cost.put(GemColor.WHITE, white);
        cost.put(GemColor.BLUE, blue);
        cost.put(GemColor.GREEN, green);
        cost.put(GemColor.RED, red);
        cost.put(GemColor.BLACK, black);
        return new Card(id, level, bonus, points, cost);
    }
}
