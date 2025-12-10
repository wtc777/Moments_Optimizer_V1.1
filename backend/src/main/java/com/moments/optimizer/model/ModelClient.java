package com.moments.optimizer.model;

import com.moments.optimizer.domain.Task;
import com.moments.optimizer.domain.TaskStep;

import java.util.Map;

public interface ModelClient {
    Map<String, Object> runStep(String stepKey, Task task, TaskStep step) throws ModelClientException;
}
