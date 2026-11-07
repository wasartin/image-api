"""Request/response shapes.

JSON uses camelCase (photoId, filePath, ...) to stay compatible with the
Kotlin API; snake_case is accepted on input too.
"""
from __future__ import annotations

from datetime import datetime

from pydantic import BaseModel, ConfigDict, Field
from pydantic.alias_generators import to_camel


class CamelModel(BaseModel):
    model_config = ConfigDict(alias_generator=to_camel, populate_by_name=True)


class PhotoOut(CamelModel):
    photo_id: int
    title: str | None
    file_path: str | None
    created: datetime | None
    url: str = Field(description="Where to fetch the image itself")
    file_exists: bool
    managed: bool = Field(description="True if the file lives in the API's image folder")
    size_bytes: int | None = None
    width: int | None = None
    height: int | None = None


class PhotoRegister(CamelModel):
    """Register a file that already exists on disk (the old POST /v1/photos)."""

    file_path: str
    title: str | None = None


class PhotoUpdate(CamelModel):
    title: str = Field(min_length=1, max_length=260)


class LegacyImage(CamelModel):
    """Same shape as the Kotlin `Image` DTO used by /v1/images."""

    id: int = -100
    file_path: str = ""
    contents_as_base64: str = ""


class CurrentIn(CamelModel):
    photo_id: int


class CurrentOut(CamelModel):
    photo: PhotoOut | None
    updated: datetime | None
