package com.poc.inventory.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "inventory")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Inventory {
    
    @Id
    private String productId;
    
    @Column(nullable = false)
    private Integer availableQuantity;
    
    @Column(nullable = false)
    private Integer reservedQuantity;
    
    @Column(nullable = false)
    private LocalDateTime lastUpdated;
    
    @Version
    private Long version; // For optimistic locking
    
    public boolean canReserve(Integer requestedQuantity) {
        return availableQuantity >= requestedQuantity;
    }
    
    public void reserveQuantity(Integer quantity) {
        if (!canReserve(quantity)) {
            throw new IllegalStateException("Insufficient inventory for product: " + productId);
        }
        this.availableQuantity -= quantity;
        this.reservedQuantity += quantity;
        this.lastUpdated = LocalDateTime.now();
    }
}