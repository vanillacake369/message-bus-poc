import http from 'k6/http';
import { check, sleep } from 'k6';
import { Rate } from 'k6/metrics';

// Custom metrics
export const errorRate = new Rate('errors');

export const options = {
    stages: [
        // Ramp up to 10,000 TPS over 2 minutes
        {
            duration: '2m',
            target: 10000
        },
        // Sustain 10,000 TPS for 5 minutes
        {
            duration: '5m',
            target: 10000
        },
        // Ramp down to 0 over 1 minute
        {
            duration: '1m',
            target: 0
        }
    ],
    thresholds: {
        http_req_duration: ['p(95)<500'], // 95% of requests must complete below 500ms
        http_req_failed: ['rate<0.1'],    // Error rate must be below 10%
        errors: ['rate<0.1']
    }
};

// Test data generators
function randomCustomerId() {
    return `customer_${Math.floor(Math.random() * 10000)}`;
}

function randomProductId() {
    const products = ['laptop', 'phone', 'tablet', 'headphones', 'watch'];
    return products[Math.floor(Math.random() * products.length)];
}

function randomQuantity() {
    return Math.floor(Math.random() * 5) + 1; // 1-5 items
}

function randomPrice() {
    return (Math.random() * 1000 + 10).toFixed(2); // $10-$1010
}

export default function () {
    const orderPayload = {
        customerId: randomCustomerId(),
        productId: randomProductId(),
        quantity: randomQuantity(),
        price: parseFloat(randomPrice())
    };

    const params = {
        headers: {
            'Content-Type': 'application/json',
        },
    };

    // Create order (main load test endpoint)
    const createResponse = http.post(
        'http://localhost:8080/api/orders',
        JSON.stringify(orderPayload),
        params
    );

    const createSuccess = check(createResponse, {
        'order creation status is 200': (r) => r.status === 200,
        'order creation response time < 500ms': (r) => r.timings.duration < 500,
        'order has id': (r) => JSON.parse(r.body).id !== undefined,
    });

    if (!createSuccess) {
        errorRate.add(1);
    }

    // Occasionally test GET endpoints (10% of requests)
    if (Math.random() < 0.1) {
        const getAllResponse = http.get('http://localhost:8080/api/orders');
        
        check(getAllResponse, {
            'get all orders status is 200': (r) => r.status === 200,
        });
    }

    // Small sleep to prevent overwhelming the system
    sleep(0.1);
}