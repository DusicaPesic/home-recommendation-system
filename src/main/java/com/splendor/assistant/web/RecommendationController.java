package com.splendor.assistant.web;

import com.splendor.assistant.DemoApplication;
import com.splendor.assistant.model.GameState;
import com.splendor.assistant.model.Recommendation;
import com.splendor.assistant.service.RecommendationService;
import com.splendor.assistant.web.dto.GameStateDto;
import com.splendor.assistant.web.dto.RecommendationResponseDto;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class RecommendationController {
    private final RecommendationService recommendationService;
    private final SplendorDtoMapper mapper;

    public RecommendationController() {
        this(new RecommendationService(), new SplendorDtoMapper());
    }

    public RecommendationController(RecommendationService recommendationService, SplendorDtoMapper mapper) {
        this.recommendationService = recommendationService;
        this.mapper = mapper;
    }

    @GetMapping("/sample-state")
    public GameStateDto sampleState() {
        return mapper.toDto(DemoApplication.sampleState());
    }

    @PostMapping("/recommendation")
    public RecommendationResponseDto recommend(@RequestBody GameStateDto request) {
        GameState state = mapper.toDomain(request);
        Recommendation recommendation = recommendationService.recommend(state);
        return mapper.toDto(recommendation);
    }
}
