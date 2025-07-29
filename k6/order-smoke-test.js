import http from 'k6/http';
import { check, sleep } from 'k6';

export const options = {
    thresholds: {
        http_req_duration: ['p(95)<1000'],
        http_req_failed: ['rate<0.05']
    }
};

const BASE_URL = 'http://localhost:8080/api/orders';

export default function () {
    // Create Order
    const orderPayload = {
        customerId: `customer_${__VU}_${__ITER}`,
        productId: 'test_product',
        quantity: 2,
        price: 99.99
    };

    const createResponse = http.post(BASE_URL, JSON.stringify(orderPayload), {
        headers: { 'Content-Type': 'application/json' }
    });

    check(createResponse, {
        'create order success': (r) => r.status === 200
    });
}