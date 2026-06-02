package com.esg.riskintelligence.dto;

import java.util.UUID;

public record CreditEvaluationResponse(
        UUID id,
        String companyName,
        double esgScore,
        String riskLevel,
        String recommendation
) {
}