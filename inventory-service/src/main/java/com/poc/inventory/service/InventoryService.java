package com.poc.inventory.service;

import com.poc.inventory.entity.Inventory;
import com.poc.inventory.repository.InventoryRepository;
import com.poc.shared.events.InventoryUpdateEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
@EnableRetry
public class InventoryService {
    
    private final InventoryRepository inventoryRepository;
    
    @Transactional
    @Retryable(value = {OptimisticLockingFailureException.class}, 
               maxAttempts = 3, backoff = @Backoff(delay = 100))
    public InventoryUpdateEvent processOrderEvent(String orderId, String productId, Integer quantity) {
        log.info("Processing inventory update for orderId: {}, productId: {}, quantity: {}", 
                orderId, productId, quantity);
        
        try {
            Inventory inventory = inventoryRepository.findByProductId(productId)
                    .orElseThrow(() -> new RuntimeException("Product not found: " + productId));
            
            if (inventory.canReserve(quantity)) {
                inventory.reserveQuantity(quantity);
                inventoryRepository.save(inventory);
                
                log.info("Successfully reserved {} units for product {} (orderId: {})", 
                        quantity, productId, orderId);
                
                return new InventoryUpdateEvent(
                    orderId, productId, quantity, 
                    inventory.getAvailableQuantity(), true, 
                    LocalDateTime.now(), null
                );
            } else {
                log.warn("Insufficient inventory for product {} (orderId: {}). Requested: {}, Available: {}", 
                        productId, orderId, quantity, inventory.getAvailableQuantity());
                
                return new InventoryUpdateEvent(
                    orderId, productId, quantity, 
                    inventory.getAvailableQuantity(), false, 
                    LocalDateTime.now(), "Insufficient inventory"
                );
            }
        } catch (Exception e) {
            log.error("Failed to process inventory update for orderId: {}", orderId, e);
            
            return new InventoryUpdateEvent(
                orderId, productId, quantity, 0, false, 
                LocalDateTime.now(), "Processing error: " + e.getMessage()
            );
        }
    }
    
    public void initializeInventory(String productId, Integer initialQuantity) {
        Inventory inventory = new Inventory();
        inventory.setProductId(productId);
        inventory.setAvailableQuantity(initialQuantity);
        inventory.setReservedQuantity(0);
        inventory.setLastUpdated(LocalDateTime.now());
        
        inventoryRepository.save(inventory);
        log.info("Initialized inventory for product {}: {} units", productId, initialQuantity);
    }
}