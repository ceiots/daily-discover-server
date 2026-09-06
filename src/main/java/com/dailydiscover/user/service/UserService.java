package com.dailydiscover.user.service;

import com.dailydiscover.user.domain.User;
import com.dailydiscover.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;

    @Transactional
    public User getOrCreateUser(String anonymousId) {
        return userRepository.findByAnonymousId(anonymousId)
                .orElseGet(() -> createUser(anonymousId));
    }

    private User createUser(String anonymousId) {
        User user = User.builder()
                .anonymousId(anonymousId)
                .build();
        return userRepository.save(user);
    }

    public Optional<User> findByAnonymousId(String anonymousId) {
        return userRepository.findByAnonymousId(anonymousId);
    }

    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }
}