package com.dailydiscover.user.service;

import com.dailydiscover.user.domain.User;
import com.dailydiscover.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    /**
     * 收到 anonymous_id → 查询 users → 不存在则创建 → 继续处理
     */
    @Transactional
    public User getOrCreateUser(String anonymousId) {
        return userRepository.findByAnonymousId(anonymousId)
                .orElseGet(() -> createUser(anonymousId));
    }

    private User createUser(String anonymousId) {
        try {
            return userRepository.save(User.builder().anonymousId(anonymousId).build());
        } catch (DataIntegrityViolationException e) {
            // 并发下唯一约束冲突：回读已存在用户
            return userRepository.findByAnonymousId(anonymousId).orElseThrow();
        }
    }
}
