"""Logic shared by the routers."""
from __future__ import annotations

from datetime import datetime
from pathlib import Path

from fastapi import HTTPException, status
from sqlalchemy import update
from sqlalchemy.orm import Session

from . import storage
from .models import STATE_ROW_ID, DisplayState, Photo
from .schemas import CurrentOut, PhotoOut


def photo_out(photo: Photo, image_dir: Path) -> PhotoOut:
    return PhotoOut(
        photo_id=photo.id,
        title=photo.title,
        file_path=photo.file_path,
        created=photo.created,
        url=f"/v1/photos/{photo.id}/file",
        managed=storage.is_managed(image_dir, photo.file_path),
        **storage.describe(photo.file_path),
    )


def get_photo_or_404(db: Session, photo_id: int) -> Photo:
    photo = db.get(Photo, photo_id)
    if photo is None:
        raise HTTPException(status.HTTP_404_NOT_FOUND, f"Photo {photo_id} not found")
    return photo


def create_photo_from_bytes(
    db: Session, image_dir: Path, original_name: str, data: bytes, title: str | None
) -> Photo:
    try:
        path = storage.save_image(image_dir, original_name, data)
    except storage.InvalidImage as exc:
        raise HTTPException(status.HTTP_400_BAD_REQUEST, str(exc)) from exc

    photo = Photo(file_path=str(path), title=(title or path.name)[:260])
    try:
        db.add(photo)
        db.commit()
    except Exception:
        db.rollback()
        path.unlink(missing_ok=True)
        raise
    db.refresh(photo)
    return photo


def delete_photo(db: Session, image_dir: Path, photo_id: int) -> None:
    photo = get_photo_or_404(db, photo_id)
    file_path = photo.file_path
    # Postgres clears this via ON DELETE SET NULL; done explicitly so it also
    # holds on databases that don't enforce foreign keys (e.g. SQLite in tests).
    db.execute(
        update(DisplayState)
        .where(DisplayState.current_photo_id == photo_id)
        .values(current_photo_id=None, updated=datetime.now())
    )
    db.delete(photo)
    db.commit()
    storage.delete_if_managed(image_dir, file_path)


def current_out(db: Session, image_dir: Path) -> CurrentOut:
    state = db.get(DisplayState, STATE_ROW_ID)
    if state is None:
        return CurrentOut(photo=None, updated=None)
    db.refresh(state)
    photo = photo_out(state.current_photo, image_dir) if state.current_photo else None
    return CurrentOut(photo=photo, updated=state.updated)


def set_current(db: Session, photo_id: int | None) -> None:
    if photo_id is not None:
        get_photo_or_404(db, photo_id)
    state = db.get(DisplayState, STATE_ROW_ID)
    if state is None:
        state = DisplayState(id=STATE_ROW_ID)
        db.add(state)
    state.current_photo_id = photo_id
    state.updated = datetime.now()
    db.commit()
