"""
ESG Risk Intelligence — AI Service.

Entry point for the FastAPI application. This module wires the
domain logic into HTTP endpoints, registers startup hooks,
and exposes the OpenAPI documentation at /docs.
"""

from contextlib import asynccontextmanager

from fastapi import FastAPI

from app.api.scoring import router as scoring_router
from app.domain.scorecard import validate_weights


@asynccontextmanager
async def lifespan(app: FastAPI):
    """
    Application lifespan hook.

    Runs once on startup before accepting requests, and once on shutdown.
    We use it to fail fast if any weight table is misconfigured.
    """
    validate_weights()
    yield
    # Shutdown logic would go here (none for now).


app = FastAPI(
    title="ESG Risk Intelligence — AI Service",
    description="Hierarchical ESG scorecard and (future) ML-based analysis.",
    version="0.1.0",
    lifespan=lifespan,
)


@app.get("/health")
def health():
    return {"status": "ok", "service": "ai-service"}


app.include_router(scoring_router)