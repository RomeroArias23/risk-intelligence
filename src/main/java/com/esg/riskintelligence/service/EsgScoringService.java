package com.esg.riskintelligence.service;

import com.esg.riskintelligence.dto.CreditApplicationRequest;
import com.esg.riskintelligence.dto.CreditEvaluationResponse;
import com.esg.riskintelligence.entity.CreditEvaluation;
import com.esg.riskintelligence.mapper.CreditEvaluationMapper;
import com.esg.riskintelligence.repository.CreditEvaluationRepository;
import com.esg.riskintelligence.error.EvaluationNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class EsgScoringService {

    private final CreditEvaluationRepository repository;
    private final CreditEvaluationMapper mapper;

    public EsgScoringService(
            CreditEvaluationRepository repository,
            CreditEvaluationMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Transactional
    public CreditEvaluationResponse evaluate(CreditApplicationRequest request) {
        double esgScore = (request.environmentalScore()
                + request.socialScore()
                + request.governanceScore()) / 3.0;

        String riskLevel = classifyRisk(esgScore);
        String recommendation = buildRecommendation(riskLevel);

        CreditEvaluation entity = new CreditEvaluation(
                request.companyName(),
                request.requestedAmount(),
                request.environmentalScore(),
                request.socialScore(),
                request.governanceScore(),
                esgScore,
                riskLevel,
                recommendation
        );

        CreditEvaluation saved = repository.save(entity);

        return mapper.toResponse(saved);
    }

    @Transactional(readOnly = true)
    public List<CreditEvaluationResponse> findAll() {
        return repository.findAll()
                .stream()
                .map(mapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public CreditEvaluationResponse findById(UUID id) {
        CreditEvaluation entity = repository.findById(id)
                .orElseThrow(() -> new EvaluationNotFoundException(id));
        return mapper.toResponse(entity);
    }

    private String classifyRisk(double esgScore) {
        if (esgScore >= 70) return "LOW";
        if (esgScore >= 40) return "MEDIUM";
        return "HIGH";
    }

    private String buildRecommendation(String riskLevel) {
        return switch (riskLevel) {
            case "LOW" -> "Aligned with sustainable financing criteria. Approve for review.";
            case "MEDIUM" -> "Requires additional ESG due diligence before approval.";
            case "HIGH" -> "High ESG risk. Escalate to risk committee.";
            default -> throw new IllegalStateException("Unknown risk level: " + riskLevel);
        };
    }
}