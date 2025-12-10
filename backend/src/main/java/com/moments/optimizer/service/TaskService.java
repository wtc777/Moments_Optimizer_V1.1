package com.moments.optimizer.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.moments.optimizer.api.ErrorCodes;
import com.moments.optimizer.domain.Task;
import com.moments.optimizer.domain.TaskStep;
import com.moments.optimizer.dto.TaskCreateRequest;
import com.moments.optimizer.dto.TaskDetailDto;
import com.moments.optimizer.dto.TaskStepDto;
import com.moments.optimizer.exception.BadRequestException;
import com.moments.optimizer.exception.NotFoundException;
import com.moments.optimizer.mapper.TaskMapper;
import com.moments.optimizer.mapper.TaskStepMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Service
public class TaskService {

    private static final Set<String> ALLOWED_STATUS = Set.of("PENDING", "RUNNING", "SUCCESS", "FAILED");

    private static final List<StepTemplate> DEFAULT_STEPS = List.of(
            new StepTemplate(1, "image_processing", "Image processing"),
            new StepTemplate(2, "image_model_call", "Image model call"),
            new StepTemplate(3, "image_result_saved", "Image result saved"),
            new StepTemplate(4, "prompt_building", "Prompt building"),
            new StepTemplate(5, "llm_call", "LLM call"),
            new StepTemplate(6, "final_result", "Final result")
    );

    private final TaskMapper taskMapper;
    private final TaskStepMapper taskStepMapper;
    private final ObjectMapper objectMapper;

    public TaskService(TaskMapper taskMapper, TaskStepMapper taskStepMapper, ObjectMapper objectMapper) {
        this.taskMapper = taskMapper;
        this.taskStepMapper = taskStepMapper;
        this.objectMapper = objectMapper;
    }

    @Transactional
    public TaskDetailDto createTask(TaskCreateRequest request) {
        if (request.getUserId() == null || request.getUserId().isBlank()) {
            throw new BadRequestException(ErrorCodes.VALIDATION_ERROR, "userId is required");
        }
        String type = request.getType() == null || request.getType().isBlank()
                ? "moments_optimize"
                : request.getType();

        String taskId = UUID.randomUUID().toString();
        LocalDateTime now = LocalDateTime.now();

        Task task = new Task();
        task.setId(taskId);
        task.setType(type);
        task.setStatus("PENDING");
        task.setPayloadJson(serializePayload(request));
        task.setCreatedAt(now);
        task.setUpdatedAt(now);
        taskMapper.insertTask(task);

        List<TaskStep> steps = new ArrayList<>();
        for (StepTemplate template : DEFAULT_STEPS) {
            TaskStep step = new TaskStep();
            step.setTaskId(taskId);
            step.setStepOrder(template.order());
            step.setStepKey(template.key());
            step.setStepLabel(template.label());
            step.setStatus("PENDING");
            steps.add(step);
        }
        taskStepMapper.insertSteps(steps);

        return toDetailDto(task, taskStepMapper.selectByTaskId(taskId));
    }

    @Transactional(readOnly = true)
    public TaskDetailDto getTask(String taskId) {
        Task task = taskMapper.selectById(taskId);
        if (task == null) {
            throw new NotFoundException(ErrorCodes.TASK_NOT_FOUND, "Task not found");
        }
        validateStatus(task.getStatus());
        List<TaskStep> steps = taskStepMapper.selectByTaskId(taskId);
        steps.forEach(step -> validateStatus(step.getStatus()));
        return toDetailDto(task, steps);
    }

    @Transactional(readOnly = true)
    public List<Task> findRunnableTasks(int limit) {
        return taskMapper.selectRunnableTasks(Math.max(1, limit));
    }

    @Transactional
    public Task markTaskRunning(String taskId) {
        Task task = taskMapper.selectById(taskId);
        if (task == null) {
            throw new NotFoundException(ErrorCodes.TASK_NOT_FOUND, "Task not found");
        }
        if (!"PENDING".equals(task.getStatus()) && !"RUNNING".equals(task.getStatus())) {
            throw new BadRequestException(ErrorCodes.VALIDATION_ERROR, "Task not runnable");
        }
        LocalDateTime now = LocalDateTime.now();
        task.setStatus("RUNNING");
        task.setUpdatedAt(now);
        taskMapper.updateStatus(taskId, "RUNNING", task.getErrorMessage(), now, task.getResultJson());
        return taskMapper.selectById(taskId);
    }

    @Transactional
    public TaskStep markFirstStepRunning(String taskId) {
        TaskStep firstPending = taskStepMapper.selectFirstPendingStep(taskId);
        if (firstPending == null) {
            return null;
        }
        firstPending.setStatus("RUNNING");
        firstPending.setStartedAt(LocalDateTime.now());
        taskStepMapper.updateStepStatus(firstPending.getId(), "RUNNING", firstPending.getStartedAt(), null);
        return taskStepMapper.selectFirstPendingStep(taskId) == null ? firstPending : firstPending;
    }

    @Transactional(readOnly = true)
    public TaskStep getNextPendingStep(String taskId) {
        return taskStepMapper.selectFirstPendingStep(taskId);
    }

    @Transactional
    public TaskStep markStepSuccess(Long stepId) {
        TaskStep step = findStepById(stepId);
        LocalDateTime now = LocalDateTime.now();
        taskStepMapper.updateStepStatus(stepId, "SUCCESS", step.getStartedAt(), now);
        step.setStatus("SUCCESS");
        step.setFinishedAt(now);
        return step;
    }

    @Transactional
    public TaskStep markStepFailed(Long stepId, String errorMessage) {
        TaskStep step = findStepById(stepId);
        LocalDateTime now = LocalDateTime.now();
        taskStepMapper.updateStepStatus(stepId, "FAILED", step.getStartedAt(), now);
        step.setStatus("FAILED");
        step.setFinishedAt(now);
        return step;
    }

    @Transactional
    public Task markTaskSuccess(String taskId, Map<String, Object> result) {
        Task task = requireTask(taskId);
        LocalDateTime now = LocalDateTime.now();
        taskMapper.updateStatus(taskId, "SUCCESS", task.getErrorMessage(), now, writeJson(result));
        task.setStatus("SUCCESS");
        task.setResultJson(writeJson(result));
        task.setUpdatedAt(now);
        return task;
    }

    @Transactional
    public Task markTaskFailed(String taskId, String errorMessage) {
        Task task = requireTask(taskId);
        LocalDateTime now = LocalDateTime.now();
        taskMapper.updateStatus(taskId, "FAILED", errorMessage, now, task.getResultJson());
        task.setStatus("FAILED");
        task.setErrorMessage(errorMessage);
        task.setUpdatedAt(now);
        return task;
    }

    private void validateStatus(String status) {
        if (!ALLOWED_STATUS.contains(status)) {
            throw new BadRequestException(ErrorCodes.VALIDATION_ERROR, "Invalid status value");
        }
    }

    private String serializePayload(TaskCreateRequest request) {
        Map<String, Object> payload = new HashMap<>();
        payload.put("userId", request.getUserId());
        payload.put("type", request.getType());
        payload.put("inputText", request.getInputText());
        payload.put("imageUrls", request.getImageUrls());
        payload.put("options", request.getOptions());
        try {
            return objectMapper.writeValueAsString(payload);
        } catch (JsonProcessingException e) {
            throw new BadRequestException(ErrorCodes.VALIDATION_ERROR, "Invalid payload");
        }
    }

    private TaskDetailDto toDetailDto(Task task, List<TaskStep> steps) {
        TaskDetailDto dto = new TaskDetailDto();
        dto.setId(task.getId());
        dto.setType(task.getType());
        dto.setStatus(task.getStatus());
        dto.setCreatedAt(task.getCreatedAt());
        dto.setUpdatedAt(task.getUpdatedAt());
        dto.setResultJson(parseJson(task.getResultJson()));
        dto.setErrorMessage(task.getErrorMessage());

        List<TaskStepDto> stepDtos = new ArrayList<>();
        for (TaskStep step : steps) {
            TaskStepDto s = new TaskStepDto();
            s.setKey(step.getStepKey());
            s.setLabel(step.getStepLabel());
            s.setStatus(step.getStatus());
            s.setStartedAt(step.getStartedAt());
            s.setFinishedAt(step.getFinishedAt());
            stepDtos.add(s);
        }
        dto.setSteps(stepDtos);
        return dto;
    }

    private Map<String, Object> parseJson(String json) {
        if (json == null || json.isBlank()) {
            return null;
        }
        try {
            return objectMapper.readValue(json, Map.class);
        } catch (Exception e) {
            return null;
        }
    }

    private String writeJson(Map<String, Object> data) {
        if (data == null) {
            return null;
        }
        try {
            return objectMapper.writeValueAsString(data);
        } catch (JsonProcessingException e) {
            throw new BadRequestException(ErrorCodes.VALIDATION_ERROR, "Invalid result");
        }
    }

    private Task requireTask(String taskId) {
        Task task = taskMapper.selectById(taskId);
        if (task == null) {
            throw new NotFoundException(ErrorCodes.TASK_NOT_FOUND, "Task not found");
        }
        return task;
    }

    private TaskStep findStepById(Long stepId) {
        TaskStep step = taskStepMapper.selectById(stepId);
        if (step == null) {
            throw new NotFoundException(ErrorCodes.TASK_NOT_FOUND, "Step not found");
        }
        return step;
    }

    private record StepTemplate(int order, String key, String label) {}
}
