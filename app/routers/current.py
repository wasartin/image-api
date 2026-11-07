"""Which photo the frame should be showing."""
from __future__ import annotations

from typing import Annotated

from fastapi import APIRouter, Depends
from sqlalchemy.orm import Session

from .. import services
from ..config import Settings
from ..db import get_db
from ..deps import get_settings
from ..schemas import CurrentIn, CurrentOut

router = APIRouter(prefix="/v1/current", tags=["current photo"])

DB = Annotated[Session, Depends(get_db)]
Cfg = Annotated[Settings, Depends(get_settings)]


@router.get(
    "",
    response_model=CurrentOut,
    summary="Get the photo the frame should show",
    description="`photo` is null when nothing is selected; the display then runs its slideshow.",
)
def get_current(db: DB, settings: Cfg):
    return services.current_out(db, settings.image_dir)


@router.put("", response_model=CurrentOut, summary="Pick the photo to show")
def put_current(body: CurrentIn, db: DB, settings: Cfg):
    services.set_current(db, body.photo_id)
    return services.current_out(db, settings.image_dir)


@router.delete("", response_model=CurrentOut, summary="Clear the selection (back to slideshow)")
def clear_current(db: DB, settings: Cfg):
    services.set_current(db, None)
    return services.current_out(db, settings.image_dir)
