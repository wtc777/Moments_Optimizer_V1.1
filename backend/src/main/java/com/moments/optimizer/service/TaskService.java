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
import com.moments.optimizer.repository.TaskRepository;
import com.moments.optimizer.repository.TaskStepRepository;
import org.springframework.data.domain.PageRequest;
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

    private final TaskRepository taskRepository;
    private final TaskStepRepository taskStepRepository;
    private final ObjectMapper objectMapper;

    public TaskService(TaskRepository taskRepository, TaskStepRepository taskStepRepository, ObjectMapper objectMapper) {
        this.taskRepository = taskRepository;
        this.taskStepRepository = taskStepRepository;
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
        taskRepository.save(task);

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
        taskStepRepository.saveAll(steps);

        return toDetailDto(task, taskStepRepository.findByTaskIdOrderByStepOrderAsc(taskId));
    }

    @Transactional(readOnly = true)
    public TaskDetailDto getTask(String taskId) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new NotFoundException(ErrorCodes.TASK_NOT_FOUND, "Task not found"));
        validateStatus(task.getStatus());
        List<TaskStep> steps = taskStepRepository.findByTaskIdOrderByStepOrderAsc(taskId);
        steps.forEach(step -> validateStatus(step.getStatus()));
        return toDetailDto(task, steps);
    }

    @Transactional(readOnly = true)
    public List<Task> findRunnableTasks(int limit) {
        PageRequest pageRequest = PageRequest.of(0, Math.max(1, limit));
        return taskRepository.findByStatusInOrderByCreatedAtAsc(
                        List.of("PENDING", "RUNNING"), pageRequest)
                .getContent();
    }

    @Transactional
    public Task markTaskRunning(String taskId) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new NotFoundException(ErrorCodes.TASK_NOT_FOUND, "Task not found"));
        if (!"PENDING".equals(task.getStatus()) && !"RUNNING".equals(task.getStatus())) {
            throw new BadRequestException(ErrorCodes.VALIDATION_ERROR, "Task not runnable");
        }
        task.setStatus("RUNNING");
        task.setUpdatedAt(LocalDateTime.now());
        return taskRepository.save(task);
    }

    @Transactional
    public TaskStep markFirstStepRunning(String taskId) {
        List<TaskStep> steps = taskStepRepository.findByTaskIdOrderByStepOrderAsc(taskId);
        TaskStep firstPending = steps.stream()
                .filter(s -> "PENDING".equals(s.getStatus()))
                .findFirst()
                .orElse(null);
        if (firstPending == null) {
            return null;
        }
        firstPending.setStatus("RUNNING");
        firstPending.setStartedAt(LocalDateTime.now());
        return taskStepRepository.save(firstPending);
    }

    @Transactional(readOnly = true)
    public TaskStep getNextPendingStep(String taskId) {
        List<TaskStep> steps = taskStepRepository.findByTaskIdOrderByStepOrderAsc(taskId);
        return steps.stream()
                .filter(s -> "PENDING".equals(s.getStatus()))
                .findFirst()
                .orElse(null);
    }

    @Transactional
    public TaskStep markStepSuccess(Long stepId) {
        TaskStep step = taskStepRepository.findById(stepId)
                .orElseThrow(() -> new NotFoundException(ErrorCodes.TASK_NOT_FOUND, "Step not found"));
        step.setStatus("SUCCESS");
        step.setFinishedAt(LocalDateTime.now());
        return taskStepRepository.save(step);
    }

    @Transactional
    public TaskStep markStepFailed(Long stepId, String errorMessage) {
        TaskStep step = taskStepRepository.findById(stepId)
                .orElseThrow(() -> new NotFoundException(ErrorCodes.TASK_NOT_FOUND, "Step not found"));
        step.setStatus("FAILED");
        step.setFinishedAt(LocalDateTime.now());
        return taskStepRepository.save(step);
    }

    @Transactional
    public Task markTaskSuccess(String taskId, Map<String, Object> result) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new NotFoundException(ErrorCodes.TASK_NOT_FOUND, "Task not found"));
        task.setStatus("SUCCESS");
        task.setResultJson(writeJson(result));
        task.setUpdatedAt(LocalDateTime.now());
        return taskRepository.save(task);
    }

    @Transactional
    public Task markTaskFailed(String taskId, String errorMessage) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new NotFoundException(ErrorCodes.TASK_NOT_FOUND, "Task not found"));
        task.setStatus("FAILED");
        task.setErrorMessage(errorMessage);
        task.setUpdatedAt(LocalDateTime.now());
        return taskRepository.save(task);
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

    private record StepTemplate(int order, String key, String label) {}
}
