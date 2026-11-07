"""The old base64 /v1/images endpoints, kept so existing clients keep working.

New code should use POST /v1/photos/upload and DELETE /v1/photos/{id}.
"""
from __future__ import annotations

import base64
import binascii
from typing import Annotated

from fastapi import APIRouter, Depends, HTTPException, Response, status
from sqlalchemy.orm import Session

from .. import services
from ..config import Settings
from ..db import get_db
from ..deps import get_settings
from ..schemas import LegacyImage

router = APIRouter(prefix="/v1/images", tags=["images (legacy)"])

DB = Annotated[Session, Depends(get_db)]
Cfg = Annotated[Settings, Depends(get_settings)]


@router.post("", response_model=LegacyImage, deprecated=True, summary="Upload a base64 image")
def add_image(body: LegacyImage, db: DB, settings: Cfg):
    payload = body.contents_as_base64
    if payload.startswith("data:") and "," in payload:  # tolerate data URLs
        payload = payload.split(",", 1)[1]
    try:
        data = base64.b64decode(payload, validate=True)
    except (binascii.Error, ValueError) as exc:
        raise HTTPException(status.HTTP_400_BAD_REQUEST, "contentsAsBase64 is not valid base64") from exc

    photo = services.create_photo_from_bytes(
        db, settings.image_dir, body.file_path or "upload.jpg", data, title=None
    )
    return LegacyImage(id=photo.id, file_path=photo.file_path or "", contents_as_base64="")


@router.delete(
    "/{image_id}", status_code=status.HTTP_204_NO_CONTENT, deprecated=True, summary="Delete an image"
)
def delete_image(image_id: int, db: DB, settings: Cfg):
    services.delete_photo(db, settings.image_dir, image_id)
    return Response(status_code=status.HTTP_204_NO_CONTENT)
