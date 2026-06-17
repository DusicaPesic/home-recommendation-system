package com.splendor.assistant.web;

import com.splendor.assistant.game.SplendorGame;
import com.splendor.assistant.game.SplendorGameService;
import com.splendor.assistant.model.Recommendation;
import com.splendor.assistant.web.dto.DiscardRequestDto;
import com.splendor.assistant.web.dto.GameViewDto;
import com.splendor.assistant.web.dto.MoveRequestDto;
import com.splendor.assistant.web.dto.RecommendationResponseDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/game")
public class GameController {
    private final SplendorGameService gameService;
    private final SplendorDtoMapper mapper;

    @Autowired
    public GameController(SplendorGameService gameService) {
        this(gameService, new SplendorDtoMapper());
    }

    public GameController(SplendorGameService gameService, SplendorDtoMapper mapper) {
        this.gameService = gameService;
        this.mapper = mapper;
    }

    @GetMapping
    public GameViewDto currentGame() {
        return toView(gameService.currentGame(), gameService.recommendation());
    }

    @PostMapping("/new")
    public GameViewDto newGame() {
        return toView(gameService.newGame(), gameService.recommendation());
    }

    @PostMapping("/mid-game")
    public GameViewDto midGamePreset() {
        return toView(gameService.midGamePreset(), gameService.recommendation());
    }

    @GetMapping("/recommendation")
    public RecommendationResponseDto recommendation() {
        return mapper.toDto(gameService.recommendation());
    }

    @PostMapping("/moves")
    public GameViewDto play(@RequestBody MoveRequestDto request) {
        gameService.play(request.getMoveId());
        return toView(gameService.currentGame(), gameService.recommendation());
    }

    @PostMapping("/discard")
    public GameViewDto discard(@RequestBody DiscardRequestDto request) {
        gameService.discardTokens(request.getTokens(), request.getGoldTokens());
        return toView(gameService.currentGame(), gameService.recommendation());
    }

    private GameViewDto toView(SplendorGame game, Recommendation recommendation) {
        GameViewDto view = new GameViewDto();
        view.setPlayerOne(mapper.toDto(game.getPlayerOne()));
        view.setPlayerTwo(mapper.toDto(game.getPlayerTwo()));
        view.setBoard(mapper.toDto(game.getBoard()));
        view.setCurrentPlayerNumber(game.getCurrentPlayerNumber());
        view.setFinished(game.isFinished());
        view.setWinnerPlayerNumber(game.getWinnerPlayerNumber());
        view.setLastEvent(game.getLastEvent());
        view.setRecommendation(mapper.toDto(recommendation));
        view.setWaitingForDiscard(game.isWaitingForDiscard());
        view.setDiscardPlayerNumber(game.getDiscardPlayerNumber());
        view.setDiscardCount(game.getDiscardCount());
        Map<Integer, Integer> deckCounts = new HashMap<>();
        game.getDecksByLevel().forEach((level, cards) -> deckCounts.put(level, cards.size()));
        view.setDeckCounts(deckCounts);
        return view;
    }
}
