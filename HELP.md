# Getting Started

This is a Spring Boot RESTful API application for user and external project management.

## Project Documentation

* **[README.md](README.md)** - Complete project documentation, setup instructions, and API overview
* **[CURL_COMMANDS.md](bin/CURL_COMMANDS.md)** - Comprehensive curl commands for testing all API endpoints

## Quick Start

1. **Run with Docker Compose:**
   ```bash
   docker-compose up --build
   ```

2. **Or run locally:**
   ```bash
   ./gradlew bootRun
   ```

3. **Test the API:**
   ```bash
   ./curl-commands.sh
   ```

For detailed instructions, see [README.md](README.md).

## Reference Documentation

### Spring Boot 3.2.0 Documentation
* [Spring Boot Reference Documentation](https://docs.spring.io/spring-boot/3.2.0/reference/htmlsingle/)
* [Spring Boot Gradle Plugin Reference Guide](https://docs.spring.io/spring-boot/3.2.0/gradle-plugin/reference/html/)
* [Spring Web](https://docs.spring.io/spring-boot/3.2.0/reference/web/servlet.html)
* [Spring Data JPA](https://docs.spring.io/spring-boot/3.2.0/reference/data/sql.html#data.sql.jpa-and-spring-data)
* [Spring Security](https://docs.spring.io/spring-boot/3.2.0/reference/web/spring-security.html)

### Build Tools
* [Official Gradle documentation](https://docs.gradle.org)
* [Gradle Build Scans – insights for your project's build](https://scans.gradle.com#gradle)

### Spring Guides
The following guides illustrate how to use some features concretely:

* [Building a RESTful Web Service](https://spring.io/guides/gs/rest-service/)
* [Building REST services with Spring](https://spring.io/guides/tutorials/rest/)
* [Accessing Data with JPA](https://spring.io/guides/gs/accessing-data-jpa/)
* [Securing a Web Application](https://spring.io/guides/gs/securing-web/)

## Technology Stack

This project uses:
- **Spring Boot 3.2.0**
- **Java 21**
- **PostgreSQL 15** (production) / **H2** (testing)
- **Gradle** (build tool)
- **Docker & Docker Compose** (containerization)
- **Liquibase** (database migrations)
- **Spring Security** (HTTP Basic Authentication)
- **Spring Boot Actuator** (monitoring and metrics)

