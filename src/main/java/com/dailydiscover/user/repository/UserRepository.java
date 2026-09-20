package com.dailydiscover.user.repository;

import com.dailydiscover.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByAnonymousId(String anonymousId);
}
