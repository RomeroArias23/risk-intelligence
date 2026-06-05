# syntax=docker/dockerfile:1

# ============================================================================
# Stage 1 — Builder: install dependencies into a self-contained venv
# ============================================================================
FROM python:3.12-slim AS builder

# Pull the official uv binary from Astral's image.
COPY --from=ghcr.io/astral-sh/uv:0.5.11 /uv /uvx /bin/

# uv environment tweaks for container builds
ENV UV_LINK_MODE=copy \
    UV_COMPILE_BYTECODE=1 \
    UV_PYTHON_DOWNLOADS=never

WORKDIR /app

# Install dependencies first (better layer caching).
# Only pyproject.toml and uv.lock change less often than app code.
COPY pyproject.toml uv.lock ./
RUN uv sync --frozen --no-dev --no-install-project

# Now copy the application code and install the project itself.
COPY app ./app
COPY main.py ./
RUN uv sync --frozen --no-dev

# ============================================================================
# Stage 2 — Runtime: minimal image with only the venv and source code
# ============================================================================
FROM python:3.12-slim AS runtime

WORKDIR /app

# Copy the prepared venv from the builder stage.
COPY --from=builder /app/.venv /app/.venv
COPY --from=builder /app/app /app/app
COPY --from=builder /app/main.py /app/main.py

# Put the venv binaries first in PATH so `python` and `uvicorn` resolve to ours.
ENV PATH="/app/.venv/bin:$PATH" \
    PYTHONUNBUFFERED=1 \
    PYTHONDONTWRITEBYTECODE=1

EXPOSE 8000

CMD ["uvicorn", "main:app", "--host", "0.0.0.0", "--port", "8000"]