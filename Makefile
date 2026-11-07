# piFrame gallery: starts the photoDB database and this API. Run from this folder.
#
#   make            -> list commands
#   make start      -> start the database, then the gallery API (Ctrl+C stops the API)
#
# Needs Docker, Python 3.10+, GNU make, and the photo-db repo cloned next to this
# one as ../photoDB (or pass DB_DIR=/path/to/photoDB).

DB_DIR      ?= ../photoDB
GALLERY_DIR := .
PORT        ?= 9009

DB_CONTAINER := photoDB
DB_USER      := pg-user
DB_NAME      := pi_frame_displayable

# --- Platform differences (Windows venvs use Scripts/ and python.exe) --------
ifeq ($(OS),Windows_NT)
  PYTHON   ?= python
  VENV_BIN := Scripts
else
  PYTHON   ?= python3
  VENV_BIN := bin
endif

VENV     := .venv
VENV_PY  := $(VENV)/$(VENV_BIN)/python
VENV_OK  := $(VENV)/.installed

# Prefer "docker compose" (v2); fall back to the older "docker-compose".
COMPOSE := $(shell docker compose version >/dev/null 2>&1 && echo "docker compose" || echo "docker-compose")

.DEFAULT_GOAL := help
.PHONY: help start stop status \
        db-up db-wait db-down db-reset db-logs db-psql \
        gallery gallery-dev gallery-install gallery-test gallery-clean

help: ## List commands
	@echo "piFrame commands:"
	@grep -E '^[a-z-]+:.*## ' $(MAKEFILE_LIST) | awk -F':.*## ' '{printf "  make %-16s %s\n", $$1, $$2}'

# --- Everything ---------------------------------------------------------------

start: gallery ## Start the database and the gallery API

stop: db-down ## Stop the database (the API stops with Ctrl+C)

status: ## Show whether the database container and API are up
	@docker ps -a --filter "name=$(DB_CONTAINER)" --format "table {{.Names}}\t{{.Status}}\t{{.Ports}}"
	@curl -fsS "http://localhost:$(PORT)/health" 2>/dev/null && echo || echo "gallery API: not running on port $(PORT)"

# --- Database (photoDB) -------------------------------------------------------

db-up: ## Start Postgres (and pgAdmin on :16543) in Docker
	@test -f $(DB_DIR)/docker-compose.yml || { \
		echo "photoDB not found at $(DB_DIR). Clone photo-db next to this repo, or pass DB_DIR=..." >&2; \
		exit 1; \
	}
	cd $(DB_DIR) && $(COMPOSE) up --build -d

db-wait: db-up ## Start Postgres and wait until it accepts connections
	@echo "Waiting for Postgres..."
	@for i in $$(seq 1 30); do \
		docker exec $(DB_CONTAINER) pg_isready -q -U $(DB_USER) -d $(DB_NAME) && { echo "Postgres is ready."; exit 0; }; \
		sleep 1; \
	done; \
	echo "Postgres did not become ready after 30s. Try: make db-logs" >&2; \
	exit 1

# "stop" rather than "down": the data is in an anonymous volume, so removing the
# container would leave the next one with an empty database.
db-down: ## Stop the database containers (keeps data)
	cd $(DB_DIR) && $(COMPOSE) stop

db-reset: ## DELETE all database data and start fresh
	cd $(DB_DIR) && $(COMPOSE) down -v
	$(MAKE) db-wait

db-logs: ## Follow the Postgres logs
	cd $(DB_DIR) && $(COMPOSE) logs -f postgres-photo

db-psql: ## Open a psql shell in the database
	docker exec -it $(DB_CONTAINER) psql -U $(DB_USER) -d $(DB_NAME)

# --- Gallery API --------------------------------------------------------------

# Load .env if there is one. Recipes using this must run from $(GALLERY_DIR).
LOAD_ENV := set -a; [ ! -f .env ] || . ./.env; set +a
UVICORN  := .venv/$(VENV_BIN)/python -m uvicorn app.main:create_app --factory --host 0.0.0.0 --port $(PORT)

# Install comes first so a missing Python fails before Docker is touched.
gallery: gallery-install db-wait ## Start the database and the gallery API
	cd $(GALLERY_DIR) && $(LOAD_ENV) && exec $(UVICORN)

gallery-dev: gallery-install db-wait ## Same as gallery, but reloads on code changes
	cd $(GALLERY_DIR) && $(LOAD_ENV) && exec $(UVICORN) --reload --reload-dir app

gallery-install: $(VENV_OK) ## Create the virtualenv and install dependencies

$(VENV_OK): $(GALLERY_DIR)/requirements.txt $(GALLERY_DIR)/requirements-dev.txt
	@$(PYTHON) -c 'import sys; sys.exit(sys.version_info < (3, 10))' 2>/dev/null || { \
		echo "Python 3.10+ is required, but '$(PYTHON)' is missing or older." >&2; \
		echo "Install a newer Python, then run e.g.: make start PYTHON=python3.12" >&2; \
		exit 1; \
	}
	$(PYTHON) -m venv $(VENV)
	$(VENV_PY) -m pip install -r $(GALLERY_DIR)/requirements-dev.txt
	@touch $@

gallery-test: gallery-install ## Run the API tests (no database needed)
	cd $(GALLERY_DIR) && .venv/$(VENV_BIN)/python -m pytest

gallery-clean: ## Delete the virtualenv
	rm -rf $(VENV)
