#!/bin/bash

# API Testing Script - Curl Commands
# Base URL - adjust if running on different host/port
BASE_URL="http://localhost:8080"

echo "=========================================="
echo "API Testing - Curl Commands"
echo "=========================================="
echo ""

# Colors for output
GREEN='\033[0;32m'
BLUE='\033[0;34m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# ============================================
# 1. HEALTH CHECK (Public - No Auth Required)
# ============================================
echo -e "${BLUE}1. Health Check${NC}"
echo "curl -X GET $BASE_URL/actuator/health"
curl -X GET "$BASE_URL/actuator/health"
echo -e "\n"

# ============================================
# 2. CREATE USER (Public - No Auth Required)
# ============================================
echo -e "${BLUE}2. Create User${NC}"
echo "curl -X POST $BASE_URL/api/users -H 'Content-Type: application/json' -d '{\"email\":\"testuser@example.com\",\"password\":\"password123\",\"name\":\"Test User\"}'"
RESPONSE=$(curl -s -X POST "$BASE_URL/api/users" \
  -H "Content-Type: application/json" \
  -d '{
    "email": "testuser@example.com",
    "password": "password123",
    "name": "Test User"
  }')
echo "$RESPONSE" | jq '.' 2>/dev/null || echo "$RESPONSE"

# Extract user ID from response (assuming JSON response with id field)
USER_ID=$(echo "$RESPONSE" | grep -o '"id":[0-9]*' | head -1 | cut -d':' -f2)
if [ -z "$USER_ID" ]; then
  USER_ID="1"  # Fallback to ID 1 if extraction fails
fi
echo -e "${GREEN}Created user with ID: $USER_ID${NC}\n"

# ============================================
# 3. GET USER BY ID (Authenticated)
# ============================================
echo -e "${BLUE}3. Get User by ID${NC}"
echo "curl -u testuser@example.com:password123 -X GET $BASE_URL/api/users/$USER_ID"
curl -u "testuser@example.com:password123" -X GET "$BASE_URL/api/users/$USER_ID" \
  -H "Content-Type: application/json" | jq '.' 2>/dev/null || curl -u "testuser@example.com:password123" -X GET "$BASE_URL/api/users/$USER_ID"
echo -e "\n"

# ============================================
# 4. UPDATE USER (Authenticated)
# ============================================
echo -e "${BLUE}4. Update User${NC}"
echo "curl -u testuser@example.com:password123 -X PUT $BASE_URL/api/users/$USER_ID -H 'Content-Type: application/json' -d '{\"name\":\"Updated Test User\",\"email\":\"updated@example.com\"}'"
curl -u "testuser@example.com:password123" -X PUT "$BASE_URL/api/users/$USER_ID" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Updated Test User",
    "email": "updated@example.com"
  }' | jq '.' 2>/dev/null || curl -u "testuser@example.com:password123" -X PUT "$BASE_URL/api/users/$USER_ID" \
  -H "Content-Type: application/json" \
  -d '{"name": "Updated Test User", "email": "updated@example.com"}'
echo -e "\n"

# ============================================
# 5. CREATE EXTERNAL PROJECT (Authenticated)
# ============================================
echo -e "${BLUE}5. Create External Project${NC}"
echo "curl -u testuser@example.com:password123 -X POST $BASE_URL/api/users/$USER_ID/external-projects -H 'Content-Type: application/json' -d '{\"id\":\"project-123\",\"name\":\"My External Project\"}'"
PROJECT_RESPONSE=$(curl -s -u "testuser@example.com:password123" -X POST "$BASE_URL/api/users/$USER_ID/external-projects" \
  -H "Content-Type: application/json" \
  -d '{
    "id": "project-123",
    "name": "My External Project"
  }')
echo "$PROJECT_RESPONSE" | jq '.' 2>/dev/null || echo "$PROJECT_RESPONSE"

PROJECT_ID="project-123"
echo -e "${GREEN}Created project with ID: $PROJECT_ID${NC}\n"

# Create a second project for testing
echo -e "${YELLOW}Creating second external project...${NC}"
curl -s -u "testuser@example.com:password123" -X POST "$BASE_URL/api/users/$USER_ID/external-projects" \
  -H "Content-Type: application/json" \
  -d '{
    "id": "project-456",
    "name": "Another External Project"
  }' | jq '.' 2>/dev/null || echo "Second project created"
echo -e "\n"

# ============================================
# 6. GET ALL EXTERNAL PROJECTS (Authenticated)
# ============================================
echo -e "${BLUE}6. Get All External Projects for User${NC}"
echo "curl -u testuser@example.com:password123 -X GET $BASE_URL/api/users/$USER_ID/external-projects"
curl -u "testuser@example.com:password123" -X GET "$BASE_URL/api/users/$USER_ID/external-projects" \
  -H "Content-Type: application/json" | jq '.' 2>/dev/null || curl -u "testuser@example.com:password123" -X GET "$BASE_URL/api/users/$USER_ID/external-projects"
echo -e "\n"

# ============================================
# 7. GET SPECIFIC EXTERNAL PROJECT (Authenticated)
# ============================================
echo -e "${BLUE}7. Get Specific External Project${NC}"
echo "curl -u testuser@example.com:password123 -X GET $BASE_URL/api/users/$USER_ID/external-projects/$PROJECT_ID"
curl -u "testuser@example.com:password123" -X GET "$BASE_URL/api/users/$USER_ID/external-projects/$PROJECT_ID" \
  -H "Content-Type: application/json" | jq '.' 2>/dev/null || curl -u "testuser@example.com:password123" -X GET "$BASE_URL/api/users/$USER_ID/external-projects/$PROJECT_ID"
echo -e "\n"

# ============================================
# 8. UPDATE EXTERNAL PROJECT (Authenticated)
# ============================================
echo -e "${BLUE}8. Update External Project${NC}"
echo "curl -u testuser@example.com:password123 -X PUT $BASE_URL/api/users/$USER_ID/external-projects/$PROJECT_ID -H 'Content-Type: application/json' -d '{\"name\":\"Updated Project Name\"}'"
curl -u "testuser@example.com:password123" -X PUT "$BASE_URL/api/users/$USER_ID/external-projects/$PROJECT_ID" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Updated Project Name"
  }' | jq '.' 2>/dev/null || curl -u "testuser@example.com:password123" -X PUT "$BASE_URL/api/users/$USER_ID/external-projects/$PROJECT_ID" \
  -H "Content-Type: application/json" \
  -d '{"name": "Updated Project Name"}'
echo -e "\n"

# ============================================
# 9. DELETE EXTERNAL PROJECT (Authenticated)
# ============================================
echo -e "${BLUE}8. Delete External Project${NC}"
echo "curl -u testuser@example.com:password123 -X DELETE $BASE_URL/api/users/$USER_ID/external-projects/$PROJECT_ID"
curl -u "testuser@example.com:password123" -X DELETE "$BASE_URL/api/users/$USER_ID/external-projects/$PROJECT_ID" \
  -H "Content-Type: application/json" -w "\nHTTP Status: %{http_code}\n"
echo -e "\n"

# ============================================
# 10. DELETE USER (Authenticated)
# ============================================
echo -e "${BLUE}9. Delete User${NC}"
echo "curl -u testuser@example.com:password123 -X DELETE $BASE_URL/api/users/$USER_ID"
curl -u "testuser@example.com:password123" -X DELETE "$BASE_URL/api/users/$USER_ID" \
  -H "Content-Type: application/json" -w "\nHTTP Status: %{http_code}\n"
echo -e "\n"

# ============================================
# BONUS: Test with Default Swagger User
# ============================================
echo -e "${YELLOW}=========================================="
echo "BONUS: Testing with Default Swagger User"
echo "==========================================${NC}\n"

echo -e "${BLUE}Get User 1 (using default swagger user)${NC}"
echo "curl -u swagger-ui@example.com:swagger123 -X GET $BASE_URL/api/users/1"
curl -u "swagger-ui@example.com:swagger123" -X GET "$BASE_URL/api/users/1" \
  -H "Content-Type: application/json" | jq '.' 2>/dev/null || curl -u "swagger-ui@example.com:swagger123" -X GET "$BASE_URL/api/users/1"
echo -e "\n"

echo -e "${BLUE}Prometheus Metrics (Public)${NC}"
echo "curl -X GET $BASE_URL/actuator/prometheus"
curl -X GET "$BASE_URL/actuator/prometheus" | head -20
echo -e "\n"

echo -e "${GREEN}=========================================="
echo "All API tests completed!"
echo "==========================================${NC}"

