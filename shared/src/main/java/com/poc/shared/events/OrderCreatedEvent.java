package com.poc.shared.events;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderCreatedEvent {
    
    @NotNull
    private String orderId;
    
    @NotNull
    private String customerId;
    
    @NotNull
    private String productId;
    
    @Positive
    private Integer quantity;
    
    @Positive
    private BigDecimal price;
    
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime orderTimestamp;
    
    @NotNull
    private String status; // CREATED, CONFIRMED, CANCELLED
}