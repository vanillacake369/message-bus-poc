# Message Bus PoC - Multi-Broker Comparison

This project is a Proof of Concept (PoC) comparing message brokers (Kafka, RabbitMQ, and Pulsar) in a Spring Boot microservices architecture. The implementations simulate a flash sale order event processing system to evaluate message broker performance, cost, and features across domain-based services.

> 🚀 **Quick Start**: Use `just` or `make` scripts for convenient local development - see [script/justfile](script/justfile) and [script/makefile](script/makefile)

## Project Overview

This is a multi-module Spring Boot application designed to evaluate message brokers based on real-world scenarios. Each consumer service is dedicated to a specific broker for direct comparison:

- **Kafka**: High-throughput inventory management with optimistic locking
- **RabbitMQ**: Reliable notification delivery (email/push)
- **Pulsar**: Real-time analytics and metrics aggregation

## Architecture

### Multi-Module Structure
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

### Service Responsibilities
- **shared**: Common event DTOs, configurations, serialization utilities
- **order-service**: REST API for order creation, publishes to all 3 brokers simultaneously
- **inventory-service**: Kafka consumer for stock management with database transactions
- **notification-service**: RabbitMQ consumer for email/push notification delivery
- **analytics-service**: Pulsar consumer for real-time analytics with REST API endpoints

## Quick Start

### Prerequisites
- Java 17+
- Docker & Docker Compose
- Gradle 8.x
- **Optional**: [just](https://github.com/casey/just) or make for convenient local scripts

### Running the Application

#### Option 1: Using Just (Recommended)
```bash
# Start infrastructure only (for local development)
just infra

# Build and start full stack
just all up

# Build, test, and restart specific module
just module order

# Build, test, and restart multiple modules
just modules order inventory

# Check service health
just health

# View available commands
just help
```

#### Option 2: Using Make
```bash
# Start infrastructure only
make infra

# Build and start full stack  
make all-up

# Build, test, and restart specific module
make module name=order

# Build, test, and restart multiple modules
make modules names="order inventory"

# Check service health
make health

# View available commands
make help
```

#### Option 3: Traditional Commands

1. **Start infrastructure services**:
   ```bash
   docker-compose up mysql kafka rabbitmq pulsar prometheus grafana
   ```

2. **Build all modules**:
   ```bash
   ./gradlew build
   ```

3. **Run services individually** (for development):
   ```bash
   ./gradlew :order-service:bootRun
   ./gradlew :inventory-service:bootRun
   ./gradlew :notification-service:bootRun
   ./gradlew :analytics-service:bootRun
   ```

4. **Or run full stack with Docker**:
   ```bash
   docker-compose up --build
   ```

### Service Endpoints
- **Order Service**: http://localhost:8080 (REST API for order creation)
- **Analytics Service**: http://localhost:8083 (Analytics REST API)
- **Grafana Dashboard**: http://localhost:3000 (admin/admin)
- **RabbitMQ Management**: http://localhost:15672 (admin/admin)
- **Prometheus**: http://localhost:9090

## Load Testing

### Using Local Scripts (Recommended)

#### Just Commands
```bash
# Basic load test
just load-test basic

# Specific verification scenarios
just load-test debounce
just load-test throttle  
just load-test priority
```

#### Make Commands
```bash
# Load testing scenarios
make load-test-basic
make load-test-debounce
make load-test-throttle
make load-test-priority
```

### Traditional K6 Commands
K6 load tests are included in the `k6/` directory:

```bash
# Smoke test
k6 run k6/order-smoke-test.js

# Load test
k6 run k6/order-load-test.js
```

## Requirements & Specifications

### Functional Requirements
* **Order Event Publication**: Publish JSON messages to broker-specific topics/exchanges upon order creation
* **Per-Customer Ordering**: Ensure messages for the same `customerId` are processed in sequence
* **Inventory Update**: Kafka-based inventory service decrements stock with optimistic locking
* **Notification Delivery**: RabbitMQ-based service handles email and push notifications
* **Real-Time Analytics**: Pulsar-based service aggregates events into real-time statistics
* **Fault Recovery**: Guarantee no message loss with broker-specific retry mechanisms

### Non-Functional Requirements
* **Throughput**: Peak of 5,000 TPS; average of 1,000 TPS
* **Latency**: End-to-end latency under 200 ms
* **Availability**: 99.9% uptime with broker clustering
* **Fault Tolerance**: Each broker cluster tolerates node failures
* **Scalability**: Horizontal scaling support for consumer instances
* **Monitoring**: Comprehensive metrics via Prometheus/Grafana

## Architecture Diagrams

### Message Flow Architecture
```mermaid
flowchart TB
  %% Order Service publishes to all brokers
  subgraph Publisher["Order Service (Port 8080)"]
    direction TB
    P[Order Creation REST API]
    P -->|Publishes simultaneously| AllBrokers[All Message Brokers]
  end

  %% Each broker handles specific consumer
  subgraph Brokers["Message Brokers"]
    direction LR
    Kafka[Kafka:9092<br/>High Throughput]
    RabbitMQ[RabbitMQ:5672<br/>Reliable Delivery]
    Pulsar[Pulsar:6650<br/>Real-time Analytics]
  end

  %% Dedicated consumers per broker
  AllBrokers --> Kafka
  AllBrokers --> RabbitMQ
  AllBrokers --> Pulsar

  Kafka --> InvSvc[Inventory Service<br/>Port 8081<br/>Stock Management]
  RabbitMQ --> NotifSvc[Notification Service<br/>Port 8082<br/>Email/Push]
  Pulsar --> AnalSvc[Analytics Service<br/>Port 8083<br/>Real-time Metrics]
```

### System Component Architecture
```mermaid
flowchart LR
  %% Publisher
  subgraph Publisher["Publisher Layer"]
    OrderSvc[["Order Service<br/>(REST API)"]]
  end

  %% Message Brokers
  subgraph BrokerLayer["Message Broker Layer"]
    direction TB
    Kafka[Kafka<br/>Inventory Events]
    RabbitMQ[RabbitMQ<br/>Notification Events]
    Pulsar[Pulsar<br/>Analytics Events]
  end

  %% Consumers
  subgraph Consumers["Consumer Services"]
    direction TB
    InventorySvc[["Inventory Service<br/>(Kafka Consumer)"]]
    NotificationSvc[["Notification Service<br/>(RabbitMQ Consumer)"]]
    AnalyticsSvc[["Analytics Service<br/>(Pulsar Consumer)"]]
  end

  %% Storage & External
  subgraph Storage["Storage & External APIs"]
    direction TB
    MySQL[(MySQL<br/>Multi-DB)]
    EmailAPI(("Email API"))
    PushAPI(("Push API"))
  end

  %% Monitoring
  subgraph Monitoring["Monitoring Stack"]
    direction TB
    Prometheus[Prometheus<br/>Metrics Collection]
    Grafana[Grafana<br/>Dashboards]
  end

  %% Connections
  OrderSvc --> Kafka
  OrderSvc --> RabbitMQ
  OrderSvc --> Pulsar

  Kafka --> InventorySvc
  RabbitMQ --> NotificationSvc
  Pulsar --> AnalyticsSvc

  InventorySvc --> MySQL
  NotificationSvc --> EmailAPI
  NotificationSvc --> PushAPI
  AnalyticsSvc --> MySQL

  InventorySvc --> Prometheus
  NotificationSvc --> Prometheus
  AnalyticsSvc --> Prometheus
  OrderSvc --> Prometheus
  Prometheus --> Grafana
```

## Technology Stack

### Core Technologies
- **Java 17** with Spring Boot 3.5.4
- **Spring Cloud Stream** for message broker abstraction
- **Spring Data JPA** for database operations
- **MySQL 8.0** for persistent storage
- **Lombok** for boilerplate reduction

### Message Brokers
- **Apache Kafka 7.4.0** - High-throughput streaming
- **RabbitMQ 3.12** - Reliable message queuing
- **Apache Pulsar 3.1.0** - Real-time analytics streaming

### Infrastructure & Monitoring
- **Docker Compose** for multi-service orchestration
- **Prometheus** for metrics collection
- **Grafana** for monitoring dashboards
- **K6** for load testing

## Development & Testing

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

### Multi-Module Build Commands
```bash
# Build all modules
./gradlew build

# Build specific module
./gradlew :order-service:build
./gradlew :inventory-service:build
./gradlew :notification-service:build
./gradlew :analytics-service:build

# Run tests
./gradlew test

# Test specific module
./gradlew :shared:test
./gradlew :order-service:test

# Clean build
./gradlew clean
```

### Docker Development Options
```bash
# Full stack with all services
docker-compose up --build

# Infrastructure only (for local development)
docker-compose up mysql kafka rabbitmq pulsar prometheus grafana

# Start services in background
docker-compose up -d --build

# Scale specific consumers for load testing
docker-compose up --scale inventory-service=3 --scale notification-service=2

# Stop specific service
docker-compose stop order-service

# Remove containers and networks
docker-compose down
```

## Verification Plan

A systematic approach to evaluate message broker capabilities beyond surface-level implementations. Each broker is tested across 6 critical areas to provide meaningful performance and cost-effectiveness comparisons.

### Verification Criteria

| Criterion | Description | Business Impact |
|-----------|-------------|-----------------|
| **Spring Integration** | Native Spring Boot/Cloud Stream support | Development velocity & maintainability |
| **Rate Control** | Debounce & throttling capabilities | Resource optimization & duplicate handling |
| **Fault Tolerance** | Dead Letter Queue policy handling | Reliability & error recovery |
| **Priority Processing** | Consumer priority reading support | Business-critical message prioritization |
| **Ordering Guarantees** | Sequential processing completion | Data consistency & business logic integrity |
| **Observability** | Internal event verification & monitoring | Operational visibility & debugging |
| **Data Integration** | CDC plugin support | Real-time data synchronization |

### Test Scenarios

#### 1. Debouncing & Throttling
```mermaid
flowchart LR
  LoadGen[K6 Load Generator] -->|1000 msg/s| Broker
  Broker --> Consumer
  Consumer -->|Apply rate limits| Metrics[Prometheus Metrics]
```

**Test Cases**:
- **Debounce**: 10 identical messages (same customerId) within 1s → expect 1 processed
- **Throttle**: 1000 msg/s load → verify consumer rate limiting

**Implementation**:
- Kafka: `max.poll.records` + `pause()/resume()`
- RabbitMQ: `basicQos(prefetchCount)`
- Pulsar: `receiverQueueSize`

#### 2. Dead Letter Queue Policy
```mermaid
flowchart TB
  Producer --> Broker
  Broker --> Consumer
  Consumer -->|Exception| RetryMechanism[Retry Mechanism]
  RetryMechanism -->|Max retries exceeded| DLQ[Dead Letter Queue]
```

**Test Case**: Consumer throws exception → message routes to DLQ after retry limit

**Verification**:
- Kafka: `DeadLetterPublishingRecoverer`
- RabbitMQ: Dead Letter Exchange with `x-dead-letter-exchange`
- Pulsar: `deadLetterPolicy` configuration

#### 3. Consumer Priority & Ordering
**Priority Test**: 2 consumers (C1: priority 10, C2: priority 1) → C1 receives majority
**Ordering Test**: Sequential messages (1-10) for same key → processed in order

#### 4. Observability & CDC Integration
**Metrics Collection**: Query broker APIs for lag, queue depth, consumer offsets
**CDC Pipeline**: Debezium MySQL → Broker → verify change events

### Performance Benchmarks

| Broker | Throughput (TPS) | Latency (p99) | Memory (GB) | Spring Integration |
|--------|------------------|---------------|-------------|--------------------|
| Kafka | Target: 5000 | <50ms | TBD | Native |
| RabbitMQ | Target: 3000 | <100ms | TBD | Native |
| Pulsar | Target: 4000 | <75ms | TBD | Community |

### Execution Commands

#### Using Local Scripts (Recommended)

**Just Commands:**
```bash
# Verification tests
just verification debounce    # Debounce/throttling test
just verification dlq         # Dead letter queue test  
just verification priority    # Consumer priority test
just verification ordering    # Message ordering test
just verification monitoring  # Broker monitoring test
just verification cdc         # Change data capture test

# Load testing scenarios  
just load-test basic
just load-test debounce
just load-test throttle
just load-test priority
```

**Make Commands:**
```bash
# Verification tests
make verification-debounce    # Debounce/throttling test
make verification-dlq         # Dead letter queue test
make verification-priority    # Consumer priority test
make verification-ordering    # Message ordering test  
make verification-monitoring  # Broker monitoring test
make verification-cdc         # Change data capture test

# Load testing scenarios
make load-test-basic
make load-test-debounce
make load-test-throttle
make load-test-priority
```

#### Traditional Commands
```bash
# Full verification suite
./gradlew :verification-tests:test

# Specific scenario testing
k6 run k6/debounce-test.js
k6 run k6/throttle-test.js
k6 run k6/priority-test.js

# DLQ verification per broker (when scripts implemented)
# ../scripts/test-dlq.sh kafka
# ../scripts/test-dlq.sh rabbitmq  
# ../scripts/test-dlq.sh pulsar

# CDC integration testing (when scripts implemented)
# ../scripts/test-cdc.sh

# Metrics collection (when scripts implemented)
# ../scripts/collect-metrics.sh
```