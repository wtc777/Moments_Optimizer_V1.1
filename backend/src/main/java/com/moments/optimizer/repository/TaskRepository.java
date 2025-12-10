package com.moments.optimizer.repository;

import com.moments.optimizer.domain.Task;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TaskRepository extends JpaRepository<Task, String> {
    Page<Task> findByStatusInOrderByCreatedAtAsc(List<String> statuses, Pageable pageable);
}
