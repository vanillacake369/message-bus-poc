package com.poc.analytics.service;

import com.poc.analytics.entity.OrderAnalytics;
import com.poc.analytics.repository.OrderAnalyticsRepository;
import com.poc.shared.events.OrderCreatedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class AnalyticsService {
    
    private final OrderAnalyticsRepository analyticsRepository;
    
    private static final DateTimeFormatter HOUR_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd-HH");
    private static final DateTimeFormatter DAY_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    
    @Transactional
    public void processOrderEvent(OrderCreatedEvent orderEvent) {
        log.info("Processing analytics for order: {}", orderEvent.getOrderId());
        
        try {
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime orderTime = orderEvent.getOrderTimestamp();
            
            OrderAnalytics analytics = new OrderAnalytics();
            analytics.setOrderId(orderEvent.getOrderId());
            analytics.setCustomerId(orderEvent.getCustomerId());
            analytics.setProductId(orderEvent.getProductId());
            analytics.setQuantity(orderEvent.getQuantity());
            analytics.setOrderValue(orderEvent.getPrice().multiply(BigDecimal.valueOf(orderEvent.getQuantity())));
            analytics.setOrderTimestamp(orderTime);
            analytics.setProcessedTimestamp(now);
            analytics.setHourBucket(orderTime.format(HOUR_FORMATTER));
            analytics.setDayBucket(orderTime.format(DAY_FORMATTER));
            
            analyticsRepository.save(analytics);
            
            log.info("Successfully processed analytics for order: {}", orderEvent.getOrderId());
            
        } catch (Exception e) {
            log.error("Failed to process analytics for order: {}", orderEvent.getOrderId(), e);
            throw e;
        }
    }
    
    public Map<String, Object> getRealTimeStats() {
        LocalDateTime oneHourAgo = LocalDateTime.now().minusHours(1);
        LocalDateTime oneDayAgo = LocalDateTime.now().minusDays(1);
        
        Map<String, Object> stats = new HashMap<>();
        
        // Last hour stats
        Long ordersLastHour = analyticsRepository.countOrdersSince(oneHourAgo);
        BigDecimal revenueLastHour = analyticsRepository.sumOrderValueSince(oneHourAgo);
        
        // Last day stats
        Long ordersLastDay = analyticsRepository.countOrdersSince(oneDayAgo);
        BigDecimal revenueLastDay = analyticsRepository.sumOrderValueSince(oneDayAgo);
        
        stats.put("ordersLastHour", ordersLastHour);
        stats.put("revenueLastHour", revenueLastHour != null ? revenueLastHour : BigDecimal.ZERO);
        stats.put("ordersLastDay", ordersLastDay);
        stats.put("revenueLastDay", revenueLastDay != null ? revenueLastDay : BigDecimal.ZERO);
        stats.put("timestamp", LocalDateTime.now());
        
        return stats;
    }
    
    public List<Object[]> getTopProductsToday() {
        String today = LocalDateTime.now().format(DAY_FORMATTER);
        return analyticsRepository.getTopProductsByDay(today);
    }
    
    public List<Object[]> getHourlyStatsToday() {
        String today = LocalDateTime.now().format(DAY_FORMATTER);
        return analyticsRepository.getHourlyStatsByDay(today);
    }
    
    public List<OrderAnalytics> getCustomerOrders(String customerId) {
        return analyticsRepository.findByCustomerId(customerId);
    }
}