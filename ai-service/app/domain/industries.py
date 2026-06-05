from enum import Enum


class Industry(str, Enum):
    """
    Supported industry classifications.

    Aligned with SASB Industry Standards (high-level groupings).
    Each industry will have its own ESG dimension weights, reflecting
    materiality differences across sectors.
    """

    ENERGY = "energy"
    MINING = "mining"
    MANUFACTURING = "manufacturing"
    TECHNOLOGY = "technology"
    FINANCIAL_SERVICES = "financial_services"
    HEALTHCARE = "healthcare"
    CONSUMER_GOODS = "consumer_goods"
    AGRICULTURE = "agriculture"
    REAL_ESTATE = "real_estate"
    TRANSPORTATION = "transportation"