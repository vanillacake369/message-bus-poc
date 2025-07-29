package com.poc.notification.client;

import com.poc.shared.events.NotificationEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class PushNotificationClient {
    
    public void sendPushNotification(NotificationEvent notification) {
        log.info("Sending push notification to device: {} for order: {}", 
                notification.getRecipient(), notification.getOrderId());
        
        try {
            // Mock push notification service call
            PushRequest request = new PushRequest(
                notification.getRecipient(),
                "Order Update",
                notification.getMessage(),
                notification.getOrderId()
            );
            
            // Simulate push notification sending
            simulatePushSend(request);
            
            log.info("Successfully sent push notification for order: {}", notification.getOrderId());
            
        } catch (Exception e) {
            log.error("Failed to send push notification for order: {}", notification.getOrderId(), e);
            throw new RuntimeException("Push notification sending failed", e);
        }
    }
    
    private void simulatePushSend(PushRequest request) {
        // Simulate network delay
        try {
            Thread.sleep(50);
            log.debug("Mock push notification sent to device: {}", request.getDeviceToken());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Push notification simulation interrupted", e);
        }
    }
    
    public static class PushRequest {
        private String deviceToken;
        private String title;
        private String message;
        private String orderId;
        
        public PushRequest(String deviceToken, String title, String message, String orderId) {
            this.deviceToken = deviceToken;
            this.title = title;
            this.message = message;
            this.orderId = orderId;
        }
        
        // Getters
        public String getDeviceToken() { return deviceToken; }
        public String getTitle() { return title; }
        public String getMessage() { return message; }
        public String getOrderId() { return orderId; }
    }
}