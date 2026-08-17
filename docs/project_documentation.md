# Carbon Footprint Tracker — Project Documentation

## 1. Project Overview

Carbon Footprint Tracker is a full-stack application built using a
microservices architecture to track and analyze users' carbon emissions.

The application allows users to record activities across three major areas:

- Transportation
- Energy consumption
- Food consumption

The collected data is used to calculate users' carbon footprint. The
Analytics Service provides aggregated footprint information, while the
Leaderboard Service uses analytics data to compare users.

The application consists of multiple Spring Boot backend microservices
and a React/Vite frontend.

The backend uses Eureka Service Registry for service discovery and
Spring Cloud Gateway as the central entry point for backend requests.

The application is containerized using Docker and Docker Compose.

CI/CD is implemented using GitHub, GitHub Webhooks, Jenkins, Docker Hub,
and Google Cloud Platform.

---

## 2. Technology Stack

### Frontend

- React
- Vite
- Axios

### Backend

- Java 21
- Spring Boot 3.5.6
- Spring Data JPA
- Spring Security
- JWT
- MySQL

### Microservices

- Spring Cloud Eureka
- Spring Cloud Gateway
- Spring Cloud OpenFeign

### Testing

- JUnit
- Mockito

### Build

- Maven

### Containerization

- Docker
- Docker Compose
- Docker Buildx

### CI/CD

- Git
- GitHub
- GitHub Webhooks
- Jenkins
- Docker Hub

### Cloud / Deployment

- Google Cloud Platform (GCP)
- Compute Engine
- SSH-based deployment
- Nginx

---

## 3. System Architecture

### 3.1 High-Level Architecture

```text
                         ┌─────────────────┐
                         │      User       │
                         └────────┬────────┘
                                  │
                                  ▼
                         ┌─────────────────┐
                         │ React + Vite    │
                         │    Frontend     │
                         └────────┬────────┘
                                  │
                                  ▼
                         ┌─────────────────┐
                         │  API Gateway    │
                         │ Spring Cloud    │
                         │    Gateway      │
                         └────────┬────────┘
                                  │
                    ┌─────────────┼─────────────┐
                    │             │             │
                    ▼             ▼             ▼
               Auth Service  Transport      Energy
                              Service        Service
                    │             │             │
                    └─────────────┼─────────────┘
                                  │
                                  ▼
                             Food Service
                                  │
                                  ▼
                           Analytics Service
                                  │
                                  ▼
                           Leaderboard Service

                         ┌─────────────────┐
                         │ Eureka Service  │
                         │    Registry     │
                         └─────────────────┘

                         ┌─────────────────┐
                         │      MySQL      │
                         └─────────────────┘

Eureka is a service registry used for service discovery. It is not itself
a request-processing step between the frontend and backend services.

### 3.2 High-Level Request Flow
User
  ↓
React/Vite Frontend
  ↓
API Gateway
  ↓
Required Backend Service
  ↓
Service Logic / Repository
  ↓
MySQL

For service-to-service communication, services use Eureka service names
and OpenFeign rather than hardcoded service IP addresses.

### 3.3 Core Services
Service Registry — Eureka-based service discovery
API Gateway — centralized entry point for backend APIs
Auth Service — registration, login, and JWT authentication
Transport Service — transportation data
Energy Service — energy consumption data
Food Service — food consumption data
Analytics Service — monthly carbon footprint calculation
Leaderboard Service — ranking based on analytics data
Frontend — React/Vite user interface

### 3.4 Deployment Architecture

The deployed system uses two GCP Compute Engine VMs:

                         GitHub
                           │
                        Webhook
                           ▼
                    ┌──────────────┐
                    │ Jenkins VM   │
                    │              │
                    │ Build Images │
                    │ Push Images  │
                    └──────┬───────┘
                           │
                           ▼
                      Docker Hub
                           │
                           │ Pull
                           ▼
                 ┌───────────────────┐
                 │ Application VM    │
                 │                   │
                 │ Docker Compose    │
                 │                   │
                 │ Frontend          │
                 │ API Gateway       │
                 │ Microservices     │
                 │ Eureka            │
                 │ MySQL             │
                 └───────────────────┘
4. Request & Data Flows
4.1 User Registration
Frontend
  ↓
API Gateway
  ↓
Auth Service
  ↓
AuthController
  ↓
AuthService
  ↓
UserRepository
  ↓
MySQL

Endpoint:

POST /api/auth/register

The registration endpoint is publicly accessible.

The Auth Service:

Checks whether the username already exists.
Checks whether the email already exists.
Validates the email format.
Validates password strength.
Hashes the password using BCrypt.
Assigns the default role USER.
Saves the user in MySQL.

The plain-text password is never stored.

4.2 User Login
Frontend
  ↓
API Gateway
  ↓
Auth Service
  ↓
AuthController
  ↓
AuthService
  ↓
UserRepository
  ↓
MySQL

Endpoint:

POST /api/auth/login

The Auth Service:

Finds the user by username.
Verifies the password using BCrypt.
Generates a JWT after successful authentication.
Returns the JWT along with the username and role.
4.3 JWT Generation

JWTs are generated by JwtUtil.

The token contains:

Subject: username
Claim: role
Issued-at timestamp
Expiration timestamp

The token is signed using the HS256 algorithm.

Token expiration is configured for 1 hour.

4.4 JWT Authentication

Protected requests contain the JWT in the HTTP Authorization header:

Authorization: Bearer <JWT>

The JwtAuthFilter executes once per request.

Request
  ↓
Read Authorization header
  ↓
Extract Bearer token
  ↓
Parse and validate JWT
  ↓
Extract username
  ↓
Create Spring Security Authentication
  ↓
Store authentication in SecurityContext
  ↓
Continue request

If JWT processing fails, the filter logs the error and continues the
filter chain. Spring Security then determines whether the requested
endpoint is allowed.

4.5 Public and Protected Endpoints

The following Auth Service endpoints are publicly accessible:

/api/auth/register
/api/auth/login
/api/auth/users
/v3/api-docs/**
/swagger-ui/**
/swagger-ui.html

All other Auth Service endpoints require authentication.

4.6 Password Security

Passwords are hashed using BCryptPasswordEncoder.

Registration:

Plain password
  ↓
BCrypt hashing
  ↓
Hashed password stored in MySQL

Login:

Entered password
  ↓
BCrypt comparison
  ↓
Stored hashed password

The original password is never stored.

5. Service-to-Service Communication

Service-to-service communication is implemented using Spring Cloud
OpenFeign.

Feign clients use Eureka service names rather than hardcoded service
IP addresses.

5.1 Analytics Service

The Analytics Service collects data required to calculate monthly carbon
emissions.

It communicates with:

Auth Service — retrieves registered usernames
Transport Service — retrieves monthly transportation data
Energy Service — retrieves monthly energy data
Food Service — retrieves monthly food data

Example:

@FeignClient(name = "energy-service")

Flow:

Analytics Service
      │
      │ Feign
      ▼
Energy Service
      │
      ▼
Monthly energy data
      │
      ▼
Analytics Service

The Analytics Service combines responses from the tracking services and
uses the collected data to calculate monthly carbon emissions.

5.2 Leaderboard Service

The Leaderboard Service communicates with the Analytics Service using
OpenFeign.

@FeignClient(name = "analytics-service")

Flow:

Leaderboard Service
        │
        │ Feign
        ▼
Analytics Service
        │
        ▼
Monthly analytics data
        │
        ▼
Leaderboard ranking
5.3 Why OpenFeign?

OpenFeign provides a declarative way to make HTTP calls between
microservices.

Instead of manually creating HTTP clients and hardcoding service URLs,
a service defines an interface using @FeignClient.

5.4 Service Discovery

Feign clients identify services using Eureka service names, such as:

auth-service
energy-service
transport-service
food-service
analytics-service

This avoids hardcoding the IP address and port of each service.

6. Docker & Deployment

The application is containerized using Docker and orchestrated using
Docker Compose.

6.1 Docker Containers

The Docker Compose deployment consists of:

MySQL
Service Registry
API Gateway
Auth Service
Transport Service
Energy Service
Food Service
Analytics Service
Leaderboard Service
Frontend

Each backend microservice has its own Docker image.

The frontend uses a multi-stage Dockerfile:

Node.js is used to build the React/Vite application.
Nginx is used to serve the generated static files.

Backend services use multi-stage Dockerfiles:

Maven + Java 21 is used for the build stage.
Eclipse Temurin Java 21 JRE is used for the runtime stage.
6.2 Docker Network

All application containers are connected to:

carbon-net

This is a custom Docker bridge network.

Containers communicate using Docker service/container names.

Examples:

auth-service → mysql:3306
api-gateway → service-registry:8761
6.3 Database Persistence

MySQL uses the named Docker volume:

mysql_data

mounted to:

/var/lib/mysql

This keeps MySQL data persistent when the MySQL container is restarted
or recreated.

6.4 Port Mapping
Component	Port
Frontend	3000
API Gateway	8961
Auth Service	8861
Transport Service	8080
Energy Service	8081
Food Service	8082
Analytics Service	8083
Leaderboard Service	8084
Eureka Service Registry	8761
MySQL	3307
6.5 Frontend Configuration

Vite requires the API Gateway URL at build time.

The Docker Compose configuration passes:

VITE_GATEWAY_BASE_URL

as a Docker build argument.

The value must be reachable from the user's browser. Therefore, the
frontend does not use Docker-internal service names for its API URL.

6.6 Docker Hub

Built images are pushed to Docker Hub using the naming pattern:

shashi1900/carbon-footprint-tracker-<service>

Images are tagged with both the Jenkins build number and latest.

Example:

shashi1900/carbon-footprint-tracker-api-gateway:10
shashi1900/carbon-footprint-tracker-api-gateway:latest
6.7 Application VM Deployment

The Application VM pulls images from Docker Hub and runs them using
Docker Compose.

Deployment commands:

docker compose pull
docker compose up -d
7. CI/CD Pipeline

The project uses Jenkins to automate Docker image building, publishing,
and deployment.

7.1 CI/CD Flow
Developer
   │
   │ git push
   ▼
GitHub (development branch)
   │
   │ Webhook
   ▼
Jenkins VM
   │
   ├── Checkout source code
   ├── Docker Buildx build
   ├── Tag images
   └── Push images
   │
   ▼
Docker Hub
   │
   │ SSH
   ▼
Application VM
   │
   ├── docker compose pull
   └── docker compose up -d
   │
   ▼
Updated Application
7.2 GitHub Webhook

A GitHub webhook is configured to trigger the Jenkins pipeline when
changes are pushed to the repository.

The pipeline uses the development branch.

7.3 Jenkins

Jenkins runs on a dedicated GCP Compute Engine VM.

The pipeline:

Checks out the repository.
Authenticates with Docker Hub.
Builds Docker images for all application services.
Tags images using the Jenkins build number.
Tags images as latest.
Pushes images to Docker Hub.
Connects to the Application VM using SSH.
Pulls the updated images.
Runs Docker Compose to update the application.
7.4 Docker Image Build

The pipeline builds nine images:

Service Registry
API Gateway
Auth Service
Transport Service
Energy Service
Food Service
Analytics Service
Leaderboard Service
Frontend

Each image receives two tags:

<build-number>
latest

Example:

carbon-footprint-tracker-api-gateway:10
carbon-footprint-tracker-api-gateway:latest
7.5 Docker Hub

Docker Hub acts as the container image registry between Jenkins and the
Application VM.

Jenkins pushes newly built images to Docker Hub.

The Application VM pulls those images during deployment.

7.6 SSH Deployment

Jenkins uses the gcp-app-server SSH credential to connect to the
Application VM.

Jenkins executes:

docker compose pull
docker compose up -d

This allows deployment without manually SSHing into the Application VM.

7.7 Build Optimization

Docker Buildx is used for image builds.

The Dockerfiles are structured to improve Docker layer caching.

Frontend:

Copy package.json
      ↓
npm ci
      ↓
Copy source
      ↓
npm run build

Backend:

Copy pom.xml
      ↓
Download Maven dependencies
      ↓
Copy source
      ↓
Maven package

This allows dependency layers to be reused when only application source
code changes.

7.8 Deployment Separation

Jenkins and the application runtime are hosted on separate VMs.

Jenkins VM
    │
    │ Build + Push
    ▼
Docker Hub
    │
    │ Pull
    ▼
Application VM
    │
    └── Docker Compose
         ├── Frontend
         ├── API Gateway
         ├── Backend Services
         ├── Eureka
         └── MySQL

This keeps CI/CD workloads separate from the application runtime.

8. Problems & Solutions
8.1 API Gateway Routing

Problem:
The API Gateway was initially not routing requests correctly to the
microservices.

Solution:
Switched to the reactive Spring Cloud Gateway setup and configured the
required routes and filters.

8.2 JWT Authentication Issues

Problem:
Protected APIs returned 401 Unauthorized or 403 Forbidden during
initial implementation.

Solution:
Debugged JWT extraction, the Authorization: Bearer <token> header,
JWT parsing, and the JwtAuthFilter configuration.

8.3 Frontend-to-Backend Connectivity

Problem:
The frontend initially attempted to communicate with the backend using
incorrect/local addresses after deployment.

Solution:
Configured VITE_GATEWAY_BASE_URL as a build-time Docker argument so
the generated frontend bundle points to the externally reachable API
Gateway.

8.4 GCP Firewall

Problem:
Services running inside the GCP VM were not externally reachable even
though the Docker containers were running.

Solution:
Configured GCP firewall rules and network tags for the required
application ports.

8.5 Static External IP

Problem:
The VM's external IP could change, causing the frontend/backend
configuration to become invalid.

Solution:
Configured a static external IP for the Application VM.

8.6 Docker Build Performance

Problem:
Building all microservice Docker images sequentially was taking a long
time.

Solution:
Optimized Dockerfiles for layer caching and used Docker Buildx.

8.7 Jenkins and Docker Access

Problem:
Jenkins initially could not communicate with the Docker daemon.

Solution:
Configured Docker access for the Jenkins environment so Jenkins could
build and push Docker images.

8.8 Docker Hub Authentication

Problem:
Jenkins initially failed to authenticate with Docker Hub.

Solution:
Configured Docker Hub credentials in Jenkins and used them during the
Docker login step.

8.9 Jenkins VM and Application VM Separation

Problem:
Running Jenkins and the application on the same VM caused resource
contention during Docker builds.

Solution:
Separated the infrastructure into:

Jenkins VM — CI/CD
Application VM — application runtime
8.10 Jenkins → Application VM SSH Deployment

Problem:
Jenkins initially received:

Permission denied (publickey)

when attempting to deploy to the Application VM.

Solution:
Configured an SSH deployment key and authorized the corresponding public
key on the Application VM.

Jenkins private key
        ↓
Jenkins credential: gcp-app-server
        ↓
Application VM ~/.ssh/authorized_keys
8.11 MySQL Data Persistence

Problem:
There was a concern that restarting or recreating containers would
delete registered users and database data.

Solution:
Configured the named Docker volume:

mysql_data
    ↓
/var/lib/mysql

This keeps MySQL data persistent across container recreation.

8.12 Jenkins Resource Usage

Problem:
Docker builds caused high CPU and memory usage on the Jenkins VM.

Solution:
Separated Jenkins from the Application VM and monitored resource usage
using:

docker stats
free -h
df -h