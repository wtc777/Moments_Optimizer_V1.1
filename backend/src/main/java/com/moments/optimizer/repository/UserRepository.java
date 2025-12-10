package com.moments.optimizer.repository;

import com.moments.optimizer.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, String> {
}
