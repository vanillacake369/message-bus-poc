# Overview

This project is a Proof of Concept (PoC) for Kafka, RabbitMQ, and Pulsar. The implementations provided are not perfect and may lack complete understanding, so please use the code only as a reference.

# Project Background

The author aimed to evaluate message brokers based on the following criteria:

* Spring ecosystem friendliness
* Support for topic-based event publication
* Support for consumer group-based consumption
* Cloud support or requirement for a standalone server
* Ability to scale out horizontally
* Size and activity of the user community
* Support for debounce and throttling mechanisms
* Configurable dead-letter queue policies
* Support for consumer priority reads
* Guarantees on order of message acknowledgement
* Visibility into internal broker events
* Support for CDC plugins (e.g., Debezium)
* Estimated monthly cost on AWS for 200,000 TPS
* Estimated monthly cost on Oracle Cloud for 200,000 TPS
* Estimated monthly cost for on-premises setup at 200,000 TPS
* Overall average monthly cost for 200,000 TPS

Despite extensive research, most resources simply recommend Kafka for its comprehensive feature set. However, cost considerations are significant: paying \$8,500/month for Kafka versus \$950/month for RabbitMQ represents a substantial difference. To determine the optimal choice, a hands-on PoC was undertaken.

# PoC: Flash Sale Order Event Processing

## 1. Functional Requirements

* **Order Event Publication**: Publish JSON messages to the `order-events` topic/exchange upon order creation.
* **Per-Customer Ordering**: Ensure messages for the same `customerId` are processed in sequence.
* **Inventory Update**: Inventory Service consumes messages to decrement stock and handle out-of-stock scenarios.
* **Notification Delivery**: Invoke email and push notification services upon order completion.
* **Real-Time Analytics**: Analytics Service aggregates incoming events into real-time order statistics.
* **Fault Recovery**: Guarantee no message loss and support retrying after broker failures.

## 2. Non-Functional Requirements

* **Throughput**: Peak of 5,000 TPS; average of 1,000 TPS.
* **Latency**: End-to-end latency under 200 ms.
* **Availability**: 99.9% uptime.
* **Fault Tolerance**: Broker cluster tolerates one node failure.
* **Scalability**: Horizontal scaling of consumer instances from 1 to 5.
* **Monitoring & Logging**: Track consumer lag, throughput, latency, and error rates.

## 3. Flow Chart (Mermaid)

```mermaid
flowchart TB
  %% Producer → Broker → Each Consumer Group
  subgraph Producer["Order Service"]
    direction TB
    P[Order Creation]
    P -->|orderEvent| Broker[Message Broker Cluster]
  end

  subgraph Broker["Broker Cluster"]
    direction LR
    P0[Partition 0]
    P1[Partition 1]
    P2[Partition 2]
    P3[Partition 3]
    Broker --> P0
    Broker --> P1
    Broker --> P2
    Broker --> P3
  end

  %% Consumer Groups
  Broker --> InventoryGroup["Inventory Group"]
  Broker --> NotificationGroup["Notification Group"]
  Broker --> AnalyticsGroup["Analytics Group"]

  InventoryGroup --> InvSvc[Inventory Service]
  NotificationGroup --> NotifSvc[Notification Service]
  AnalyticsGroup --> AnalSvc[Analytics Service]
```

## 4. High-Level Component Diagram (Mermaid)

```mermaid
flowchart LR
  %% Publisher / Broker / Subscribers / Storage
  subgraph Publisher["Publisher"]
    direction TB
    OrderSvc[["Order Service"]]
  end

  subgraph BrokerCluster["Message Broker Cluster"]
    Broker[Kafka / Pulsar / RabbitMQ]
  end

  subgraph Subscribers["Subscribers"]
    direction TB
    InventorySvc[["Inventory Service"]]
    NotificationSvc[["Notification Service"]]
    AnalyticsSvc[["Analytics Service"]]
  end

  subgraph Storage["Storage & External APIs"]
    direction TB
    OrderDB[(Orders DB)]
    InvDB[(Inventory DB)]
    Cache[(Redis Cache)]
    EmailAPI(("Email API"))
    PushAPI(("Push API"))
    AnalyticsDB[(Analytics DB)]
  end

  %% Connections
  OrderSvc -- "Publish Event" --> Broker

  Broker -- "Inventory Event" --> InventorySvc
  InventorySvc -- "Update DB" --> InvDB
  InventorySvc -- "Cache Update" --> Cache

  Broker -- "Notification Event" --> NotificationSvc
  NotificationSvc -- "Send Email" --> EmailAPI
  NotificationSvc -- "Send Push" --> PushAPI

  Broker -- "Analytics Event" --> AnalyticsSvc
  AnalyticsSvc -- "Store Metrics" --> AnalyticsDB
```