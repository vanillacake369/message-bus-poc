package com.poc.shared.events;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InventoryUpdateEvent {
    
    @NotNull
    private String orderId;
    
    @NotNull
    private String productId;
    
    @NotNull
    private Integer requestedQuantity;
    
    @NotNull
    private Integer availableQuantity;
    
    @NotNull
    private Boolean inventoryReserved;
    
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime processedTimestamp;
    
    private String failureReason; // null if successful, reason if failed
}