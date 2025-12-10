package com.moments.optimizer.repository;

import com.moments.optimizer.domain.AnalysisHistory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AnalysisHistoryRepository extends JpaRepository<AnalysisHistory, String> {
    Page<AnalysisHistory> findByUserIdOrderByCreatedAtDesc(String userId, Pageable pageable);
}
