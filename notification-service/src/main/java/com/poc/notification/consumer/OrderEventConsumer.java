package com.poc.notification.consumer;

import com.poc.notification.service.NotificationService;
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
    
    private final NotificationService notificationService;
    private final EventUtils eventUtils;
    
    @Bean
    public Consumer<String> processOrderEvents() {
        return message -> {
            try {
                log.debug("Received order event message: {}", message);
                
                OrderCreatedEvent orderEvent = eventUtils.deserialize(message, OrderCreatedEvent.class);
                log.info("Processing notification for order: {}, customer: {}", 
                        orderEvent.getOrderId(), orderEvent.getCustomerId());
                
                notificationService.processOrderCreatedEvent(orderEvent);
                
                log.info("Successfully processed notification for order: {}", orderEvent.getOrderId());
                
            } catch (Exception e) {
                log.error("Failed to process order event message: {}", message, e);
                // In a production system, this would go to a dead letter queue
                throw e;
            }
        };
    }
}