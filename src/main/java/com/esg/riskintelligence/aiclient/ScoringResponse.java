package com.esg.riskintelligence.aiclient;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record ScoringResponse(
        String companyName,
        String industry,
        DimensionScore environmental,
        DimensionScore social,
        DimensionScore governance,
        double baseScore,
        double qualitativeAdjustment,
        double finalScore,
        String riskLevel,
        String recommendation
) {
}