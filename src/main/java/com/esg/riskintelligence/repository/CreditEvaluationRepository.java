package com.esg.riskintelligence.repository;

import com.esg.riskintelligence.entity.CreditEvaluation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface CreditEvaluationRepository
        extends JpaRepository<CreditEvaluation, UUID> {
}