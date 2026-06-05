# ESG Risk Intelligence Assistant

Enterprise-grade ESG (Environmental, Social, Governance) financial risk
intelligence platform. Two-service architecture with a Spring Boot API and a
FastAPI-based AI service implementing a hierarchical scorecard inspired by
SASB materiality principles.

Roadmap: future milestones will add RAG over corporate disclosures (with
pgvector and Claude API) and an ML-based scoring layer.

## Architecture
Client
│
▼
┌─────────────────────────────┐     ┌─────────────────────────────┐
│  Java / Spring Boot (8080)  │ ◄──►│  Python / FastAPI (8000)    │
│  Public API, persistence,   │ HTTP│  Hierarchical ESG scorecard │
│  orchestration              │     │  (SASB-aligned)             │
└──────────────┬──────────────┘     └─────────────────────────────┘
│
▼
┌────────────────┐
│ PostgreSQL 16  │
└────────────────┘

- **Java / Spring Boot** (`port 8080`): public REST API, business logic,
  persistence via Spring Data JPA, HTTP client to the AI service with
  timeouts and structured error mapping.
- **Python / FastAPI** (`port 8000`): ESG scoring engine, Pydantic-validated
  contracts, pytest-tested domain logic. Designed to host future ML/RAG
  capabilities.
- **PostgreSQL 16** in Docker; future pgvector for embeddings.

All infrastructure runs via Docker Compose. The Java service runs from
IntelliJ during development for debugging; the AI service is dockerized.

## Local development — daily startup

1. Start Docker Desktop.
2. From the project root:
   docker compose up -d
   docker compose ps     # confirm both containers are "(healthy)"
3. Open IntelliJ IDEA and run `RiskIntelligenceApplication`.

## Smoke test
curl http://localhost:8080/health
curl http://localhost:8000/health
curl -X POST http://localhost:8080/api/credit-evaluations
-H "Content-Type: application/json"
-d '{"companyName":"Acme Corp","requestedAmount":1000000,
"environmentalScore":80,"socialScore":75,"governanceScore":85}'

## Configuration

All environment-specific config is externalized via Spring profiles and
environment variables. See `.env.example` for the supported variables and
their defaults.

Common tweaks:
- `AI_SERVICE_READ_TIMEOUT_MS` — override the AI service read timeout
  (default `10000`).
- `SPRING_PROFILES_ACTIVE` — switch profile (`local` is the default).

## Stack

- Java 21, Spring Boot 3.5 (Web, Validation, Data JPA, RestClient)
- Python 3.12, FastAPI, Pydantic v2, pytest
- PostgreSQL 16, Docker, Docker Compose
- Build: Maven Wrapper (Java), uv (Python)

## Domain modeling notes

The hierarchical scorecard is inspired by the [openRiskScore](https://github.com/open-risk/openRiskScore)
concept of hierarchical scorecards and by SASB Industry Standards for
ESG materiality. Sub-factors are aggregated per dimension, then dimensions
are weighted per industry, then qualitative overlays (active controversies,
sanctions, disclosure quality) adjust the final score with clamping to
`[0, 100]`. The current 10 industries and 14 sub-factors are illustrative;
weights and overlays are baseline values calibrated for demonstration, not
production.

## Milestones

- [x] **M0** — Project bootstrap (Spring Boot + Java toolchain).
- [x] **M1** — Health controller and basic project structure.
- [x] **M2** — Credit evaluation endpoint with DTOs, service layer, and DI.
- [x] **M2.5** — Bean Validation and global exception handling.
- [x] **M3** — PostgreSQL persistence via Spring Data JPA; UUID-based
  entities with creation timestamps; DTO/Entity separation through
  a mapper.
- [x] **M4** — Two-service architecture:
    - [x] M4.1–M4.3 — Python AI service with hierarchical ESG scorecard,
      Pydantic schemas, pytest coverage, OpenAPI docs.
    - [x] M4.4 — Multi-stage Docker build, docker-compose orchestration
      with healthchecks and ordered startup.
    - [x] M4.5 — Java → Python HTTP client (`RestClient`) with timeouts
      and snake_case contract via `@JsonNaming` per record.
    - [x] M4.6 — Cross-service error handling: connection failures, upstream
      errors, and timeouts mapped to semantic HTTP status codes
      (502 Bad Gateway / 504 Gateway Timeout) via a domain
      exception with an enum cause.
    - [x] M4.7 — Externalized configuration with Spring Profiles, env vars,
      and a documented `.env.example`.
- [ ] **M5** — RAG pipeline: PDF ingestion, embeddings, pgvector,
  Claude API integration.

## Architectural decisions log

- Two-service split deliberate: business/persistence in Java (typed,
  transactional, mature for banking) and AI/ML in Python (where the
  ecosystem lives). Communication by HTTP boundary.
- Hibernate `ddl-auto=update` for development; production would use
  Flyway/Liquibase for auditable migrations.
- UUIDs as primary identifiers (no enumerable IDs leaking to clients).
- All cross-service HTTP calls have explicit timeouts; no unbounded
  waits.
- Errors from upstream services are translated to a domain exception
  with a typed cause, not raw transport exceptions, so the handler can
  map cleanly to HTTP semantics without coupling to RestClient internals.
- Snake_case JSON contract with the AI service is declared per-record
  via `@JsonNaming`, not globally on the `ObjectMapper`, so the public
  Java API keeps its idiomatic camelCase.
- Open Session In View disabled (`spring.jpa.open-in-view=false`) to
  avoid lazy queries during view rendering.