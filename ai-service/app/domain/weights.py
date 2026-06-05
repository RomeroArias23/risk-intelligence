"""
ESG dimension weights by industry.

These weights reflect the relative materiality of Environmental, Social,
and Governance factors for each industry, following SASB materiality
principles. Weights must sum to 1.0 within each industry.

For sub-factor weights within each dimension, see SUBFACTOR_WEIGHTS.
"""

from app.domain.industries import Industry

# Weights per ESG dimension (E, S, G), by industry.
# Each dictionary entry sums to 1.0.
INDUSTRY_WEIGHTS: dict[Industry, dict[str, float]] = {
    Industry.ENERGY:             {"E": 0.55, "S": 0.20, "G": 0.25},
    Industry.MINING:             {"E": 0.50, "S": 0.30, "G": 0.20},
    Industry.MANUFACTURING:      {"E": 0.40, "S": 0.30, "G": 0.30},
    Industry.TECHNOLOGY:         {"E": 0.20, "S": 0.30, "G": 0.50},
    Industry.FINANCIAL_SERVICES: {"E": 0.15, "S": 0.30, "G": 0.55},
    Industry.HEALTHCARE:         {"E": 0.20, "S": 0.45, "G": 0.35},
    Industry.CONSUMER_GOODS:     {"E": 0.30, "S": 0.40, "G": 0.30},
    Industry.AGRICULTURE:        {"E": 0.50, "S": 0.30, "G": 0.20},
    Industry.REAL_ESTATE:        {"E": 0.45, "S": 0.25, "G": 0.30},
    Industry.TRANSPORTATION:     {"E": 0.50, "S": 0.25, "G": 0.25},
}


# Sub-factor weights within each ESG dimension.
# Each dimension's weights sum to 1.0.
SUBFACTOR_WEIGHTS: dict[str, dict[str, float]] = {
    "E": {
        "ghg_emissions":      0.30,
        "energy_management":  0.20,
        "water_management":   0.15,
        "waste_management":   0.15,
        "climate_transition": 0.20,
    },
    "S": {
        "labor_practices":    0.20,
        "health_safety":      0.25,
        "community_relations": 0.20,
        "supply_chain":       0.20,
        "customer_welfare":   0.15,
    },
    "G": {
        "board_composition":     0.25,
        "business_ethics":       0.30,
        "transparency_reporting": 0.25,
        "risk_management":       0.20,
    },
}