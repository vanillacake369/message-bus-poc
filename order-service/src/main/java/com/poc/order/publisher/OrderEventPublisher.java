package com.poc.order.publisher;

import com.poc.shared.events.OrderCreatedEvent;
import com.poc.shared.utils.EventUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderEventPublisher {
    
    private final StreamBridge streamBridge;
    private final EventUtils eventUtils;
    
    private static final String ORDER_EVENTS_BINDING = "orderEvents-out-0";
    
    public void publishOrderCreated(OrderCreatedEvent event) {
        try {
            String eventJson = eventUtils.serialize(event);
            log.info("Publishing order created event for orderId: {}", event.getOrderId());
            
            // Create message with partition key header for ordering
            Message<String> message = MessageBuilder
                .withPayload(eventJson)
                .setHeader("partitionKey", event.getCustomerId())
                .build();
            
            streamBridge.send(ORDER_EVENTS_BINDING, message);
            
            log.debug("Successfully published order created event: {}", eventJson);
        } catch (Exception e) {
            log.error("Failed to publish order created event for orderId: {}", event.getOrderId(), e);
            throw new RuntimeException("Failed to publish order event", e);
        }
    }
}