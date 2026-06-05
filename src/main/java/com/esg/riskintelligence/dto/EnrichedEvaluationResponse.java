package com.esg.riskintelligence.dto;

import com.esg.riskintelligence.aiclient.ScoringResponse;

import java.util.UUID;

public record EnrichedEvaluationResponse(
        UUID evaluationId,
        String companyName,
        double localEsgScore,
        String localRiskLevel,
        ScoringResponse hierarchicalAnalysis
) {
}