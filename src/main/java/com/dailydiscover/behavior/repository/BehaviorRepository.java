package com.dailydiscover.behavior.repository;

import com.dailydiscover.behavior.domain.Behavior;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BehaviorRepository extends JpaRepository<Behavior, Long> {
}
