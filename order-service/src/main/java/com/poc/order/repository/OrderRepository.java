package com.poc.order.repository;

import com.poc.order.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, String> {
    
    List<Order> findByCustomerId(String customerId);
    
    List<Order> findByProductId(String productId);
    
    List<Order> findByStatus(Order.OrderStatus status);
}