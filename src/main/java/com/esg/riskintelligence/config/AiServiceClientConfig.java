package com.esg.riskintelligence.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

import java.time.Duration;

@Configuration
public class AiServiceClientConfig {

    @Value("${ai-service.base-url}")
    private String aiServiceBaseUrl;

    @Value("${ai-service.connect-timeout-ms}")
    private int connectTimeoutMs;

    @Value("${ai-service.read-timeout-ms}")
    private int readTimeoutMs;

    /**
     * RestClient pre-configured to talk to the AI service.
     *
     * Timeouts are externalized via properties (defaults: 2s connect, 10s read),
     * so they can be tuned per environment or for ad-hoc testing without
     * recompiling. In production these would come from Vault or AWS Secrets
     * Manager along with the rest of the upstream config.
     */
    @Bean
    public RestClient aiServiceRestClient() {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(Duration.ofMillis(connectTimeoutMs));
        factory.setReadTimeout(Duration.ofMillis(readTimeoutMs));

        return RestClient.builder()
                .baseUrl(aiServiceBaseUrl)
                .requestFactory(factory)
                .build();
    }
}