"""HTTP endpoints for ESG scoring."""

from fastapi import APIRouter

from app.domain.scorecard import (
    aggregate_base_score,
    aggregate_dimension,
    apply_qualitative_overlays,
    build_recommendation,
    classify_risk,
)
from app.domain.weights import INDUSTRY_WEIGHTS
from app.schemas.scoring import (
    DimensionScore,
    ScoringRequest,
    ScoringResponse,
)

router = APIRouter(prefix="/api/v1", tags=["scoring"])


@router.post("/score", response_model=ScoringResponse)
def score(request: ScoringRequest) -> ScoringResponse:
    """
    Compute a hierarchical ESG score for a company.

    The score aggregates 14 sub-factors across Environmental, Social,
    and Governance dimensions, weighted by industry-specific materiality,
    and adjusted by qualitative overlays (controversies, sanctions,
    disclosure quality).
    """
    # Dimension aggregation (sub-factors → dimension raw scores)
    e_raw = aggregate_dimension(request.environmental.model_dump(), "E")
    s_raw = aggregate_dimension(request.social.model_dump(), "S")
    g_raw = aggregate_dimension(request.governance.model_dump(), "G")

    industry_weights = INDUSTRY_WEIGHTS[request.industry]

    # Base score (dimensions → industry-weighted base)
    base = aggregate_base_score(e_raw, s_raw, g_raw, request.industry)

    # Qualitative overlays (base → final)
    final, adjustment = apply_qualitative_overlays(
        base_score=base,
        has_active_controversy=request.qualitative.has_active_controversy,
        has_regulatory_sanctions=request.qualitative.has_regulatory_sanctions,
        publishes_sustainability_report=request.qualitative.publishes_sustainability_report,
    )

    # Classification and recommendation
    risk_level = classify_risk(final)
    recommendation = build_recommendation(risk_level)

    return ScoringResponse(
        company_name=request.company_name,
        industry=request.industry,
        environmental=DimensionScore(raw_score=e_raw, weight=industry_weights["E"]),
        social=DimensionScore(raw_score=s_raw, weight=industry_weights["S"]),
        governance=DimensionScore(raw_score=g_raw, weight=industry_weights["G"]),
        base_score=base,
        qualitative_adjustment=adjustment,
        final_score=final,
        risk_level=risk_level,
        recommendation=recommendation,
    )