package com.moments.optimizer.model;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.moments.optimizer.domain.Task;
import com.moments.optimizer.domain.TaskStep;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class StubModelClient implements ModelClient {

    private static final TypeReference<Map<String, Object>> MAP_TYPE = new TypeReference<>() {};
    private final ObjectMapper objectMapper;

    public StubModelClient(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public Map<String, Object> runStep(String stepKey, Task task, TaskStep step) throws ModelClientException {
        Map<String, Object> payload = parsePayload(task.getPayloadJson());
        Map<String, Object> response = new HashMap<>();
        response.put("stepKey", stepKey);
        response.put("message", "stub");
        response.put("inputEcho", payload);
        response.put("taskType", task.getType());
        response.put("stepOrder", step.getStepOrder());
        return Collections.unmodifiableMap(response);
    }

    private Map<String, Object> parsePayload(String payloadJson) throws ModelClientException {
        if (payloadJson == null || payloadJson.isBlank()) {
            return Collections.emptyMap();
        }
        try {
            return objectMapper.readValue(payloadJson, MAP_TYPE);
        } catch (Exception ex) {
            throw new ModelClientException("PARSE_ERROR", "Failed to parse task payload", null);
        }
    }
}
