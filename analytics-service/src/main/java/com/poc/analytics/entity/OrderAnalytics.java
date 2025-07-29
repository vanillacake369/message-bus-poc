package com.poc.analytics.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "order_analytics")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderAnalytics {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String orderId;
    
    @Column(nullable = false)
    private String customerId;
    
    @Column(nullable = false)
    private String productId;
    
    @Column(nullable = false)
    private Integer quantity;
    
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal orderValue;
    
    @Column(nullable = false)
    private LocalDateTime orderTimestamp;
    
    @Column(nullable = false)
    private LocalDateTime processedTimestamp;
    
    // Aggregation fields for quick queries
    @Column(name = "hour_bucket")
    private String hourBucket; // e.g., "2024-01-15-14" for 2 PM
    
    @Column(name = "day_bucket")
    private String dayBucket; // e.g., "2024-01-15"
}