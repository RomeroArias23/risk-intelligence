package com.esg.riskintelligence.aiclient;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record ScoringRequest(
        String companyName,
        String industry,
        EnvironmentalFactors environmental,
        SocialFactors social,
        GovernanceFactors governance,
        QualitativeFlags qualitative
) {
}