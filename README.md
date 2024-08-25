# Final Project - DevOps with Docker By Hadar Asher 207767005

## Project Overview

This project is a DevOps-oriented application that includes a Spring Boot application, Redis for caching, PostgreSQL for database management, and a Python testing service. The application is containerized using Docker, and a Continuous Integration and Continuous Deployment (CI/CD) pipeline is orchestrated using Docker Compose in a Play with Docker environment.

## Project Structure

- **final-project**: Contains the Spring Boot application code.
- **redis**: A caching service used by the Spring Boot application.
- **postgres**: A PostgreSQL database service.
- **tester**: A Python-based testing service that ensures the CI/CD pipeline functions correctly.

## Prerequisites

- **Docker**: Ensure Docker is installed on your machine.
- **Docker Compose**: Required for orchestrating multi-container applications.

## Setup and Installation

1. **Clone the Repository**: Clone the repository to your local machine.
2. **Build and Launch the Application**: Use Docker Compose to build and start the application.
3. **Access the Application**: The Spring Boot application will be accessible via the browser at `http://localhost:8080`.

## Docker Configuration

The project includes the following Docker configurations:

- **Dockerfile for Spring Boot Application**: Defines the environment and dependencies for running the Spring Boot application.
- **Docker Compose Configuration**: Defines the services, networks, and volumes for the application, including Redis, PostgreSQL, and the Python testing service.

## Running Tests

Tests are included in the Python `tester` service. The tests will run automatically after the services are up and running.
