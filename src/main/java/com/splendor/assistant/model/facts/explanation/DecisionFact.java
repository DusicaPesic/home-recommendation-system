package com.splendor.assistant.model.facts.explanation;

import com.splendor.assistant.model.Card;
import com.splendor.assistant.model.CardEfficiency;
import com.splendor.assistant.model.GemColor;
import com.splendor.assistant.model.GamePhase;
import com.splendor.assistant.model.GoalType;
import com.splendor.assistant.model.Move;
import org.kie.api.definition.type.Position;

import java.util.Objects;

public class DecisionFact {
    @Position(0)
    private final String conclusion;

    @Position(1)
    private final String prerequisite;

    private final DecisionLevel level;
    private final String explanation;

    public DecisionFact(String conclusion, String prerequisite, DecisionLevel level, String explanation) {
        this.conclusion = conclusion;
        this.prerequisite = prerequisite;
        this.level = level;
        this.explanation = explanation;
    }

    public static DecisionFact of(String conclusion, String prerequisite, DecisionLevel level, String explanation) {
        return new DecisionFact(conclusion, prerequisite, level, explanation);
    }

    public static String move(Move move) {
        return "move:" + move.getId();
    }

    public static String goal(GoalType type, Card card) {
        return "goal:" + type + ":" + card.getId();
    }

    public static String goal(GoalType type, GemColor color) {
        return "goal:" + type + ":" + color;
    }

    public static String goal(GoalType type) {
        return "goal:" + type;
    }

    public static String analysis(String type, Card card) {
        return "analysis:" + type + ":" + card.getId();
    }

    public static String analysis(String type, Card card, CardEfficiency efficiency) {
        return "analysis:" + type + ":" + efficiency + ":" + card.getId();
    }

    public static String analysis(String type, GemColor color) {
        return "analysis:" + type + ":" + color;
    }

    public static String analysis(String type, GamePhase phase) {
        return "analysis:" + type + ":" + phase;
    }

    public static String analysis(String type) {
        return "analysis:" + type;
    }

    public static String base(String description) {
        return "base:" + description;
    }

    public String getConclusion() {
        return conclusion;
    }

    public String getPrerequisite() {
        return prerequisite;
    }

    public DecisionLevel getLevel() {
        return level;
    }

    public String getExplanation() {
        return explanation;
    }

    public boolean startsFromBaseFact() {
        return prerequisite.startsWith("base:");
    }

    public static String describe(String key) {
        if (key.startsWith("move:")) {
            return "move " + key.substring("move:".length());
        }
        if (key.startsWith("goal:")) {
            return describeGoal(key.substring("goal:".length()));
        }
        if (key.startsWith("analysis:")) {
            return describeAnalysis(key.substring("analysis:".length()));
        }
        if (key.startsWith("base:")) {
            return describeBase(key.substring("base:".length()));
        }
        return key;
    }

    private static String describeBase(String value) {
        if (value.contains(" has at least 3 points and total cost at most 7")) {
            String card = value.replace(" has at least 3 points and total cost at most 7", "");
            return "card " + card + " has at least 3 points and total cost at most 7";
        }
        if (value.contains(" has at least 1 point and total cost at most 5")) {
            String card = value.replace(" has at least 1 point and total cost at most 5", "");
            return "card " + card + " has at least 1 point and total cost at most 5";
        }
        if (value.contains(" costs more than 8 and gives at most 2 points")) {
            String card = value.replace(" costs more than 8 and gives at most 2 points", "");
            return "card " + card + " is expensive and gives at most 2 points";
        }
        if (value.startsWith("at least two efficient cards require ")) {
            String color = value.replace("at least two efficient cards require ", "");
            return "at least two efficient cards require " + color;
        }
        if (value.startsWith("at least two efficient cards give ")) {
            String color = value.replace("at least two efficient cards give ", "").replace(" bonus", "");
            return "at least two efficient cards give a " + color + " bonus";
        }
        if (value.startsWith("player can pay ")) {
            String card = value.replace("player can pay ", "").replace(" using tokens, bonuses and gold", "");
            return "the player can pay for " + card + " using tokens, bonuses and gold";
        }
        if (value.startsWith("player misses at most two tokens for ")) {
            String card = value.replace("player misses at most two tokens for ", "");
            return "the player is missing at most two tokens for " + card;
        }
        if (value.startsWith("opponent misses at most two tokens for ")) {
            String card = value.replace("opponent misses at most two tokens for ", "");
            return "the opponent is missing at most two tokens for " + card;
        }
        if (value.startsWith("reserved card ") && value.endsWith(" can be played soon")) {
            String card = value.replace("reserved card ", "").replace(" can be played soon", "");
            return "reserved card " + card + " can be played soon";
        }
        if (value.endsWith(" is in player's reserved cards")) {
            String card = value.replace(" is in player's reserved cards", "");
            return "card " + card + " is already in the player's reserved cards";
        }
        if ("game is not in late phase".equals(value)) {
            return "the game is not in the late phase";
        }
        if ("player has fewer than three reserved cards and card is not purchasable".equals(value)) {
            return "the player has room to reserve and the card is not purchasable yet";
        }
        if ("player has space to reserve a card".equals(value)) {
            return "the player has room to reserve a card";
        }
        if (value.startsWith("bank has available ")) {
            String color = value.replace("bank has available ", "").replace(" tokens", "");
            return "the bank has available " + color + " tokens";
        }
        if ("player has at least 9 tokens".equals(value)) {
            return "the player has at least 9 tokens and is close to the limit";
        }
        if (value.startsWith("player reaches at least 15 prestige points after buying ")) {
            String card = value.replace("player reaches at least 15 prestige points after buying ", "");
            return "buying " + card + " makes the player reach at least 15 prestige points";
        }
        if (value.startsWith("move takes three different colors")) {
            return "the move takes three different colors and at least two of them are needed";
        }
        if ("move does not take any needed color".equals(value)) {
            return "the move does not take any currently needed color";
        }
        if ("player would have more than 10 tokens after this move".equals(value)) {
            return "the player would have more than 10 tokens after this move";
        }
        return value;
    }

    private static String describeGoal(String value) {
        String[] parts = value.split(":", 2);
        String goal = parts[0];
        String target = parts.length > 1 ? parts[1] : "";

        if ("BUY_CARD".equals(goal)) {
            return "goal is to buy card " + target;
        }
        if ("RESERVE_CARD".equals(goal)) {
            return "goal is to reserve card " + target;
        }
        if ("BUILD_DOMINANT_COLOR".equals(goal)) {
            return "goal is to build dominant color " + target;
        }
        if ("BLOCK_OPPONENT".equals(goal)) {
            return "goal is to block the opponent with card " + target;
        }
        if ("COLLECT_TOKENS".equals(goal)) {
            return "goal is to collect needed tokens of color " + target;
        }
        if ("PLAY_RESERVED_CARD".equals(goal)) {
            return "goal is to play reserved card " + target;
        }
        if ("MANAGE_TOKEN_LIMIT".equals(goal)) {
            return "goal is to manage the token limit";
        }
        return "goal " + value;
    }

    private static String describeAnalysis(String value) {
        String[] parts = value.split(":");
        String type = parts[0];

        if ("GAME_PHASE".equals(type) && parts.length > 1) {
            return "game phase is " + parts[1];
        }
        if ("CARD_EFFICIENCY".equals(type) && parts.length > 2) {
            return "card " + parts[2] + " has efficiency " + parts[1];
        }
        if ("DOMINANT_COLOR".equals(type) && parts.length > 1) {
            return "dominant color is " + parts[1];
        }
        if ("PURCHASABLE_CARD".equals(type) && parts.length > 1) {
            return "card " + parts[1] + " is immediately purchasable";
        }
        if ("ALMOST_PURCHASABLE_CARD".equals(type) && parts.length > 1) {
            return "card " + parts[1] + " is almost purchasable";
        }
        if ("OPPONENT_THREAT_CARD".equals(type) && parts.length > 1) {
            return "card " + parts[1] + " is a threat because the opponent is close to buying it";
        }
        if ("NEEDED_COLOR".equals(type) && parts.length > 1) {
            return "needed color is " + parts[1];
        }
        if ("SUPPORTS_EFFICIENCY_ENGINE".equals(type) && parts.length > 1) {
            return "card " + parts[1] + " supports the high efficiency engine";
        }
        if ("TOKEN_LIMIT_RISK".equals(type)) {
            return "there is token limit risk";
        }
        return "analysis " + value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DecisionFact)) return false;
        DecisionFact that = (DecisionFact) o;
        return Objects.equals(conclusion, that.conclusion)
                && Objects.equals(prerequisite, that.prerequisite)
                && level == that.level
                && Objects.equals(explanation, that.explanation);
    }

    @Override
    public int hashCode() {
        return Objects.hash(conclusion, prerequisite, level, explanation);
    }
}
