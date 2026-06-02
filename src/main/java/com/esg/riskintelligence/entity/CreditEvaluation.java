package com.esg.riskintelligence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "credit_evaluations")
public class CreditEvaluation {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "company_name", nullable = false, length = 200)
    private String companyName;

    @Column(name = "requested_amount", nullable = false)
    private double requestedAmount;

    @Column(name = "environmental_score", nullable = false)
    private int environmentalScore;

    @Column(name = "social_score", nullable = false)
    private int socialScore;

    @Column(name = "governance_score", nullable = false)
    private int governanceScore;

    @Column(name = "esg_score", nullable = false)
    private double esgScore;

    @Column(name = "risk_level", nullable = false, length = 20)
    private String riskLevel;

    @Column(name = "recommendation", nullable = false, length = 500)
    private String recommendation;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    protected CreditEvaluation() {
        // Required by JPA. Do not use directly.
    }

    public CreditEvaluation(
            String companyName,
            double requestedAmount,
            int environmentalScore,
            int socialScore,
            int governanceScore,
            double esgScore,
            String riskLevel,
            String recommendation
    ) {
        this.companyName = companyName;
        this.requestedAmount = requestedAmount;
        this.environmentalScore = environmentalScore;
        this.socialScore = socialScore;
        this.governanceScore = governanceScore;
        this.esgScore = esgScore;
        this.riskLevel = riskLevel;
        this.recommendation = recommendation;
    }

    public UUID getId() { return id; }
    public String getCompanyName() { return companyName; }
    public double getRequestedAmount() { return requestedAmount; }
    public int getEnvironmentalScore() { return environmentalScore; }
    public int getSocialScore() { return socialScore; }
    public int getGovernanceScore() { return governanceScore; }
    public double getEsgScore() { return esgScore; }
    public String getRiskLevel() { return riskLevel; }
    public String getRecommendation() { return recommendation; }
    public Instant getCreatedAt() { return createdAt; }
}