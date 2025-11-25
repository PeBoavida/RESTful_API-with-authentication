#!/bin/bash

# Script to run the application locally with a custom Swagger UI user password
# Usage: ./bin/run-local.sh [password]
# If no password is provided, defaults to "swagger123"

set -e

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_DIR="$(cd "$SCRIPT_DIR/.." && pwd)"

# Get password from argument or use default
SWAGGER_PASSWORD="${1:-swagger123}"

echo "Starting application with Swagger UI user password..."
echo "Swagger UI credentials:"
echo "  Email: swagger-ui@example.com"
echo "  Password: $SWAGGER_PASSWORD"
echo ""

cd "$PROJECT_DIR"

# Export the password as environment variable
export SWAGGER_UI_PASSWORD="$SWAGGER_PASSWORD"

# Source SDKMAN if available
if [ -s "$HOME/.sdkman/bin/sdkman-init.sh" ]; then
    source "$HOME/.sdkman/bin/sdkman-init.sh"
    sdk env 2>/dev/null || true
fi

docker compose up

