package com.splendor.assistant.web.dto;

import java.util.ArrayList;
import java.util.List;

public class RecommendationResponseDto {
    private MoveDto recommendedMove;
    private List<MoveDto> rankedMoves = new ArrayList<>();

    public MoveDto getRecommendedMove() {
        return recommendedMove;
    }

    public void setRecommendedMove(MoveDto recommendedMove) {
        this.recommendedMove = recommendedMove;
    }

    public List<MoveDto> getRankedMoves() {
        return rankedMoves;
    }

    public void setRankedMoves(List<MoveDto> rankedMoves) {
        this.rankedMoves = rankedMoves;
    }
}
