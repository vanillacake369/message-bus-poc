package com.poc.analytics.controller;

import com.poc.analytics.entity.OrderAnalytics;
import com.poc.analytics.service.AnalyticsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/analytics")
@RequiredArgsConstructor
public class AnalyticsController {
    
    private final AnalyticsService analyticsService;
    
    @GetMapping("/realtime")
    public ResponseEntity<Map<String, Object>> getRealTimeStats() {
        log.info("Fetching real-time analytics stats");
        Map<String, Object> stats = analyticsService.getRealTimeStats();
        return ResponseEntity.ok(stats);
    }
    
    @GetMapping("/top-products")
    public ResponseEntity<List<Object[]>> getTopProductsToday() {
        log.info("Fetching top products for today");
        List<Object[]> topProducts = analyticsService.getTopProductsToday();
        return ResponseEntity.ok(topProducts);
    }
    
    @GetMapping("/hourly-stats")
    public ResponseEntity<List<Object[]>> getHourlyStatsToday() {
        log.info("Fetching hourly stats for today");
        List<Object[]> hourlyStats = analyticsService.getHourlyStatsToday();
        return ResponseEntity.ok(hourlyStats);
    }
    
    @GetMapping("/customer/{customerId}")
    public ResponseEntity<List<OrderAnalytics>> getCustomerOrders(@PathVariable String customerId) {
        log.info("Fetching orders for customer: {}", customerId);
        List<OrderAnalytics> customerOrders = analyticsService.getCustomerOrders(customerId);
        return ResponseEntity.ok(customerOrders);
    }
}