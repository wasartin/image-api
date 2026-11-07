"""Tests run against a throwaway SQLite database and temp image folder,
so they don't need Postgres or Docker."""
from __future__ import annotations

import io

import pytest
from fastapi.testclient import TestClient
from PIL import Image

from app.config import Settings
from app.main import create_app


@pytest.fixture()
def image_dir(tmp_path):
    return tmp_path / "images"


@pytest.fixture()
def client(tmp_path, image_dir):
    settings = Settings(database_url=f"sqlite:///{tmp_path / 'test.db'}", image_dir=image_dir, max_upload_mb=1)
    with TestClient(create_app(settings)) as c:
        yield c


def make_image(fmt: str = "JPEG", size=(40, 30), color=(200, 30, 30)) -> bytes:
    buf = io.BytesIO()
    Image.new("RGB", size, color).save(buf, format=fmt)
    return buf.getvalue()


@pytest.fixture()
def upload(client):
    def _upload(name="beach.jpg", data=None, title=None):
        files = {"file": (name, data if data is not None else make_image(), "image/jpeg")}
        form = {"title": title} if title else {}
        return client.post("/v1/photos/upload", files=files, data=form)

    return _upload
