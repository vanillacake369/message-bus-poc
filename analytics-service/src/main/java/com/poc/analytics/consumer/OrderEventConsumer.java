package com.poc.analytics.consumer;

import com.poc.analytics.service.AnalyticsService;
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
    
    private final AnalyticsService analyticsService;
    private final EventUtils eventUtils;
    
    @Bean
    public Consumer<String> processOrderEvents() {
        return message -> {
            try {
                log.debug("Received order event message: {}", message);
                
                OrderCreatedEvent orderEvent = eventUtils.deserialize(message, OrderCreatedEvent.class);
                log.info("Processing analytics for order: {}, customer: {}, value: ${}", 
                        orderEvent.getOrderId(), 
                        orderEvent.getCustomerId(),
                        orderEvent.getPrice().multiply(java.math.BigDecimal.valueOf(orderEvent.getQuantity())));
                
                analyticsService.processOrderEvent(orderEvent);
                
                log.info("Successfully processed analytics for order: {}", orderEvent.getOrderId());
                
            } catch (Exception e) {
                log.error("Failed to process order event message: {}", message, e);
                // In a production system, this would go to a dead letter queue
                throw e;
            }
        };
    }
}