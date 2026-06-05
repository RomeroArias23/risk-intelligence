package com.esg.riskintelligence.aiclient;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record SocialFactors(
        int laborPractices,
        int healthSafety,
        int communityRelations,
        int supplyChain,
        int customerWelfare
) {
}