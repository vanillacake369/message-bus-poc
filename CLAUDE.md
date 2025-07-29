# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

This is a multi-module Proof of Concept (PoC) for comparing message brokers (Kafka, RabbitMQ, and Pulsar) in a Spring Boot microservices architecture. The project simulates a flash sale order event processing system to evaluate message broker performance, cost, and features across domain-based services.

## Multi-Module Architecture

### Project Structure
```
message-bus-poc/
├── settings.gradle                 # Multi-module configuration
├── build.gradle                   # Parent build with common dependencies
├── docker-compose.yml             # Multi-container orchestration
├── shared/                        # Common module (events, config, utils)
├── order-service/                 # Publisher only - REST API + event publishing
├── inventory-service/             # Kafka consumer only
├── notification-service/          # RabbitMQ consumer only
└── analytics-service/             # Pulsar consumer only + analytics REST API
```

### Module Responsibilities
- **shared**: Common event DTOs, configurations, serialization utilities
- **order-service**: REST API for order creation, publishes to all 3 brokers for comparison
- **inventory-service**: Consumes from Kafka, manages stock with optimistic locking
- **notification-service**: Consumes from RabbitMQ, sends email/push notifications
- **analytics-service**: Consumes from Pulsar, provides real-time analytics REST API

## Build and Development Commands

### Development Workflow

#### For code changes to a specific service:
```bash
# 1. Build the specific module
./gradlew :order-service:build

# 2. Rebuild and restart only that service (fast)
docker-compose up -d --no-deps --build order-service
```

#### For dependency/configuration changes:
```bash
# 1. Build all modules
./gradlew build

# 2. Restart affected services
docker-compose up -d --build
```

#### Complete rebuild workflow:
```bash
# 1. Stop everything
docker-compose down

# 2. Clean build all modules
./gradlew clean build

# 3. Start fresh
docker-compose up -d --build
```

#### Quick verification:
```bash
# Check service health
docker-compose ps

# View logs for specific service
docker-compose logs -f order-service

# View all service logs
docker-compose logs -f
```

### Multi-Module Building
```bash
# Build all modules
./gradlew build

# Build specific module
./gradlew :order-service:build
./gradlew :inventory-service:build
./gradlew :notification-service:build
./gradlew :analytics-service:build

# Run specific service locally
./gradlew :order-service:bootRun
./gradlew :analytics-service:bootRun

# Clean all modules
./gradlew clean
```

### Docker Development
```bash
# Build and run all services with dependencies
docker-compose up --build

# Run only message brokers and database (for local development)
docker-compose up mysql kafka rabbitmq pulsar prometheus grafana

# Start services in background
docker-compose up -d --build

# Scale specific services for load testing
docker-compose up --scale inventory-service=3 --scale notification-service=2

# Stop specific service
docker-compose stop order-service

# Remove containers and networks
docker-compose down
```

### Testing
```bash
# Run all tests across modules
./gradlew test

# Test specific module
./gradlew :shared:test
./gradlew :order-service:test
./gradlew :inventory-service:test
./gradlew :notification-service:test
./gradlew :analytics-service:test
```

## Service Configuration

### Ports and Endpoints
- **order-service**: 8080 - REST API for order creation
- **inventory-service**: 8081 - Health checks only
- **notification-service**: 8082 - Health checks only  
- **analytics-service**: 8083 - Analytics REST API + health checks
- **MySQL**: 3306 - Multi-database (order_db, inventory_db, analytics_db)
- **Kafka**: 9092 - High-throughput inventory updates
- **RabbitMQ**: 5672 (management: 15672) - Reliable notifications
- **Pulsar**: 6650 (web: 8080) - Real-time analytics
- **Prometheus**: 9090 - Metrics collection
- **Grafana**: 3000 - Monitoring dashboards

### Message Broker Separation
- **Publishing**: Only order-service publishes events (clean publisher separation)
- **Kafka Consumer**: inventory-service only (stock management)
- **RabbitMQ Consumer**: notification-service only (email/push)
- **Pulsar Consumer**: analytics-service only (real-time aggregation)

## Key Dependencies by Module

### Shared Module
- Spring Boot starter, validation, Jackson for JSON serialization
- No messaging dependencies (pure DTOs and utilities)

### Order Service (Publisher)
- Web, JPA, Actuator + all 3 message broker publishers
- Spring Cloud Stream with Kafka, RabbitMQ, Pulsar binders

### Consumer Services
- **Inventory**: Kafka consumer + JPA + optimistic locking
- **Notification**: RabbitMQ consumer + WebFlux for external APIs
- **Analytics**: Pulsar consumer + JPA + Web for REST API

## Development Workflow

### Local Development
1. Start dependencies: `docker-compose up mysql kafka rabbitmq pulsar`
2. Run services individually with IDE or `./gradlew :service-name:bootRun`
3. Access APIs: order-service:8080, analytics-service:8083

### Production-like Testing
1. Build all: `./gradlew build`
2. Run full stack: `docker-compose up --build`
3. Monitor with Grafana: http://localhost:3000 (admin/admin)

### Broker Switching
- Order service supports profiles: `kafka`, `rabbitmq`, `pulsar`
- Change via `SPRING_PROFILES_ACTIVE` environment variable
- Each consumer service is tied to specific broker for comparison

## Message Flow Architecture

1. **Order Creation**: POST /api/orders → order-service
2. **Event Publishing**: order-service → publishes to order-events topic/exchange
3. **Parallel Processing**:
   - inventory-service (Kafka) → stock reservation
   - notification-service (RabbitMQ) → email/push notifications  
   - analytics-service (Pulsar) → real-time statistics aggregation
4. **Monitoring**: All services expose metrics via /actuator/prometheus

## Testing Strategy

- **Unit Tests**: Each module has isolated unit tests
- **Integration Tests**: Test binders available for all message brokers
- **Load Testing**: Docker Compose setup supports scaling consumers
- **Broker Comparison**: Switch order-service profiles to test different brokers