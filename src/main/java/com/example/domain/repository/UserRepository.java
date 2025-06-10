package com.example.domain.repository;

import com.example.domain.model.user.User;

import java.util.List;
import java.util.Optional;

/**
 * 用户仓储接口
 */
public interface UserRepository {
    /**
     * 根据ID查询用户
     *
     * @param id 用户ID
     * @return 用户对象
     */
    Optional<User> findById(Long id);

    /**
     * 根据用户名查询用户
     *
     * @param username 用户名
     * @return 用户对象
     */
    Optional<User> findByUsername(String username);

    /**
     * 根据手机号查询用户
     *
     * @param phone 手机号
     * @return 用户对象
     */
    Optional<User> findByPhone(String phone);

    /**
     * 根据邮箱查询用户
     *
     * @param email 邮箱
     * @return 用户对象
     */
    Optional<User> findByEmail(String email);

    /**
     * 保存用户
     *
     * @param user 用户对象
     * @return 保存后的用户对象
     */
    User save(User user);

    /**
     * 更新用户
     *
     * @param user 用户对象
     * @return 更新后的用户对象
     */
    User update(User user);

    /**
     * 删除用户
     *
     * @param id 用户ID
     */
    void deleteById(Long id);

    /**
     * 分页查询用户
     *
     * @param pageNum  页码
     * @param pageSize 每页大小
     * @return 用户列表
     */
    List<User> findByPage(int pageNum, int pageSize);

    /**
     * 查询用户总数
     *
     * @return 用户总数
     */
    long count();
} 