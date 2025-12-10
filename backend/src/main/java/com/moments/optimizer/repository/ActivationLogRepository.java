package com.moments.optimizer.repository;

import com.moments.optimizer.domain.ActivationLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ActivationLogRepository extends JpaRepository<ActivationLog, String> {
}
