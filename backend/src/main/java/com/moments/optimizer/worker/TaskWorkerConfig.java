package com.moments.optimizer.worker;

import com.moments.optimizer.model.ModelClient;
import com.moments.optimizer.service.TaskService;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TaskWorkerConfig {

    private static final Logger log = LoggerFactory.getLogger(TaskWorkerConfig.class);

    private final TaskService taskService;
    private final boolean enabled;
    private final ModelClient modelClient;
    private TaskWorkerManager manager;

    public TaskWorkerConfig(TaskService taskService,
                            ModelClient modelClient,
                            @Value("${moments.worker.enabled:true}") boolean enabled) {
        this.taskService = taskService;
        this.modelClient = modelClient;
        this.enabled = enabled;
    }

    @Bean
    public TaskWorkerManager taskWorkerManager() {
        this.manager = new TaskWorkerManager(taskService, modelClient, enabled);
        return this.manager;
    }

    @PostConstruct
    public void startWorker() {
        if (manager == null) {
            return;
        }
        if (enabled) {
            log.info("Starting task worker (enabled={})", enabled);
            manager.start();
        } else {
            log.info("Task worker disabled by configuration (moments.worker.enabled=false)");
        }
    }

    @PreDestroy
    public void stopWorker() {
        if (manager != null) {
            log.info("Stopping task worker");
            manager.stop();
        }
    }
}
