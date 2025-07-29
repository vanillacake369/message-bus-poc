package com.poc.order.service;

import com.poc.order.entity.Order;
import com.poc.order.publisher.OrderEventPublisher;
import com.poc.order.repository.OrderRepository;
import com.poc.shared.events.OrderCreatedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderService {
    
    private final OrderRepository orderRepository;
    private final OrderEventPublisher orderEventPublisher;
    
    @Transactional
    public Order createOrder(String customerId, String productId, Integer quantity, 
                           java.math.BigDecimal price) {
        
        String orderId = UUID.randomUUID().toString();
        LocalDateTime now = LocalDateTime.now();
        
        Order order = new Order();
        order.setOrderId(orderId);
        order.setCustomerId(customerId);
        order.setProductId(productId);
        order.setQuantity(quantity);
        order.setPrice(price);
        order.setOrderTimestamp(now);
        order.setStatus(Order.OrderStatus.CREATED);
        
        // Save to database
        Order savedOrder = orderRepository.save(order);
        log.info("Created order: {}", savedOrder.getOrderId());
        
        // Publish event
        OrderCreatedEvent event = new OrderCreatedEvent(
            orderId, customerId, productId, quantity, price, now, "CREATED"
        );
        
        orderEventPublisher.publishOrderCreated(event);
        
        return savedOrder;
    }
    
    public List<Order> getOrdersByCustomer(String customerId) {
        return orderRepository.findByCustomerId(customerId);
    }
    
    public Order getOrder(String orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found: " + orderId));
    }
    
    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }
}