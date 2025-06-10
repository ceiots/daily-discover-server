package com.example.infrastructure.repository.mapper;

import com.example.domain.model.user.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 用户Mapper接口
 */
@Mapper
public interface UserMapper {
    /**
     * 根据ID查询用户
     *
     * @param id 用户ID
     * @return 用户对象
     */
    User selectById(Long id);

    /**
     * 根据用户名查询用户
     *
     * @param username 用户名
     * @return 用户对象
     */
    User selectByUsername(String username);

    /**
     * 根据手机号查询用户
     *
     * @param phone 手机号
     * @return 用户对象
     */
    User selectByPhone(String phone);

    /**
     * 根据邮箱查询用户
     *
     * @param email 邮箱
     * @return 用户对象
     */
    User selectByEmail(String email);

    /**
     * 插入用户
     *
     * @param user 用户对象
     * @return 影响行数
     */
    int insert(User user);

    /**
     * 更新用户
     *
     * @param user 用户对象
     * @return 影响行数
     */
    int update(User user);

    /**
     * 删除用户
     *
     * @param id 用户ID
     * @return 影响行数
     */
    int deleteById(Long id);

    /**
     * 分页查询用户
     *
     * @param offset   偏移量
     * @param pageSize 每页大小
     * @return 用户列表
     */
    List<User> selectByPage(@Param("offset") int offset, @Param("pageSize") int pageSize);

    /**
     * 查询用户总数
     *
     * @return 用户总数
     */
    long count();
} 