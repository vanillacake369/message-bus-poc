package com.poc.shared.events;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AnalyticsEvent {
    
    @NotNull
    private String eventId;
    
    @NotNull
    private String eventType; // ORDER_CREATED, INVENTORY_UPDATED, NOTIFICATION_SENT
    
    @NotNull
    private String orderId;
    
    private String customerId;
    
    private String productId;
    
    private BigDecimal orderValue;
    
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime eventTimestamp;
    
    private Map<String, Object> additionalData; // flexible data for different event types
}