package com.poc.shared.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class EventUtils {
    
    private final ObjectMapper eventObjectMapper;
    
    public <T> String serialize(T event) {
        try {
            return eventObjectMapper.writeValueAsString(event);
        } catch (JsonProcessingException e) {
            log.error("Failed to serialize event: {}", event, e);
            throw new RuntimeException("Event serialization failed", e);
        }
    }
    
    public <T> T deserialize(String json, Class<T> clazz) {
        try {
            return eventObjectMapper.readValue(json, clazz);
        } catch (JsonProcessingException e) {
            log.error("Failed to deserialize event from JSON: {}", json, e);
            throw new RuntimeException("Event deserialization failed", e);
        }
    }
}