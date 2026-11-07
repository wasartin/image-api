"""Database engine, session handling and startup table creation."""
from __future__ import annotations

from collections.abc import Iterator

from fastapi import Request
from sqlalchemy import create_engine
from sqlalchemy.engine import URL, Engine
from sqlalchemy.orm import DeclarativeBase, Session


class Base(DeclarativeBase):
    pass


def make_engine(url: str | URL) -> Engine:
    url_str = url if isinstance(url, str) else url.render_as_string(hide_password=False)
    connect_args = {"check_same_thread": False} if url_str.startswith("sqlite") else {}
    return create_engine(url, pool_pre_ping=True, connect_args=connect_args)


def init_db(engine: Engine) -> None:
    """Create any missing tables.

    `create_all` only creates tables that don't exist yet, so the existing
    `photo` table in Postgres is left untouched; in practice this only adds
    the new `display_state` table.
    """
    from . import models  # noqa: F401  (registers the tables on Base.metadata)

    Base.metadata.create_all(engine)


def get_db(request: Request) -> Iterator[Session]:
    with request.app.state.session_factory() as session:
        yield session
