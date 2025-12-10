package com.moments.optimizer.repository;

import com.moments.optimizer.domain.TaskStep;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TaskStepRepository extends JpaRepository<TaskStep, Long> {
    List<TaskStep> findByTaskIdOrderByStepOrderAsc(String taskId);
}
