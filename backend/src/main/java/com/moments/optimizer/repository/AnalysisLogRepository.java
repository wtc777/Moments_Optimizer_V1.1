package com.moments.optimizer.repository;

import com.moments.optimizer.domain.AnalysisLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AnalysisLogRepository extends JpaRepository<AnalysisLog, String> {
}
