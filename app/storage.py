"""Saving, describing and deleting image files on disk."""
from __future__ import annotations

import io
import re
from pathlib import Path

from PIL import Image, UnidentifiedImageError

ALLOWED_SUFFIXES = {".jpg", ".jpeg", ".png", ".gif", ".bmp", ".webp"}


class InvalidImage(ValueError):
    pass


def clean_filename(original: str) -> str:
    """Reduce a client-supplied name (possibly a full Windows/Unix path) to a safe file name."""
    name = original.replace("\\", "/").rsplit("/", 1)[-1].strip()
    stem, dot, suffix = name.rpartition(".")
    if not dot:
        raise InvalidImage(f"'{original}' has no file extension")
    suffix = "." + suffix.lower()
    if suffix not in ALLOWED_SUFFIXES:
        raise InvalidImage(f"'{suffix}' files aren't supported (use {', '.join(sorted(ALLOWED_SUFFIXES))})")
    stem = re.sub(r"[^A-Za-z0-9._-]+", "_", stem).strip("._") or "photo"
    return stem[:200] + suffix


def verify_image(data: bytes) -> None:
    if not data:
        raise InvalidImage("file is empty")
    try:
        with Image.open(io.BytesIO(data)) as img:
            img.verify()
    except (UnidentifiedImageError, OSError, SyntaxError) as exc:
        raise InvalidImage("file is not a readable image") from exc


def save_image(image_dir: Path, original_name: str, data: bytes) -> Path:
    """Validate and write an image, never overwriting an existing file."""
    name = clean_filename(original_name)
    verify_image(data)
    image_dir.mkdir(parents=True, exist_ok=True)

    stem, suffix = name[: -len(Path(name).suffix)], Path(name).suffix
    candidate, n = image_dir / name, 1
    while True:
        try:
            with open(candidate, "xb") as fh:
                fh.write(data)
            return candidate
        except FileExistsError:
            n += 1
            candidate = image_dir / f"{stem}-{n}{suffix}"


def is_managed(image_dir: Path, file_path: str | None) -> bool:
    if not file_path:
        return False
    try:
        Path(file_path).resolve().relative_to(image_dir.resolve())
        return True
    except (ValueError, OSError):
        return False


def delete_if_managed(image_dir: Path, file_path: str | None) -> bool:
    """Delete the file only if the API owns it; files registered from elsewhere are left alone."""
    if not is_managed(image_dir, file_path):
        return False
    try:
        Path(file_path).unlink()  # type: ignore[arg-type]
        return True
    except FileNotFoundError:
        return False


def describe(file_path: str | None) -> dict:
    """Size and pixel dimensions, read from the file header only."""
    info: dict = {"file_exists": False, "size_bytes": None, "width": None, "height": None}
    if not file_path:
        return info
    path = Path(file_path)
    if not path.is_file():
        return info
    info["file_exists"] = True
    info["size_bytes"] = path.stat().st_size
    try:
        with Image.open(path) as img:
            info["width"], info["height"] = img.size
    except (UnidentifiedImageError, OSError):
        pass
    return info
