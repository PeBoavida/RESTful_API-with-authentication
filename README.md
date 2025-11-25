# Java Developer Project

A Spring Boot RESTful API application for user and external project management.

## Table of Contents

- [Features](#features)
- [Prerequisites](#prerequisites)
- [Running with Docker Compose](#running-with-docker-compose)
- [Running Locally](#running-locally-without-docker)
- [Database Configuration](#database-configuration)
- [API Endpoints](#api-endpoints)
- [Authentication](#authentication)
- [API Testing](#api-testing)
- [Building](#building)
- [Running Tests](#running-tests)
- [Docker Commands](#docker-commands)
- [Remote Debugging](#remote-debugging)
- [Project Structure](#project-structure)
- [Technology Stack](#technology-stack)

## Features

- **User Management**: Full CRUD operations (Create, Read, Update, Delete)
- **External Project Management**: Full CRUD operations for user external projects
- **HTTP Basic Authentication**: Secure API endpoints with user-based authentication
- **PostgreSQL Database**: Persistent data storage with Liquibase migrations
- **Docker Containerization**: Easy deployment with Docker Compose
- **RESTful API**: Proper error handling and validation
- **Comprehensive Testing**: Unit tests with H2 in-memory database
- **Monitoring**: Health checks and Prometheus metrics

## Prerequisites

- Java 21
- Docker and Docker Compose
- Gradle (or use the included Gradle wrapper)

## Running with Docker Compose

The easiest way to run the application is using Docker Compose, which will start both PostgreSQL and the application:

```bash
docker-compose up --build
```

This will:
1. Start a PostgreSQL 15 container on port 5432
2. Build and start the Spring Boot application on port 8080
3. Wait for PostgreSQL to be healthy before starting the app

The application will be available at: `http://localhost:8080`

## Running Locally (without Docker)

1. Start PostgreSQL locally or use the Docker container:
   ```bash
   docker-compose up postgres
   ```

2. Update `application.yml` with your PostgreSQL connection details, or set environment variables:
   ```bash
   export SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/testdb
   export SPRING_DATASOURCE_USERNAME=testuser
   export SPRING_DATASOURCE_PASSWORD=testpass
   ```

3. Run the application:
   ```bash
   ./gradlew bootRun
   ```
   
   Or use the convenience script with a custom Swagger UI password:
   ```bash
   ./bin/run-local.sh [password]
   ```
   
   Example:
   ```bash
   ./bin/run-local.sh mypassword123
   ```
   
   If no password is provided, it defaults to `swagger123`.

## Database Configuration

Default PostgreSQL configuration (can be overridden via environment variables):
- Database: `testdb`
- Username: `testuser`
- Password: `testpass`
- Port: `5432`

## API Endpoints

### User Management

- `POST /api/users` - Create a new user (public)
- `GET /api/users/{id}` - Get user by ID (authenticated)
- `PUT /api/users/{id}` - Update user (authenticated)
- `DELETE /api/users/{id}` - Delete user (authenticated)

### External Projects

- `POST /api/users/{userId}/external-projects` - Create external project for user (authenticated)
- `GET /api/users/{userId}/external-projects` - Get all external projects for user (authenticated)
- `GET /api/users/{userId}/external-projects/{projectId}` - Get specific external project (authenticated)
- `PUT /api/users/{userId}/external-projects/{projectId}` - Update external project (authenticated, partial update supported)
- `DELETE /api/users/{userId}/external-projects/{projectId}` - Delete external project (authenticated)

### Monitoring

- `GET /actuator/health` - Health check (public)
- `GET /actuator/prometheus` - Prometheus metrics (public)

## Authentication

The application uses HTTP Basic Authentication. A default user is automatically created via Liquibase for Swagger UI testing:

**Default Swagger UI User:**
- Email: `swagger-ui@example.com`
- Password: `swagger123`

You can also create additional users via `POST /api/users`. To authenticate, use:
- Username: user's email
- Password: user's password

Example:
```bash
curl -u swagger-ui@example.com:swagger123 http://localhost:8080/api/users/1
```

In Swagger UI, click the "Authorize" button and use the default credentials to test authenticated endpoints.

## API Testing

For comprehensive API testing with curl commands, see [CURL_COMMANDS.md](bin/CURL_COMMANDS.md). This file contains all curl commands organized in the correct order for testing all endpoints.

You can also use the provided bash script to run all tests sequentially:
```bash
./curl-commands.sh
```

For additional Spring Boot reference documentation, see [HELP.md](HELP.md).

## Building

```bash
./gradlew build
```

## Running Tests

Tests use PostgreSQL via Testcontainers (configured in `application-test.yml` and `TestcontainersConfig.java`):

```bash
./gradlew test
```

**Note:** Testcontainers will automatically start a PostgreSQL 15 container before running tests. Ensure Docker is running on your system. The container is shared across test classes for efficiency and automatically cleaned up after tests complete.

## Docker Commands

- Start services: `docker-compose up`
- Start in background: `docker-compose up -d`
- Stop services: `docker-compose down`
- View logs: `docker-compose logs -f`
- Rebuild: `docker-compose up --build`

## Remote Debugging

The application exposes a debug port on **5005** for remote debugging. To connect your IDE:

1. Start the application: `docker compose up` or `./bin/run-local.sh [password]`
2. Configure your IDE to connect to:
   - Host: `localhost`
   - Port: `5005`
   - Debug mode: `Attach to remote JVM`

The debugger will attach without suspending the application (suspend=n), so the app starts normally and waits for debugger connection.

## Project Structure

```
src/
├── main/
│   ├── java/
│   │   └── com/sky/pedroboavida/test/
│   │       ├── config/          # Security and configuration
│   │       ├── controller/       # REST controllers
│   │       ├── dto/              # Data Transfer Objects
│   │       ├── entity/           # JPA entities
│   │       ├── exception/        # Custom exceptions
│   │       ├── repository/       # JPA repositories
│   │       └── service/          # Business logic
│   └── resources/
│       └── application.yml       # Application configuration
└── test/
    └── java/                     # Unit tests
```

## Technology Stack

- **Framework**: Spring Boot 3.2.0
- **Security**: Spring Security with HTTP Basic Authentication
- **Data Access**: Spring Data JPA
- **Database**: PostgreSQL 15 (production), H2 (testing)
- **Database Migrations**: Liquibase
- **Build Tool**: Gradle
- **Utilities**: Lombok
- **Containerization**: Docker & Docker Compose
- **Testing**: JUnit 5, Mockito
- **Monitoring**: Spring Boot Actuator, Prometheus

