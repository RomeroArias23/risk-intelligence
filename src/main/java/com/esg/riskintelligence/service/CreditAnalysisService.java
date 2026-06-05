package com.esg.riskintelligence.service;

import com.esg.riskintelligence.aiclient.AiServiceClient;
import com.esg.riskintelligence.aiclient.EnvironmentalFactors;
import com.esg.riskintelligence.aiclient.GovernanceFactors;
import com.esg.riskintelligence.aiclient.QualitativeFlags;
import com.esg.riskintelligence.aiclient.ScoringRequest;
import com.esg.riskintelligence.aiclient.ScoringResponse;
import com.esg.riskintelligence.aiclient.SocialFactors;
import com.esg.riskintelligence.dto.EnrichedEvaluationResponse;
import com.esg.riskintelligence.entity.CreditEvaluation;
import com.esg.riskintelligence.error.EvaluationNotFoundException;
import com.esg.riskintelligence.repository.CreditEvaluationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class CreditAnalysisService {

    private final CreditEvaluationRepository repository;
    private final AiServiceClient aiServiceClient;

    public CreditAnalysisService(
            CreditEvaluationRepository repository,
            AiServiceClient aiServiceClient) {
        this.repository = repository;
        this.aiServiceClient = aiServiceClient;
    }

    /**
     * Performs a hierarchical ESG analysis on an existing evaluation by
     * delegating the heavy lifting to the AI service.
     *
     * Sub-factor scores are derived from the stored single-dimension scores
     * using a uniform-fallback strategy: each dimension's sub-factors all
     * receive the dimension's stored value. In a real production system
     * these sub-factors would come from a richer data source (ESG data
     * provider, RAG over disclosures, etc.). The fallback is honest about
     * its limits and produces a result that is at least internally
     * consistent with the stored data.
     */
    @Transactional(readOnly = true)
    public EnrichedEvaluationResponse analyze(UUID evaluationId, String industry) {
        CreditEvaluation entity = repository.findById(evaluationId)
                .orElseThrow(() -> new EvaluationNotFoundException(evaluationId));

        ScoringRequest aiRequest = buildAiRequest(entity, industry);
        ScoringResponse aiResponse = aiServiceClient.score(aiRequest);

        return new EnrichedEvaluationResponse(
                entity.getId(),
                entity.getCompanyName(),
                entity.getEsgScore(),
                entity.getRiskLevel(),
                aiResponse
        );
    }

    private ScoringRequest buildAiRequest(CreditEvaluation entity, String industry) {
        int e = entity.getEnvironmentalScore();
        int s = entity.getSocialScore();
        int g = entity.getGovernanceScore();

        EnvironmentalFactors environmental = new EnvironmentalFactors(e, e, e, e, e);
        SocialFactors social = new SocialFactors(s, s, s, s, s);
        GovernanceFactors governance = new GovernanceFactors(g, g, g, g);
        QualitativeFlags qualitative = new QualitativeFlags(false, false, true);

        return new ScoringRequest(
                entity.getCompanyName(),
                industry,
                environmental,
                social,
                governance,
                qualitative
        );
    }
}