# Message Bus PoC - Makefile
# Usage: make <target> [args]

.PHONY: help module modules all-build all-test all-up all-down all-clean all-logs \
        verification-debounce verification-dlq verification-priority verification-ordering verification-monitoring verification-cdc \
        health logs scale infra load-test-basic load-test-debounce load-test-throttle load-test-priority

# Default target
help:
	@echo "Message Bus PoC - Available Commands:"
	@echo ""
	@echo "Module Operations:"
	@echo "  make module name=<service>           - Build, test, restart single module"
	@echo "  make modules names=\"<svc1> <svc2>\"   - Build, test, restart multiple modules"
	@echo "  Available modules: order, inventory, notification, analytics, shared"
	@echo ""
	@echo "All Operations:"
	@echo "  make all-build                       - Build all modules"
	@echo "  make all-test                        - Test all modules"
	@echo "  make all-up                          - Start full stack"
	@echo "  make all-down                        - Stop all services"
	@echo "  make all-clean                       - Clean rebuild everything"
	@echo "  make all-logs                        - View all logs"
	@echo ""
	@echo "Verification:"
	@echo "  make verification-<test>             - Run verification tests"
	@echo "  Tests: debounce, dlq, priority, ordering, monitoring, cdc"
	@echo ""
	@echo "Utilities:"
	@echo "  make health                          - Check service health"
	@echo "  make logs service=<name>            - View service logs"
	@echo "  make scale service=<name> count=<n> - Scale service"
	@echo "  make infra                          - Start infrastructure only"
	@echo "  make load-test-<scenario>           - Run load tests"

# Module operations
module:
ifndef name
	$(error Usage: make module name=<service>. Available: order, inventory, notification, analytics, shared)
endif
	@echo "Processing module: $(name)"
	@if [ "$(name)" = "shared" ]; then \
		cd .. && ./gradlew :shared:build && \
		./gradlew :shared:test && \
		echo "Shared module built and tested (no service restart)"; \
	else \
		cd .. && ./gradlew :$(name)-service:build && \
		./gradlew :$(name)-service:test && \
		docker-compose up -d --no-deps --build $(name)-service && \
		echo "Module $(name) built, tested, and restarted"; \
	fi

modules:
ifndef names
	$(error Usage: make modules names="order inventory". Available: order, inventory, notification, analytics, shared)
endif
	@echo "Processing modules: $(names)"
	@services=""; \
	for name in $(names); do \
		echo "Building module: $$name"; \
		if [ "$$name" = "shared" ]; then \
			cd .. && ./gradlew :shared:build && ./gradlew :shared:test; \
		else \
			cd .. && ./gradlew :$$name-service:build && ./gradlew :$$name-service:test; \
			services="$$services $$name-service"; \
		fi; \
	done; \
	if [ -n "$$services" ]; then \
		echo "Restarting services:$$services"; \
		docker-compose up -d --no-deps --build $$services; \
	fi

# All operations
all-build:
	@echo "Building all modules..."
	cd .. && ./gradlew build

all-test:
	@echo "Testing all modules..."
	cd .. && ./gradlew test

all-up:
	@echo "Starting full stack..."
	cd .. && ./gradlew build
	docker-compose up -d --build

all-down:
	@echo "Stopping all services..."
	docker-compose down

all-clean:
	@echo "Clean building all modules..."
	docker-compose down
	cd .. && ./gradlew clean build
	docker-compose up -d --build

all-logs:
	@echo "Showing all service logs..."
	docker-compose logs -f

# Verification tests
verification-debounce:
	@echo "Running debounce/throttling verification..."
	cd .. && ./gradlew :verification-tests:test --tests "*DebounceTest*"

verification-dlq:
	@echo "Running dead letter queue verification..."
	../scripts/test-dlq.sh kafka
	../scripts/test-dlq.sh rabbitmq
	../scripts/test-dlq.sh pulsar

verification-priority:
	@echo "Running consumer priority verification..."
	cd .. && ./gradlew :verification-tests:test --tests "*PriorityTest*"

verification-ordering:
	@echo "Running message ordering verification..."
	cd .. && ./gradlew :verification-tests:test --tests "*OrderingTest*"

verification-monitoring:
	@echo "Running broker monitoring verification..."
	../scripts/collect-metrics.sh

verification-cdc:
	@echo "Running change data capture verification..."
	cd .. && ./gradlew :verification-tests:test --tests "*CdcTest*"

# Utility commands
health:
	@echo "Checking service health..."
	docker-compose ps
	@echo ""
	@echo "Service endpoints:"
	@curl -s http://localhost:8080/actuator/health | jq '.' || echo "order-service: DOWN"
	@curl -s http://localhost:8081/actuator/health | jq '.' || echo "inventory-service: DOWN"
	@curl -s http://localhost:8082/actuator/health | jq '.' || echo "notification-service: DOWN"
	@curl -s http://localhost:8083/actuator/health | jq '.' || echo "analytics-service: DOWN"

logs:
ifndef service
	$(error Usage: make logs service=<name>)
endif
	docker-compose logs -f $(service)-service

scale:
ifndef service
	$(error Usage: make scale service=<name> count=<number>)
endif
ifndef count
	$(error Usage: make scale service=<name> count=<number>)
endif
	docker-compose up -d --scale $(service)-service=$(count)

infra:
	@echo "Starting infrastructure services only..."
	docker-compose up -d mysql kafka rabbitmq pulsar prometheus grafana
	@echo "Infrastructure ready for local development"

# Load testing
load-test-basic:
	k6 run k6/basic-load-test.js

load-test-debounce:
	k6 run k6/debounce-test.js

load-test-throttle:
	k6 run k6/throttle-test.js

load-test-priority:
	k6 run k6/priority-test.js