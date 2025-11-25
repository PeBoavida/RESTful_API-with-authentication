# API Testing - Curl Commands

This document contains curl commands to test all API endpoints in the correct order.

**Base URL:** `http://localhost:8080`

**Default Swagger User (for testing):**
- Email: `swagger-ui@example.com`
- Password: `swagger123`

## Table of Contents

- [Health Check](#1-health-check-public---no-auth-required)
- [User Management](#user-management)
  - [Create User](#2-create-user-public---no-auth-required)
  - [Get User](#3-get-user-by-id-authenticated)
  - [Update User](#4-update-user-authenticated)
  - [Delete User](#10-delete-user-authenticated)
- [External Projects](#external-projects)
  - [Create Project](#5-create-external-project-authenticated)
  - [Get All Projects](#6-get-all-external-projects-for-user-authenticated)
  - [Get Specific Project](#7-get-specific-external-project-authenticated)
  - [Update Project](#8-update-external-project-authenticated)
  - [Delete Project](#9-delete-external-project-authenticated)
- [Monitoring](#bonus-additional-endpoints)
- [Complete Test Sequence](#complete-test-sequence)
- [Authentication Notes](#authentication-notes)
- [Error Handling](#error-handling)

---

## User Management

### 1. Health Check (Public - No Auth Required)

```bash
curl -X GET http://localhost:8080/actuator/health
```

---

### 2. Create User (Public - No Auth Required)

```bash
curl -X POST http://localhost:8080/api/users \
  -H "Content-Type: application/json" \
  -d '{
    "email": "testuser@example.com",
    "password": "password123",
    "name": "Test User"
  }'
```

**Note:** Save the returned `id` from the response for subsequent requests. In the examples below, we'll use `1` as the user ID.

**Request Body Fields:**
- `email` (required, String, max 200 characters, must be valid email): User's email address (also used for authentication)
- `password` (required, String, 6-129 characters): User's password
- `name` (optional, String, max 120 characters): User's name

**Response:** Returns a `UserDTO` object with `id`, `email`, `name`, and `externalProjects` fields.

---

### 3. Get User by ID (Authenticated)

```bash
curl -u testuser@example.com:password123 \
  -X GET http://localhost:8080/api/users/1 \
  -H "Content-Type: application/json"
```

**Alternative:** Using default swagger user:
```bash
curl -u swagger-ui@example.com:swagger123 \
  -X GET http://localhost:8080/api/users/1 \
  -H "Content-Type: application/json"
```

---

### 4. Update User (Authenticated)

```bash
curl -u testuser@example.com:password123 \
  -X PUT http://localhost:8080/api/users/1 \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Updated Test User",
    "email": "updated@example.com"
  }'
```

**Note:** All fields in UpdateUserRequest are optional. You can update just name, just email, just password, or any combination.

**Request Body Fields:**
- `email` (optional, String, max 200 characters, must be valid email): User's email address
- `password` (optional, String, 6-129 characters): User's password
- `name` (optional, String, max 120 characters): User's name

---

## External Projects

### 5. Create External Project (Authenticated)

```bash
curl -u testuser@example.com:password123 \
  -X POST http://localhost:8080/api/users/1/external-projects \
  -H "Content-Type: application/json" \
  -d '{
    "id": "project-123",
    "name": "My External Project"
  }'
```

**Note:** Replace `1` with the actual user ID, and `project-123` with your desired project ID.

**Request Body Fields:**
- `id` (required, String, max 200 characters): Unique identifier for the external project
- `name` (required, String, max 120 characters): Name of the external project

---

### 6. Get All External Projects for User (Authenticated)

```bash
curl -u testuser@example.com:password123 \
  -X GET http://localhost:8080/api/users/1/external-projects \
  -H "Content-Type: application/json"
```

---

### 7. Get Specific External Project (Authenticated)

```bash
curl -u testuser@example.com:password123 \
  -X GET http://localhost:8080/api/users/1/external-projects/project-123 \
  -H "Content-Type: application/json"
```

**Note:** Replace `1` with the actual user ID and `project-123` with the actual project ID.

---

### 8. Update External Project (Authenticated)

```bash
curl -u testuser@example.com:password123 \
  -X PUT http://localhost:8080/api/users/1/external-projects/project-123 \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Updated Project Name"
  }'
```

**Note:** Replace `1` with the actual user ID and `project-123` with the actual project ID. The `name` field is optional - only provided fields will be updated (partial update).

**Request Body Fields:**
- `name` (optional, String, max 120 characters): The new name for the external project

---

### 9. Delete External Project (Authenticated)

```bash
curl -u testuser@example.com:password123 \
  -X DELETE http://localhost:8080/api/users/1/external-projects/project-123 \
  -H "Content-Type: application/json"
```

---

### 10. Delete User (Authenticated)

```bash
curl -u testuser@example.com:password123 \
  -X DELETE http://localhost:8080/api/users/1 \
  -H "Content-Type: application/json"
```

---

## Bonus: Additional Endpoints

### Prometheus Metrics (Public)

```bash
curl -X GET http://localhost:8080/actuator/prometheus
```

---

## Complete Test Sequence

Here's a complete sequence you can run to test all endpoints:

```bash
# 1. Health check
curl -X GET http://localhost:8080/actuator/health

# 2. Create user
USER_RESPONSE=$(curl -s -X POST http://localhost:8080/api/users \
  -H "Content-Type: application/json" \
  -d '{"email":"testuser@example.com","password":"password123","name":"Test User"}')
USER_ID=$(echo $USER_RESPONSE | grep -o '"id":[0-9]*' | head -1 | cut -d':' -f2)
echo "Created user with ID: $USER_ID"

# 3. Get user
curl -u testuser@example.com:password123 \
  -X GET http://localhost:8080/api/users/$USER_ID

# 4. Update user
curl -u testuser@example.com:password123 \
  -X PUT http://localhost:8080/api/users/$USER_ID \
  -H "Content-Type: application/json" \
  -d '{"name":"Updated Name"}'

# 5. Create external project
curl -u testuser@example.com:password123 \
  -X POST http://localhost:8080/api/users/$USER_ID/external-projects \
  -H "Content-Type: application/json" \
  -d '{"id":"project-123","name":"My Project"}'

# 6. Get all projects
curl -u testuser@example.com:password123 \
  -X GET http://localhost:8080/api/users/$USER_ID/external-projects

# 7. Get specific project
curl -u testuser@example.com:password123 \
  -X GET http://localhost:8080/api/users/$USER_ID/external-projects/project-123

# 8. Update project
curl -u testuser@example.com:password123 \
  -X PUT http://localhost:8080/api/users/$USER_ID/external-projects/project-123 \
  -H "Content-Type: application/json" \
  -d '{"name":"Updated Project Name"}'

# 9. Delete project
curl -u testuser@example.com:password123 \
  -X DELETE http://localhost:8080/api/users/$USER_ID/external-projects/project-123

# 10. Delete user
curl -u testuser@example.com:password123 \
  -X DELETE http://localhost:8080/api/users/$USER_ID
```

---

## Authentication Notes

- **HTTP Basic Authentication** is used for all protected endpoints
- Format: `curl -u email:password`
- The email is the username for authentication
- Only `POST /api/users` is public (no authentication required)
- All other endpoints require authentication

## Response Formats

### Success Responses

**User DTO:**
```json
{
  "id": 1,
  "email": "testuser@example.com",
  "name": "Test User",
  "externalProjects": []
}
```

**External Project DTO:**
```json
{
  "id": "project-123",
  "name": "My External Project",
  "userId": 1
}
```

**List of External Projects:**
```json
[
  {
    "id": "project-123",
    "name": "My External Project",
    "userId": 1
  },
  {
    "id": "project-456",
    "name": "Another Project",
    "userId": 1
  }
]
```

### Error Responses

**Error Response Format:**
```json
{
  "timestamp": "2024-01-15T10:30:00",
  "status": 404,
  "error": "Not Found",
  "message": "User not found with id: 999",
  "path": "/api/users/999",
  "details": []
}
```

## Error Handling

### Common HTTP Status Codes

- **200 OK**: Request successful
- **201 Created**: Resource created successfully
- **204 No Content**: Resource deleted successfully
- **400 Bad Request**: Invalid request data or validation errors
- **401 Unauthorized**: Authentication required or invalid credentials
- **404 Not Found**: Resource not found
- **409 Conflict**: Resource already exists (e.g., duplicate email or project ID)

### Troubleshooting

**Authentication Errors (401):**
1. Verify the user exists (create it first with `POST /api/users`)
2. Check that you're using the correct email and password
3. Ensure the credentials match the user you created
4. Verify you're using HTTP Basic Authentication format: `curl -u email:password`

**Not Found Errors (404):**
- Verify the user ID or project ID exists
- Check that you're using the correct path parameters
- Ensure the resource belongs to the authenticated user (for external projects)

**Validation Errors (400):**
- Check that required fields are provided
- Verify field constraints (email format, string lengths, etc.)
- Review the error response `details` field for specific validation messages

**Conflict Errors (409):**
- For users: Email address already exists
- For external projects: Project ID already exists for the user

