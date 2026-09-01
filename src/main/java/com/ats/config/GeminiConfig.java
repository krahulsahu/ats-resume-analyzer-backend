package com.ats.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;

/**
 * Configuration for Google Gemini 1.5 Flash API.
 * The API key is securely loaded from environment variables / backend .env and never committed to code.
 */
@Configuration
public class GeminiConfig {

    @Value("${gemini.api-key:}")
    private String apiKey;

    @Value("${gemini.model:gemini-1.5-flash}")
    private String model;

    @Value("${gemini.base-url:https://generativelanguage.googleapis.com/v1beta/models}")
    private String baseUrl;

    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder) {
        return builder
                .connectTimeout(Duration.ofSeconds(15))
                .readTimeout(Duration.ofSeconds(60))
                .build();
    }

    public String getApiKey() {
        return apiKey != null ? apiKey.trim() : "";
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public String getModel() {
        return model != null && !model.isBlank() ? model : "gemini-1.5-flash";
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public boolean hasApiKey() {
        String key = getApiKey();
        return key != null && !key.isBlank();
    }
}
