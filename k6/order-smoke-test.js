import http from 'k6/http';
import { check, group, sleep } from 'k6';
import { Rate } from 'k6/metrics';

// Custom metrics
export const errorRate = new Rate('errors');

export const options = {
    stages: [
        {
            duration: '30s',
            target: 5  // Light load for smoke testing
        }
    ],
    thresholds: {
        http_req_duration: ['p(95)<1000'], // More lenient for integration testing
        http_req_failed: ['rate<0.05'],    // Very low error rate expected
        errors: ['rate<0.05']
    }
};

const BASE_URL = 'http://localhost:8080/api/orders';

// Test data
const testOrder = {
    customerId: 'smoke_test_customer_001',
    productId: 'smoke_test_product',
    quantity: 2,
    price: 99.99
};

let createdOrderId;

export default function () {
    group('Order API Integration Tests', function () {
        
        // Test 1: Create Order
        group('Create Order', function () {
            const payload = {
                ...testOrder,
                customerId: `${testOrder.customerId}_${__VU}_${__ITER}`, // Make unique per VU and iteration
            };

            const params = {
                headers: {
                    'Content-Type': 'application/json',
                },
            };

            const response = http.post(BASE_URL, JSON.stringify(payload), params);
            
            const success = check(response, {
                'create order status is 200': (r) => r.status === 200,
                'create order response time < 1000ms': (r) => r.timings.duration < 1000,
                'response has order id': (r) => {
                    try {
                        const body = JSON.parse(r.body);
                        createdOrderId = body.id;
                        return body.id !== undefined && body.id !== null;
                    } catch (e) {
                        return false;
                    }
                },
                'response has correct customer id': (r) => {
                    try {
                        const body = JSON.parse(r.body);
                        return body.customerId === payload.customerId;
                    } catch (e) {
                        return false;
                    }
                },
                'response has correct product id': (r) => {
                    try {
                        const body = JSON.parse(r.body);
                        return body.productId === payload.productId;
                    } catch (e) {
                        return false;
                    }
                },
                'response has correct quantity': (r) => {
                    try {
                        const body = JSON.parse(r.body);
                        return body.quantity === payload.quantity;
                    } catch (e) {
                        return false;
                    }
                }
            });

            if (!success) {
                errorRate.add(1);
            }

            sleep(1);
        });

        // Test 2: Get Specific Order (if creation was successful)
        if (createdOrderId) {
            group('Get Order by ID', function () {
                const response = http.get(`${BASE_URL}/${createdOrderId}`);
                
                const success = check(response, {
                    'get order status is 200': (r) => r.status === 200,
                    'get order response time < 500ms': (r) => r.timings.duration < 500,
                    'get order returns correct id': (r) => {
                        try {
                            const body = JSON.parse(r.body);
                            return body.id === createdOrderId;
                        } catch (e) {
                            return false;
                        }
                    }
                });

                if (!success) {
                    errorRate.add(1);
                }

                sleep(0.5);
            });
        }

        // Test 3: Get All Orders
        group('Get All Orders', function () {
            const response = http.get(BASE_URL);
            
            const success = check(response, {
                'get all orders status is 200': (r) => r.status === 200,
                'get all orders response time < 1000ms': (r) => r.timings.duration < 1000,
                'get all orders returns array': (r) => {
                    try {
                        const body = JSON.parse(r.body);
                        return Array.isArray(body);
                    } catch (e) {
                        return false;
                    }
                }
            });

            if (!success) {
                errorRate.add(1);
            }

            sleep(0.5);
        });

        // Test 4: Get Orders by Customer
        group('Get Orders by Customer', function () {
            const customerId = `${testOrder.customerId}_${__VU}_${__ITER}`;
            const response = http.get(`${BASE_URL}/customer/${customerId}`);
            
            const success = check(response, {
                'get orders by customer status is 200': (r) => r.status === 200,
                'get orders by customer response time < 1000ms': (r) => r.timings.duration < 1000,
                'get orders by customer returns array': (r) => {
                    try {
                        const body = JSON.parse(r.body);
                        return Array.isArray(body);
                    } catch (e) {
                        return false;
                    }
                },
                'customer orders contain correct customer id': (r) => {
                    try {
                        const body = JSON.parse(r.body);
                        return body.length === 0 || body.every(order => order.customerId === customerId);
                    } catch (e) {
                        return false;
                    }
                }
            });

            if (!success) {
                errorRate.add(1);
            }

            sleep(0.5);
        });

        // Test 5: Error Handling - Invalid Order Creation
        group('Error Handling Tests', function () {
            const invalidPayload = {
                customerId: null,
                productId: "",
                quantity: -1,
                price: "invalid"
            };

            const params = {
                headers: {
                    'Content-Type': 'application/json',
                },
            };

            const response = http.post(BASE_URL, JSON.stringify(invalidPayload), params);
            
            check(response, {
                'invalid order returns 4xx status': (r) => r.status >= 400 && r.status < 500,
                'invalid order response time < 1000ms': (r) => r.timings.duration < 1000
            });

            sleep(0.5);
        });

        // Test 6: Non-existent Order
        group('Get Non-existent Order', function () {
            const response = http.get(`${BASE_URL}/non-existent-order-id`);
            
            check(response, {
                'non-existent order returns 404 or handles gracefully': (r) => 
                    r.status === 404 || r.status === 200, // Depending on implementation
                'non-existent order response time < 500ms': (r) => r.timings.duration < 500
            });

            sleep(0.5);
        });
    });
}

export function handleSummary(data) {
    return {
        'summary.json': JSON.stringify(data, null, 2),
        stdout: `
        ========================================
        Order Service Integration Test Summary
        ========================================
        Total Requests: ${data.metrics.http_reqs.values.count}
        Failed Requests: ${data.metrics.http_req_failed.values.count}
        Average Response Time: ${data.metrics.http_req_duration.values.avg.toFixed(2)}ms
        95th Percentile: ${data.metrics.http_req_duration.values['p(95)'].toFixed(2)}ms
        Error Rate: ${(data.metrics.errors ? data.metrics.errors.values.rate * 100 : 0).toFixed(2)}%
        ========================================
        `
    };
}