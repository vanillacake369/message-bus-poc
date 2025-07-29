import http from 'k6/http';
import { check, sleep } from 'k6';

export const options = {
    stages: [
        { duration: '5m', target: 10000 },  // Ramp up to 10,000 VUs
        { duration: '10m', target: 10000 }, // Sustain 10,000 VUs
        { duration: '2m', target: 0 }       // Ramp down to 0
    ],
    thresholds: {
        http_req_duration: ['p(95)<2000'], // More lenient for high load
        http_req_failed: ['rate<0.1']      // 10% error rate acceptable under load
    }
};

const BASE_URL = 'http://localhost:8080/api/orders';

// Simple random data generators
function randomCustomerId() {
    return `customer_${Math.floor(Math.random() * 10000)}`;
}

function randomProductId() {
    const products = ['laptop', 'phone', 'tablet', 'headphones', 'watch'];
    return products[Math.floor(Math.random() * products.length)];
}

function randomQuantity() {
    return Math.floor(Math.random() * 5) + 1;
}

function randomPrice() {
    return parseFloat((Math.random() * 1000 + 10).toFixed(2));
}

export default function () {
    // Create Order with random data
    const orderPayload = {
        customerId: randomCustomerId(),
        productId: randomProductId(),
        quantity: randomQuantity(),
        price: randomPrice()
    };

    const createResponse = http.post(BASE_URL, JSON.stringify(orderPayload), {
        headers: { 'Content-Type': 'application/json' }
    });

    check(createResponse, {
        'create order success': (r) => r.status === 200
    });

    // Occasionally test read endpoints (5% of requests)
    if (Math.random() < 0.05) {
        const getAllResponse = http.get(BASE_URL);
        check(getAllResponse, {
            'get all orders success': (r) => r.status === 200
        });
    }

    sleep(0.1);
}