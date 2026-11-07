"""ORM models.

`photo` maps the existing table from photoDB/scripts/init.sql exactly.
`display_state` is new: a single-row table holding the photo the frame
should show. Its equivalent SQL, if you ever want it in photoDB's init.sql:

    CREATE TABLE IF NOT EXISTS display_state (
      dst_id INTEGER PRIMARY KEY CHECK (dst_id = 1),
      dst_current_photo_id INTEGER REFERENCES photo(pho_id) ON DELETE SET NULL,
      dst_updated TIMESTAMP DEFAULT CURRENT_TIMESTAMP
    );
"""
from __future__ import annotations

from datetime import datetime

from sqlalchemy import CheckConstraint, DateTime, ForeignKey, Integer, String, func
from sqlalchemy.orm import Mapped, mapped_column, relationship

from .db import Base

STATE_ROW_ID = 1


class Photo(Base):
    __tablename__ = "photo"

    id: Mapped[int] = mapped_column("pho_id", Integer, primary_key=True, autoincrement=True)
    file_path: Mapped[str | None] = mapped_column("pho_file_path", String(260))
    title: Mapped[str | None] = mapped_column("pho_title", String(260))
    created: Mapped[datetime | None] = mapped_column(
        "pho_created", DateTime, server_default=func.now()
    )


class DisplayState(Base):
    __tablename__ = "display_state"
    __table_args__ = (CheckConstraint("dst_id = 1", name="display_state_single_row"),)

    id: Mapped[int] = mapped_column("dst_id", Integer, primary_key=True, autoincrement=False)
    current_photo_id: Mapped[int | None] = mapped_column(
        "dst_current_photo_id",
        Integer,
        ForeignKey("photo.pho_id", ondelete="SET NULL"),
        nullable=True,
    )
    updated: Mapped[datetime | None] = mapped_column(
        "dst_updated", DateTime, server_default=func.now()
    )

    current_photo: Mapped[Photo | None] = relationship(lazy="joined")
