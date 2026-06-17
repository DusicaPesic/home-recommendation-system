package com.splendor.assistant.web;

import com.splendor.assistant.model.BoardState;
import com.splendor.assistant.model.Card;
import com.splendor.assistant.model.GemColor;
import com.splendor.assistant.model.Move;
import com.splendor.assistant.model.Noble;
import com.splendor.assistant.model.PlayerState;
import com.splendor.assistant.model.Recommendation;
import com.splendor.assistant.web.dto.BoardStateDto;
import com.splendor.assistant.web.dto.CardDto;
import com.splendor.assistant.web.dto.MoveDto;
import com.splendor.assistant.web.dto.NobleDto;
import com.splendor.assistant.web.dto.PlayerStateDto;
import com.splendor.assistant.web.dto.RecommendationResponseDto;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

public class SplendorDtoMapper {
    public RecommendationResponseDto toDto(Recommendation recommendation) {
        RecommendationResponseDto dto = new RecommendationResponseDto();
        List<MoveDto> moves = new ArrayList<>();
        for (Move move : recommendation.getRankedMoves()) {
            moves.add(toDto(move, recommendation.explainView(move)));
        }
        dto.setRankedMoves(moves);
        if (recommendation.getRecommendedMove() != null) {
            dto.setRecommendedMove(toDto(
                    recommendation.getRecommendedMove(),
                    recommendation.getExplanationView()));
        }
        return dto;
    }

    public PlayerStateDto toDto(PlayerState player) {
        PlayerStateDto dto = new PlayerStateDto();
        dto.setPrestigePoints(player.getPrestigePoints());
        dto.setTokens(copyCounts(player.getTokens()));
        dto.setGoldTokens(player.getGoldTokens());
        dto.setBonuses(copyCounts(player.getBonuses()));
        dto.setReservedCards(toCardDtos(player.getReservedCards()));
        dto.setPurchasedCards(toCardDtos(player.getPurchasedCards()));
        return dto;
    }

    public BoardStateDto toDto(BoardState board) {
        BoardStateDto dto = new BoardStateDto();
        dto.setVisibleCards(toCardDtos(board.getVisibleCards()));
        dto.setNobles(toNobleDtos(board.getNobles()));
        dto.setBankTokens(copyCounts(board.getBankTokens()));
        dto.setBankGoldTokens(board.getBankGoldTokens());
        return dto;
    }

    private MoveDto toDto(Move move, com.splendor.assistant.model.explanation.ExplanationView explanation) {
        MoveDto dto = new MoveDto();
        dto.setId(move.getId());
        dto.setType(move.getType());
        dto.setScore(move.getScore());
        dto.setTakenTokens(copyCounts(move.getTakenTokens()));
        dto.setExplanation(explanation);
        if (move.getCard() != null) {
            dto.setCard(toDto(move.getCard()));
        }
        return dto;
    }

    public CardDto toDto(Card card) {
        CardDto dto = new CardDto();
        dto.setId(card.getId());
        dto.setLevel(card.getLevel());
        dto.setColorBonus(card.getColorBonus());
        dto.setPrestigePoints(card.getPrestigePoints());
        dto.setCost(copyCounts(card.getCost()));
        return dto;
    }

    public NobleDto toDto(Noble noble) {
        NobleDto dto = new NobleDto();
        dto.setId(noble.getId());
        dto.setPrestigePoints(noble.getPrestigePoints());
        dto.setRequiredBonuses(copyCounts(noble.getRequiredBonuses()));
        return dto;
    }

    private List<CardDto> toCardDtos(List<Card> cards) {
        List<CardDto> dtos = new ArrayList<>();
        cards.forEach(card -> dtos.add(toDto(card)));
        return dtos;
    }

    private List<NobleDto> toNobleDtos(List<Noble> nobles) {
        List<NobleDto> dtos = new ArrayList<>();
        nobles.forEach(noble -> dtos.add(toDto(noble)));
        return dtos;
    }

    private EnumMap<GemColor, Integer> copyCounts(Map<GemColor, Integer> source) {
        EnumMap<GemColor, Integer> copy = new EnumMap<>(GemColor.class);
        for (GemColor color : GemColor.values()) {
            copy.put(color, source == null ? 0 : source.getOrDefault(color, 0));
        }
        return copy;
    }
}
