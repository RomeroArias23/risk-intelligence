package com.esg.riskintelligence.aiclient;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record EnvironmentalFactors(
        int ghgEmissions,
        int energyManagement,
        int waterManagement,
        int wasteManagement,
        int climateTransition
) {
}