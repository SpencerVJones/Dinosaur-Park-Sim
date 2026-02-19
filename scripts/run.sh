#!/usr/bin/env bash
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
cd "$ROOT_DIR"

APP_PORT="${APP_PORT:-8080}"
mvn clean -Dspring-boot.run.jvmArguments="-Dserver.port=${APP_PORT}" spring-boot:run
