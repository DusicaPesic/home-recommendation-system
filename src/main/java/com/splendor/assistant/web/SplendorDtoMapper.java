package com.splendor.assistant.web;

import com.splendor.assistant.model.BoardState;
import com.splendor.assistant.model.Card;
import com.splendor.assistant.model.GameState;
import com.splendor.assistant.model.GemColor;
import com.splendor.assistant.model.Move;
import com.splendor.assistant.model.Noble;
import com.splendor.assistant.model.PlayerState;
import com.splendor.assistant.model.Recommendation;
import com.splendor.assistant.web.dto.BoardStateDto;
import com.splendor.assistant.web.dto.CardDto;
import com.splendor.assistant.web.dto.GameStateDto;
import com.splendor.assistant.web.dto.MoveDto;
import com.splendor.assistant.web.dto.NobleDto;
import com.splendor.assistant.web.dto.PlayerStateDto;
import com.splendor.assistant.web.dto.RecommendationResponseDto;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

public class SplendorDtoMapper {
    public GameState toDomain(GameStateDto dto) {
        return new GameState(
                toDomain(dto.getPlayer()),
                toDomain(dto.getOpponent()),
                toDomain(dto.getBoard()));
    }

    public GameStateDto toDto(GameState state) {
        GameStateDto dto = new GameStateDto();
        dto.setPlayer(toDto(state.getPlayer()));
        dto.setOpponent(toDto(state.getOpponent()));
        dto.setBoard(toDto(state.getBoard()));
        return dto;
    }

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

    private PlayerState toDomain(PlayerStateDto dto) {
        PlayerState player = new PlayerState(dto.getPrestigePoints());
        copyCounts(dto.getTokens()).forEach(player::setToken);
        player.setGoldTokens(dto.getGoldTokens());
        copyCounts(dto.getBonuses()).forEach(player::setBonus);
        safeCards(dto.getReservedCards()).forEach(card -> player.reserve(toDomain(card)));
        safeCards(dto.getPurchasedCards()).forEach(card -> player.purchase(toDomain(card)));
        return player;
    }

    private BoardState toDomain(BoardStateDto dto) {
        BoardState board = new BoardState();
        safeCards(dto.getVisibleCards()).forEach(card -> board.addVisibleCard(toDomain(card)));
        safeNobles(dto.getNobles()).forEach(noble -> board.addNoble(toDomain(noble)));
        copyCounts(dto.getBankTokens()).forEach(board::setBankToken);
        board.setBankGoldTokens(dto.getBankGoldTokens());
        return board;
    }

    private Card toDomain(CardDto dto) {
        return new Card(
                dto.getId(),
                dto.getLevel(),
                dto.getColorBonus(),
                dto.getPrestigePoints(),
                copyCounts(dto.getCost()));
    }

    private Noble toDomain(NobleDto dto) {
        return new Noble(dto.getId(), copyCounts(dto.getRequiredBonuses()), dto.getPrestigePoints());
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

    private List<CardDto> safeCards(List<CardDto> cards) {
        return cards == null ? new ArrayList<>() : cards;
    }

    private List<NobleDto> safeNobles(List<NobleDto> nobles) {
        return nobles == null ? new ArrayList<>() : nobles;
    }

    private EnumMap<GemColor, Integer> copyCounts(Map<GemColor, Integer> source) {
        EnumMap<GemColor, Integer> copy = new EnumMap<>(GemColor.class);
        for (GemColor color : GemColor.values()) {
            copy.put(color, source == null ? 0 : source.getOrDefault(color, 0));
        }
        return copy;
    }
}
