package com.moments.optimizer.model;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.moments.optimizer.model.http.HttpModelClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

@Configuration
public class ModelClientConfig {

    private static final Logger log = LoggerFactory.getLogger(ModelClientConfig.class);

    @Value("${moments.model.client-type:stub}")
    private String clientType;

    @Value("${moments.model.base-url:}")
    private String baseUrl;

    @Value("${moments.model.auth-header:}")
    private String authHeader;

    @Value("${moments.model.auth-token:}")
    private String authToken;

    @Value("${moments.model.timeout-ms:5000}")
    private int timeoutMs;

    @Bean
    public ModelClient modelClient(ObjectMapper objectMapper) {
        if ("http".equalsIgnoreCase(clientType)) {
            log.info("Using HttpModelClient (baseUrl configured: {})", !baseUrl.isBlank());
            RestTemplate restTemplate = buildRestTemplate(timeoutMs);
            return new HttpModelClient(restTemplate, objectMapper, baseUrl, authHeader, authToken, timeoutMs);
        }
        log.info("Using StubModelClient (client-type={})", clientType);
        return new StubModelClient(objectMapper);
    }

    private RestTemplate buildRestTemplate(int timeoutMs) {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(timeoutMs);
        factory.setReadTimeout(timeoutMs);
        return new RestTemplate(factory);
    }
}
