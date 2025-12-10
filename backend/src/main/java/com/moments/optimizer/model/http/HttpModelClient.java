package com.moments.optimizer.model.http;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.moments.optimizer.domain.Task;
import com.moments.optimizer.domain.TaskStep;
import com.moments.optimizer.model.ModelClient;
import com.moments.optimizer.model.ModelClientException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

public class HttpModelClient implements ModelClient {

    private static final Logger log = LoggerFactory.getLogger(HttpModelClient.class);
    private static final String PATH = "/internal/model/runStep";
    private static final int MAX_SNIPPET = 300;
    private static final TypeReference<Map<String, Object>> MAP_TYPE = new TypeReference<>() {};

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    private final String baseUrl;
    private final String authHeader;
    private final String authToken;
    private final int timeoutMs;

    public HttpModelClient(RestTemplate restTemplate,
                           ObjectMapper objectMapper,
                           String baseUrl,
                           String authHeader,
                           String authToken,
                           int timeoutMs) {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
        this.baseUrl = baseUrl;
        this.authHeader = authHeader;
        this.authToken = authToken;
        this.timeoutMs = timeoutMs;
    }

    @Override
    public Map<String, Object> runStep(String stepKey, Task task, TaskStep step) throws ModelClientException {
        if (baseUrl == null || baseUrl.isBlank()) {
            throw new ModelClientException("CONFIG_ERROR", "Model base URL is not configured");
        }
        Map<String, Object> requestBody = buildRequestBody(stepKey, task, step);
        String url = normalizeUrl(baseUrl) + PATH;
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            if (authHeader != null && !authHeader.isBlank() && authToken != null && !authToken.isBlank()) {
                headers.set(authHeader, authToken);
            }
            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);
            ResponseEntity<String> response = restTemplate.postForEntity(url, entity, String.class);
            return handleResponse(response);
        } catch (ModelClientException e) {
            throw e;
        } catch (RestClientException e) {
            throw new ModelClientException("HTTP_ERROR", "HTTP request failed: " + e.getMessage(), null);
        } catch (Exception e) {
            throw new ModelClientException("HTTP_ERROR", "Model HTTP call failed", null);
        }
    }

    private Map<String, Object> buildRequestBody(String stepKey, Task task, TaskStep step) throws ModelClientException {
        Map<String, Object> payloadData = parsePayload(task.getPayloadJson());
        Map<String, Object> body = new HashMap<>();
        body.put("stepKey", stepKey);
        body.put("taskId", task.getId());
        body.put("taskType", task.getType());
        body.put("userId", payloadData.getOrDefault("userId", null));
        body.put("stepOrder", step.getStepOrder());
        body.put("payload", payloadData);
        body.put("options", Map.of("timeoutMs", timeoutMs));
        return body;
    }

    private Map<String, Object> handleResponse(ResponseEntity<String> response) throws ModelClientException {
        String body = response.getBody();
        if (!response.getStatusCode().is2xxSuccessful()) {
            throw new ModelClientException("HTTP_ERROR", "Non-2xx from model service", truncate(body));
        }
        try {
            Map<String, Object> parsed = objectMapper.readValue(body, MAP_TYPE);
            Object success = parsed.get("success");
            if (Boolean.TRUE.equals(success)) {
                Object data = parsed.get("data");
                if (data instanceof Map) {
                    return (Map<String, Object>) data;
                }
                return Map.of("data", data);
            }
            String code = parsed.getOrDefault("code", "REMOTE_ERROR").toString();
            String message = parsed.getOrDefault("message", "Remote model error").toString();
            throw new ModelClientException("REMOTE_ERROR", message, truncate(body));
        } catch (ModelClientException e) {
            throw e;
        } catch (Exception e) {
            throw new ModelClientException("PARSE_ERROR", "Failed to parse model response", truncate(body));
        }
    }

    private String truncate(String body) {
        if (body == null) {
            return null;
        }
        return body.length() <= MAX_SNIPPET ? body : body.substring(0, MAX_SNIPPET);
    }

    private String normalizeUrl(String url) {
        if (url.endsWith("/")) {
            return url.substring(0, url.length() - 1);
        }
        return url;
    }

    private Map<String, Object> parsePayload(String payloadJson) throws ModelClientException {
        if (payloadJson == null || payloadJson.isBlank()) {
            return Map.of();
        }
        try {
            return objectMapper.readValue(payloadJson, MAP_TYPE);
        } catch (Exception ex) {
            throw new ModelClientException("PARSE_ERROR", "Failed to parse task payload", null);
        }
    }
}
