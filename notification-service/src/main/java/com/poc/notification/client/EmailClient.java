package com.poc.notification.client;

import com.poc.shared.events.NotificationEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.Duration;

@Slf4j
@Component
public class EmailClient {
    
    private final WebClient webClient;
    
    public EmailClient() {
        this.webClient = WebClient.builder()
                .baseUrl("https://api.emailservice.com") // Mock URL
                .build();
    }
    
    public void sendEmail(NotificationEvent notification) {
        log.info("Sending email to: {} for order: {}", 
                notification.getRecipient(), notification.getOrderId());
        
        try {
            // Mock external email service call
            EmailRequest request = new EmailRequest(
                notification.getRecipient(),
                "Order Confirmation - " + notification.getOrderId(),
                notification.getMessage(),
                notification.getTemplateId()
            );
            
            // In real implementation, this would be an actual HTTP call
            // For now, we'll just simulate the call
            simulateEmailSend(request);
            
            log.info("Successfully sent email for order: {}", notification.getOrderId());
            
        } catch (Exception e) {
            log.error("Failed to send email for order: {}", notification.getOrderId(), e);
            throw new RuntimeException("Email sending failed", e);
        }
    }
    
    private void simulateEmailSend(EmailRequest request) {
        // Simulate network delay
        try {
            Thread.sleep(100);
            log.debug("Mock email sent to: {}", request.getTo());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Email simulation interrupted", e);
        }
    }
    
    public static class EmailRequest {
        private String to;
        private String subject;
        private String body;
        private String templateId;
        
        public EmailRequest(String to, String subject, String body, String templateId) {
            this.to = to;
            this.subject = subject;
            this.body = body;
            this.templateId = templateId;
        }
        
        // Getters
        public String getTo() { return to; }
        public String getSubject() { return subject; }
        public String getBody() { return body; }
        public String getTemplateId() { return templateId; }
    }
}