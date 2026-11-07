"""App factory. Run with:

    uvicorn app.main:create_app --factory --host 0.0.0.0 --port 9009

Swagger UI is at http://<host>:9009/docs
"""
from __future__ import annotations

from contextlib import asynccontextmanager

from fastapi import FastAPI, Request
from fastapi.responses import JSONResponse
from sqlalchemy import text
from sqlalchemy.orm import sessionmaker

from .config import Settings, load_settings
from .db import init_db, make_engine
from .routers import current, images, photos

DESCRIPTION = """
REST API for piFrame: stores photos, tracks them in Postgres, and tells the
display which photo to show.

* **photos** – upload, list, inspect, rename and delete photos
* **current photo** – choose what the frame shows (or clear it for slideshow mode)
* **images (legacy)** – the old base64 endpoints from the Kotlin version
"""


def create_app(settings: Settings | None = None) -> FastAPI:
    settings = settings or load_settings()
    engine = make_engine(settings.database_url)

    @asynccontextmanager
    async def lifespan(app: FastAPI):
        settings.image_dir.mkdir(parents=True, exist_ok=True)
        init_db(engine)
        yield
        engine.dispose()

    app = FastAPI(
        title="piFrame Gallery API",
        version="0.2.0",
        description=DESCRIPTION,
        lifespan=lifespan,
    )
    app.state.settings = settings
    app.state.engine = engine
    app.state.session_factory = sessionmaker(engine, autoflush=False, expire_on_commit=False)

    app.include_router(photos.router)
    app.include_router(current.router)
    app.include_router(images.router)

    @app.get("/health", tags=["health"], summary="Liveness and database check")
    def health(request: Request):
        try:
            with request.app.state.engine.connect() as conn:
                conn.execute(text("SELECT 1"))
            return {"status": "UP", "database": "UP"}
        except Exception as exc:  # noqa: BLE001
            return JSONResponse(
                status_code=503,
                content={"status": "DOWN", "database": "DOWN", "error": type(exc).__name__},
            )

    # Same path the Spring Boot actuator used.
    app.add_api_route("/actuator/health", health, methods=["GET"], include_in_schema=False)

    return app
