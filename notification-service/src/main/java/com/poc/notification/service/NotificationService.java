package com.poc.notification.service;

import com.poc.notification.client.EmailClient;
import com.poc.notification.client.PushNotificationClient;
import com.poc.shared.events.NotificationEvent;
import com.poc.shared.events.OrderCreatedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationService {
    
    private final EmailClient emailClient;
    private final PushNotificationClient pushNotificationClient;
    
    public void processOrderCreatedEvent(OrderCreatedEvent orderEvent) {
        log.info("Processing notification for order: {}, customer: {}", 
                orderEvent.getOrderId(), orderEvent.getCustomerId());
        
        try {
            // Send email notification
            NotificationEvent emailEvent = createEmailNotification(orderEvent);
            emailClient.sendEmail(emailEvent);
            
            // Send push notification
            NotificationEvent pushEvent = createPushNotification(orderEvent);
            pushNotificationClient.sendPushNotification(pushEvent);
            
            log.info("Successfully sent notifications for order: {}", orderEvent.getOrderId());
            
        } catch (Exception e) {
            log.error("Failed to send notifications for order: {}", orderEvent.getOrderId(), e);
            throw e;
        }
    }
    
    private NotificationEvent createEmailNotification(OrderCreatedEvent orderEvent) {
        String message = String.format(
            "Your order %s has been created successfully! " +
            "Product: %s, Quantity: %s, Total: $%.2f",
            orderEvent.getOrderId(),
            orderEvent.getProductId(),
            orderEvent.getQuantity(),
            orderEvent.getPrice()
        );
        
        return new NotificationEvent(
            orderEvent.getOrderId(),
            orderEvent.getCustomerId(),
            "EMAIL",
            message,
            orderEvent.getCustomerId() + "@example.com", // Mock email
            LocalDateTime.now(),
            "order-confirmation"
        );
    }
    
    private NotificationEvent createPushNotification(OrderCreatedEvent orderEvent) {
        String message = String.format(
            "Order %s confirmed! %s x%s - $%.2f",
            orderEvent.getOrderId().substring(0, 8),
            orderEvent.getProductId(),
            orderEvent.getQuantity(),
            orderEvent.getPrice()
        );
        
        return new NotificationEvent(
            orderEvent.getOrderId(),
            orderEvent.getCustomerId(),
            "PUSH",
            message,
            "device_token_" + orderEvent.getCustomerId(), // Mock device token
            LocalDateTime.now(),
            "order-push"
        );
    }
}