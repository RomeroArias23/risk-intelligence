package com.esg.riskintelligence.aiclient;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record QualitativeFlags(
        boolean hasActiveControversy,
        boolean hasRegulatorySanctions,
        boolean publishesSustainabilityReport
) {
}