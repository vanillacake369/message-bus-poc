package com.poc.inventory.consumer;

import com.poc.inventory.service.InventoryService;
import com.poc.shared.events.InventoryUpdateEvent;
import com.poc.shared.events.OrderCreatedEvent;
import com.poc.shared.utils.EventUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.function.Consumer;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderEventConsumer {
    
    private final InventoryService inventoryService;
    private final EventUtils eventUtils;
    
    @Bean
    public Consumer<String> processOrderEvents() {
        return message -> {
            try {
                log.debug("Received order event message: {}", message);
                
                OrderCreatedEvent orderEvent = eventUtils.deserialize(message, OrderCreatedEvent.class);
                log.info("Processing order event for orderId: {}, customerId: {}", 
                        orderEvent.getOrderId(), orderEvent.getCustomerId());
                
                InventoryUpdateEvent result = inventoryService.processOrderEvent(
                    orderEvent.getOrderId(),
                    orderEvent.getProductId(),
                    orderEvent.getQuantity()
                );
                
                if (result.getInventoryReserved()) {
                    log.info("Successfully processed order event for orderId: {}", orderEvent.getOrderId());
                } else {
                    log.warn("Failed to reserve inventory for orderId: {} - {}", 
                            orderEvent.getOrderId(), result.getFailureReason());
                }
                
            } catch (Exception e) {
                log.error("Failed to process order event message: {}", message, e);
                // In a production system, this would go to a dead letter queue
                throw e;
            }
        };
    }
}