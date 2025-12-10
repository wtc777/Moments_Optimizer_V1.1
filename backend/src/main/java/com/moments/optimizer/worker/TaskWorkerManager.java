package com.moments.optimizer.worker;

import com.moments.optimizer.domain.Task;
import com.moments.optimizer.domain.TaskStep;
import com.moments.optimizer.model.ModelClient;
import com.moments.optimizer.model.ModelClientException;
import com.moments.optimizer.service.TaskService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

public class TaskWorkerManager {

    private static final Logger log = LoggerFactory.getLogger(TaskWorkerManager.class);
    private static final int SCAN_LIMIT = 1020;
    private static final long SCAN_INTERVAL_SECONDS = 5L;

    private final TaskService taskService;
    private final boolean enabled;
    private final ModelClient modelClient;
    private ScheduledExecutorService executor;

    public TaskWorkerManager(TaskService taskService, ModelClient modelClient, boolean enabled) {
        this.taskService = taskService;
        this.modelClient = modelClient;
        this.enabled = enabled;
    }

    public void start() {
        if (!enabled) {
            log.info("Task worker is disabled by configuration.");
            return;
        }
        if (executor != null && !executor.isShutdown()) {
            return;
        }
        executor = Executors.newSingleThreadScheduledExecutor(new WorkerThreadFactory());
        executor.scheduleAtFixedRate(this::safeScan, 0, SCAN_INTERVAL_SECONDS, TimeUnit.SECONDS);
        log.info("Task worker started with interval {}s and scan limit {}", SCAN_INTERVAL_SECONDS, SCAN_LIMIT);
    }

    public void stop() {
        if (executor != null) {
            executor.shutdown();
        }
        log.info("Task worker stopped.");
    }

    private void safeScan() {
        try {
            scanAndProcess();
        } catch (Exception ex) {
            log.error("Worker scan failed", ex);
        }
    }

    private void scanAndProcess() {
        List<Task> tasks = taskService.findRunnableTasks(SCAN_LIMIT);
        for (Task task : tasks) {
            switch (task.getStatus()) {
                case "PENDING" -> processPending(task);
                case "RUNNING" -> processRunning(task);
                default -> {
                }
            }
        }
    }

    private void processPending(Task task) {
        try {
            taskService.markTaskRunning(task.getId());
            TaskStep first = taskService.markFirstStepRunning(task.getId());
            log.info("Task {} transitioned PENDING -> RUNNING, first step {}", task.getId(),
                    first != null ? first.getStepKey() : "none");
        } catch (Exception ex) {
            log.error("Failed to start task {}", task.getId(), ex);
            taskService.markTaskFailed(task.getId(), "Failed to start task");
        }
    }

    private void processRunning(Task task) {
        String taskId = task.getId();
        Map<String, Object> accumulated = new HashMap<>();
        boolean failed = false;
        while (true) {
            TaskStep next = taskService.getNextPendingStep(taskId);
            if (next == null) {
                // No pending steps: mark success
                taskService.markTaskSuccess(taskId, accumulated);
                log.info("Task {} transitioned RUNNING -> SUCCESS", taskId);
                return;
            }
            try {
                Map<String, Object> stepResult = modelClient.runStep(next.getStepKey(), task, next);
                taskService.markStepSuccess(next.getId());
                accumulated.put(next.getStepKey(), stepResult);
                log.info("Task {} step {} -> SUCCESS", taskId, next.getStepKey());
            } catch (ModelClientException ex) {
                failed = true;
                log.error("Task {} step {} failed (code={}, message={})", taskId, next.getStepKey(),
                        ex.getErrorCode(), ex.getMessage());
                taskService.markStepFailed(next.getId(), ex.getMessage());
                taskService.markTaskFailed(taskId, "Step failed: " + ex.getMessage());
                break;
            } catch (Exception ex) {
                failed = true;
                log.error("Task {} step {} failed with unexpected error", taskId, next.getStepKey(), ex);
                taskService.markStepFailed(next.getId(), "Unexpected error");
                taskService.markTaskFailed(taskId, "Unexpected error");
                break;
            }
        }
        if (failed) {
            log.info("Task {} transitioned RUNNING -> FAILED", taskId);
        }
    }

    private static class WorkerThreadFactory implements ThreadFactory {
        @Override
        public Thread newThread(Runnable r) {
            Thread t = new Thread(r);
            t.setName("task-worker-thread");
            t.setDaemon(true);
            return t;
        }
    }
}
