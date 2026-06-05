"""Unit tests for the ESG scorecard engine."""

import pytest

from app.domain.industries import Industry
from app.domain.scorecard import (
    aggregate_base_score,
    aggregate_dimension,
    apply_qualitative_overlays,
    classify_risk,
    validate_weights,
)


def test_validate_weights_passes_on_correct_tables():
    """All shipped weight tables should be valid."""
    validate_weights()  # raises if any sum diverges from 1.0


def test_aggregate_dimension_full_score():
    """A dimension with all sub-factors at 100 must yield 100."""
    subfactors = {
        "ghg_emissions":      100,
        "energy_management":  100,
        "water_management":   100,
        "waste_management":   100,
        "climate_transition": 100,
    }
    assert aggregate_dimension(subfactors, "E") == pytest.approx(100.0)


def test_aggregate_dimension_zero_score():
    """A dimension with all sub-factors at 0 must yield 0."""
    subfactors = {
        "ghg_emissions":      0,
        "energy_management":  0,
        "water_management":   0,
        "waste_management":   0,
        "climate_transition": 0,
    }
    assert aggregate_dimension(subfactors, "E") == pytest.approx(0.0)


def test_aggregate_base_score_uses_industry_weights():
    """
    Energy industry weights E=0.55, S=0.20, G=0.25.
    With E=100, S=0, G=0, base score must equal 55.0.
    """
    base = aggregate_base_score(100.0, 0.0, 0.0, Industry.ENERGY)
    assert base == pytest.approx(55.0)


def test_aggregate_base_score_financial_services_weighted_to_governance():
    """Financial services weight G=0.55 — a strong G should dominate."""
    base = aggregate_base_score(0.0, 0.0, 100.0, Industry.FINANCIAL_SERVICES)
    assert base == pytest.approx(55.0)


def test_qualitative_overlays_active_controversy_penalty():
    """Active controversy penalizes 15 points."""
    final, adjustment = apply_qualitative_overlays(
        base_score=80.0,
        has_active_controversy=True,
        has_regulatory_sanctions=False,
        publishes_sustainability_report=True,
    )
    # -15 (controversy) + 5 (reporting bonus) = -10 net
    assert adjustment == pytest.approx(-10.0)
    assert final == pytest.approx(70.0)


def test_qualitative_overlays_clamp_to_zero():
    """Score must not go below 0 regardless of penalties."""
    final, _ = apply_qualitative_overlays(
        base_score=10.0,
        has_active_controversy=True,
        has_regulatory_sanctions=True,
        publishes_sustainability_report=False,
    )
    assert final == pytest.approx(0.0)


def test_qualitative_overlays_clamp_to_hundred():
    """Score must not exceed 100 regardless of bonuses."""
    final, _ = apply_qualitative_overlays(
        base_score=99.0,
        has_active_controversy=False,
        has_regulatory_sanctions=False,
        publishes_sustainability_report=True,
    )
    assert final == pytest.approx(100.0)


def test_classify_risk_buckets():
    """Risk classification thresholds."""
    assert classify_risk(85.0) == "LOW"
    assert classify_risk(70.0) == "LOW"
    assert classify_risk(69.9) == "MEDIUM"
    assert classify_risk(40.0) == "MEDIUM"
    assert classify_risk(39.9) == "HIGH"
    assert classify_risk(0.0) == "HIGH"