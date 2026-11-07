from __future__ import annotations

from pathlib import Path
from typing import Annotated

from fastapi import APIRouter, Depends, File, Form, HTTPException, Response, UploadFile, status
from fastapi.responses import FileResponse
from sqlalchemy import select
from sqlalchemy.orm import Session

from .. import services
from ..config import Settings
from ..db import get_db
from ..deps import get_settings
from ..models import Photo
from ..schemas import PhotoOut, PhotoRegister, PhotoUpdate

router = APIRouter(prefix="/v1/photos", tags=["photos"])

DB = Annotated[Session, Depends(get_db)]
Cfg = Annotated[Settings, Depends(get_settings)]


@router.get("", response_model=list[PhotoOut], summary="List all photos, newest first")
def list_photos(db: DB, settings: Cfg):
    photos = db.scalars(select(Photo).order_by(Photo.id.desc())).all()
    return [services.photo_out(p, settings.image_dir) for p in photos]


@router.get("/{photo_id}", response_model=PhotoOut, summary="Get one photo's details")
def get_photo(photo_id: int, db: DB, settings: Cfg):
    return services.photo_out(services.get_photo_or_404(db, photo_id), settings.image_dir)


@router.get(
    "/{photo_id}/file",
    response_class=FileResponse,
    summary="Download the image itself",
    responses={200: {"content": {"image/*": {}}}, 404: {"description": "Photo or file missing"}},
)
def get_photo_file(photo_id: int, db: DB):
    photo = services.get_photo_or_404(db, photo_id)
    if not photo.file_path or not Path(photo.file_path).is_file():
        raise HTTPException(status.HTTP_404_NOT_FOUND, f"File for photo {photo_id} not found on disk")
    return FileResponse(photo.file_path)


@router.post(
    "/upload",
    response_model=PhotoOut,
    status_code=status.HTTP_201_CREATED,
    summary="Upload an image file",
)
def upload_photo(
    db: DB,
    settings: Cfg,
    file: Annotated[UploadFile, File(description="JPEG, PNG, GIF, BMP or WebP")],
    title: Annotated[str | None, Form(max_length=260)] = None,
):
    limit = settings.max_upload_mb * 1024 * 1024
    data = file.file.read(limit + 1)
    if len(data) > limit:
        raise HTTPException(
            status.HTTP_413_REQUEST_ENTITY_TOO_LARGE, f"File is larger than {settings.max_upload_mb} MB"
        )
    photo = services.create_photo_from_bytes(
        db, settings.image_dir, file.filename or "upload.jpg", data, title
    )
    return services.photo_out(photo, settings.image_dir)


@router.post(
    "",
    response_model=PhotoOut,
    status_code=status.HTTP_201_CREATED,
    summary="Register a file that's already on disk",
    description="Same as the old Kotlin POST /v1/photos: adds a database row pointing at an "
    "existing path. The file isn't copied, and deleting the photo later won't delete it.",
)
def register_photo(body: PhotoRegister, db: DB, settings: Cfg):
    photo = Photo(
        file_path=body.file_path,
        title=(body.title or Path(body.file_path.replace("\\", "/")).name)[:260],
    )
    db.add(photo)
    db.commit()
    db.refresh(photo)
    return services.photo_out(photo, settings.image_dir)


@router.patch("/{photo_id}", response_model=PhotoOut, summary="Rename a photo")
def update_photo(photo_id: int, body: PhotoUpdate, db: DB, settings: Cfg):
    photo = services.get_photo_or_404(db, photo_id)
    photo.title = body.title
    db.commit()
    db.refresh(photo)
    return services.photo_out(photo, settings.image_dir)


@router.delete(
    "/{photo_id}",
    status_code=status.HTTP_204_NO_CONTENT,
    summary="Delete a photo",
    description="Removes the database row, and the file too if it was uploaded through this API.",
)
def delete_photo(photo_id: int, db: DB, settings: Cfg):
    services.delete_photo(db, settings.image_dir, photo_id)
    return Response(status_code=status.HTTP_204_NO_CONTENT)
