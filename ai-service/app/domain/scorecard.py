"""
Hierarchical ESG scorecard engine.

Computes a final ESG score from sub-factor inputs by:
  1. Aggregating sub-factors into dimension raw scores (weighted sum).
  2. Aggregating dimension raw scores into a base score using
     industry-specific weights.
  3. Applying qualitative overlays (multipliers and penalties)
     to produce the final score.

This module is framework-agnostic: it does not depend on FastAPI,
Pydantic, or any HTTP machinery. It can be invoked from any context.
"""

from app.domain.industries import Industry
from app.domain.weights import INDUSTRY_WEIGHTS, SUBFACTOR_WEIGHTS

# Tolerance for floating-point weight validation.
# Weights are declared as floats and may not sum to *exactly* 1.0
# due to binary representation, so we allow a tiny epsilon.
WEIGHT_SUM_TOLERANCE = 1e-9


def validate_weights() -> None:
    """
    Validate that all weight tables sum to 1.0 within tolerance.

    Raises ValueError on the first inconsistency. Intended to run
    at application startup so misconfigured weights fail loudly
    rather than silently corrupt every score.
    """
    for industry, weights in INDUSTRY_WEIGHTS.items():
        total = sum(weights.values())
        if abs(total - 1.0) > WEIGHT_SUM_TOLERANCE:
            raise ValueError(
                f"INDUSTRY_WEIGHTS[{industry.value}] sums to {total}, expected 1.0"
            )

    for dimension, weights in SUBFACTOR_WEIGHTS.items():
        total = sum(weights.values())
        if abs(total - 1.0) > WEIGHT_SUM_TOLERANCE:
            raise ValueError(
                f"SUBFACTOR_WEIGHTS[{dimension}] sums to {total}, expected 1.0"
            )


def aggregate_dimension(subfactors: dict[str, int], dimension: str) -> float:
    """
    Compute the weighted sum of a dimension's sub-factors.

    Args:
        subfactors: mapping of sub-factor name to score (0-100).
        dimension: 'E', 'S', or 'G'.

    Returns:
        Weighted raw score for the dimension (0-100).
    """
    weights = SUBFACTOR_WEIGHTS[dimension]
    return sum(subfactors[name] * weight for name, weight in weights.items())


def aggregate_base_score(
    e_score: float,
    s_score: float,
    g_score: float,
    industry: Industry,
) -> float:
    """
    Combine dimension scores into a base ESG score using
    industry-specific weights.
    """
    weights = INDUSTRY_WEIGHTS[industry]
    return (
        e_score * weights["E"]
        + s_score * weights["S"]
        + g_score * weights["G"]
    )


def apply_qualitative_overlays(
    base_score: float,
    has_active_controversy: bool,
    has_regulatory_sanctions: bool,
    publishes_sustainability_report: bool,
) -> tuple[float, float]:
    """
    Apply qualitative adjustments to the base score.

    Returns:
        (final_score, adjustment) where adjustment is the net change
        applied (positive or negative).

    Adjustments are additive in score points, capped so the final
    score remains within [0, 100].
    """
    adjustment = 0.0

    # Penalties
    if has_active_controversy:
        adjustment -= 15.0
    if has_regulatory_sanctions:
        adjustment -= 20.0

    # Bonus
    if publishes_sustainability_report:
        adjustment += 5.0
    else:
        adjustment -= 10.0  # missing disclosure is itself a flag

    final_score = max(0.0, min(100.0, base_score + adjustment))
    return final_score, final_score - base_score


def classify_risk(score: float) -> str:
    """Map a final score to a discrete risk bucket."""
    if score >= 70:
        return "LOW"
    if score >= 40:
        return "MEDIUM"
    return "HIGH"


def build_recommendation(risk_level: str) -> str:
    """Build the textual recommendation for each risk bucket."""
    recommendations = {
        "LOW": "Aligned with sustainable financing criteria. Approve for review.",
        "MEDIUM": "Requires additional ESG due diligence before approval.",
        "HIGH": "High ESG risk. Escalate to risk committee.",
    }
    return recommendations[risk_level]