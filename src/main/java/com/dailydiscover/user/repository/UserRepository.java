package com.dailydiscover.user.repository;

import com.dailydiscover.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByAnonymousId(String anonymousId);

    User save(User user);
}