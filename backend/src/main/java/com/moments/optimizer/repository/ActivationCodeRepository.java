package com.moments.optimizer.repository;

import com.moments.optimizer.domain.ActivationCode;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ActivationCodeRepository extends JpaRepository<ActivationCode, String> {
}
