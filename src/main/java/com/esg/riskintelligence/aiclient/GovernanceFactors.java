package com.esg.riskintelligence.aiclient;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record GovernanceFactors(
        int boardComposition,
        int businessEthics,
        int transparencyReporting,
        int riskManagement
) {
}