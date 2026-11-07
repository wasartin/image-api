"""Runtime configuration, read from environment variables.

Defaults match photoDB/docker-compose.yml so the API works against the
existing Postgres container with no extra setup.
"""
from __future__ import annotations

import os
from dataclasses import dataclass
from pathlib import Path

from sqlalchemy.engine import URL


@dataclass(frozen=True)
class Settings:
    database_url: str | URL
    image_dir: Path
    max_upload_mb: int = 25


def load_settings() -> Settings:
    database_url: str | URL | None = os.environ.get("DATABASE_URL")
    if not database_url:
        database_url = URL.create(
            "postgresql+psycopg",
            username=os.environ.get("DB_USER", "pg-user"),
            password=os.environ.get("DB_PASSWORD", "docket"),
            host=os.environ.get("DB_HOST", "127.0.0.1"),
            port=int(os.environ.get("DB_PORT", "5432")),
            database=os.environ.get("DB_NAME", "pi_frame_displayable"),
        )

    return Settings(
        database_url=database_url,
        image_dir=Path(os.environ.get("IMAGE_DIR", "./images")).expanduser().resolve(),
        max_upload_mb=int(os.environ.get("MAX_UPLOAD_MB", "25")),
    )
