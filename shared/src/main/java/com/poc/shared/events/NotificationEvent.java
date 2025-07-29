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
public class NotificationEvent {
    
    @NotNull
    private String orderId;
    
    @NotNull
    private String customerId;
    
    @NotNull
    private String notificationType; // EMAIL, PUSH, SMS
    
    @NotNull
    private String message;
    
    @NotNull
    private String recipient; // email address or device token
    
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdTimestamp;
    
    private String templateId; // for different notification templates
}