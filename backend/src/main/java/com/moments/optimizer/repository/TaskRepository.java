package com.moments.optimizer.repository;

import com.moments.optimizer.domain.Task;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TaskRepository extends JpaRepository<Task, String> {
}
