package com.moments.optimizer.controller.api;

import com.moments.optimizer.api.ApiResponse;
import com.moments.optimizer.dto.TaskCreateRequest;
import com.moments.optimizer.dto.TaskDetailDto;
import com.moments.optimizer.service.TaskService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {

    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<TaskDetailDto>> createTask(@RequestBody TaskCreateRequest request) {
        TaskDetailDto task = taskService.createTask(request);
        return ResponseEntity.ok(ApiResponse.ok(task));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<TaskDetailDto>> getTask(@PathVariable("id") String id) {
        TaskDetailDto task = taskService.getTask(id);
        return ResponseEntity.ok(ApiResponse.ok(task));
    }
}
