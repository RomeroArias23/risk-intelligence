package com.esg.riskintelligence.mapper;

import com.esg.riskintelligence.dto.CreditEvaluationResponse;
import com.esg.riskintelligence.entity.CreditEvaluation;
import org.springframework.stereotype.Component;

@Component
public class CreditEvaluationMapper {

    public CreditEvaluationResponse toResponse(CreditEvaluation entity) {
        return new CreditEvaluationResponse(
                entity.getId(),
                entity.getCompanyName(),
                entity.getEsgScore(),
                entity.getRiskLevel(),
                entity.getRecommendation()
        );
    }
}