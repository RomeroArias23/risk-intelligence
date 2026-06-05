"""
Pydantic schemas for the ESG scoring API.

These models define the request/response contracts and provide
automatic validation, serialization, and OpenAPI documentation.
"""

from pydantic import BaseModel, Field

from app.domain.industries import Industry


class EnvironmentalFactors(BaseModel):
    """Environmental sub-factor scores (0-100 each)."""

    ghg_emissions:      int = Field(ge=0, le=100, description="GHG emissions performance")
    energy_management:  int = Field(ge=0, le=100, description="Energy efficiency and renewables")
    water_management:   int = Field(ge=0, le=100, description="Water use and stewardship")
    waste_management:   int = Field(ge=0, le=100, description="Solid and hazardous waste handling")
    climate_transition: int = Field(ge=0, le=100, description="Net-zero alignment and TCFD reporting")


class SocialFactors(BaseModel):
    """Social sub-factor scores (0-100 each)."""

    labor_practices:    int = Field(ge=0, le=100, description="Labor rights and working conditions")
    health_safety:      int = Field(ge=0, le=100, description="Occupational health and safety")
    community_relations: int = Field(ge=0, le=100, description="Community impact and social license")
    supply_chain:       int = Field(ge=0, le=100, description="Supply chain due diligence")
    customer_welfare:   int = Field(ge=0, le=100, description="Data privacy and customer protection")


class GovernanceFactors(BaseModel):
    """Governance sub-factor scores (0-100 each)."""

    board_composition:     int = Field(ge=0, le=100, description="Board independence and diversity")
    business_ethics:       int = Field(ge=0, le=100, description="Anti-corruption and AML controls")
    transparency_reporting: int = Field(ge=0, le=100, description="ESG disclosure quality")
    risk_management:       int = Field(ge=0, le=100, description="Internal controls and risk integration")


class QualitativeFlags(BaseModel):
    """
    Qualitative overlays that adjust the final ESG score.

    These are binary or low-cardinality flags that, in real ESG analysis,
    can dominate quantitative scoring. For instance, a major active
    controversy can override otherwise strong sub-factor scores.
    """

    has_active_controversy: bool = Field(
        default=False,
        description="Active material ESG controversy in the last 12 months",
    )
    has_regulatory_sanctions: bool = Field(
        default=False,
        description="Active regulatory sanctions or fines",
    )
    publishes_sustainability_report: bool = Field(
        default=True,
        description="Publishes annual sustainability/ESG report aligned with GRI/SASB",
    )


class ScoringRequest(BaseModel):
    """Input payload for the /score endpoint."""

    company_name: str = Field(min_length=1, max_length=200)
    industry: Industry
    environmental: EnvironmentalFactors
    social: SocialFactors
    governance: GovernanceFactors
    qualitative: QualitativeFlags = Field(default_factory=QualitativeFlags)


class DimensionScore(BaseModel):
    """Aggregated score for a single ESG dimension."""

    raw_score: float = Field(description="Weighted sub-factor score, 0-100")
    weight: float = Field(description="Weight of this dimension for the company's industry")


class ScoringResponse(BaseModel):
    """Output payload for the /score endpoint."""

    company_name: str
    industry: Industry
    environmental: DimensionScore
    social: DimensionScore
    governance: DimensionScore
    base_score: float = Field(description="Weighted sum of dimension scores, before qualitative adjustments")
    qualitative_adjustment: float = Field(description="Net effect of qualitative flags on the final score")
    final_score: float = Field(description="Final ESG score after all adjustments, 0-100")
    risk_level: str = Field(description="LOW | MEDIUM | HIGH")
    recommendation: str