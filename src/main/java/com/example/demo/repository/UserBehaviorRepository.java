package com.example.demo.repository;

import com.example.demo.model.UserBehavior;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface UserBehaviorRepository extends JpaRepository<UserBehavior, Long> {
    List<UserBehavior> findByUserId(Long userId);
    List<UserBehavior> findByActionType(String actionType);
    List<UserBehavior> findByCreatedAtBetween(LocalDateTime start, LocalDateTime end);
    List<UserBehavior> findBySessionId(String sessionId);
}
