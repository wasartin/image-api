from __future__ import annotations

from fastapi import Request

from .config import Settings


def get_settings(request: Request) -> Settings:
    return request.app.state.settings
