package com.example.user.domain.repository;

import com.example.common.model.PageRequest;
import com.example.common.model.PageResult;
import com.example.user.domain.model.id.UserId;
import com.example.user.domain.model.user.User;
import com.example.user.domain.model.valueobject.Email;
import com.example.user.domain.model.valueobject.Mobile;

import java.util.List;
import java.util.Optional;

/**
 * 用户仓储接口
 */
public interface UserRepository {
    /**
     * 根据ID查询用户
     *
     * @param userId 用户ID
     * @return 用户
     */
    Optional<User> findById(UserId userId);

    /**
     * 根据用户名查询用户
     *
     * @param username 用户名
     * @return 用户
     */
    Optional<User> findByUsername(String username);

    /**
     * 根据手机号查询用户
     *
     * @param mobile 手机号
     * @return 用户
     */
    Optional<User> findByMobile(Mobile mobile);

    /**
     * 根据邮箱查询用户
     *
     * @param email 邮箱
     * @return 用户
     */
    Optional<User> findByEmail(Email email);

    /**
     * 保存用户
     *
     * @param user 用户
     * @return 保存后的用户
     */
    User save(User user);

    /**
     * 更新用户
     *
     * @param user 用户
     * @return 更新后的用户
     */
    User update(User user);

    /**
     * 删除用户
     *
     * @param userId 用户ID
     * @return 是否删除成功
     */
    boolean delete(UserId userId);

    /**
     * 批量删除用户
     *
     * @param userIds 用户ID列表
     * @return 是否删除成功
     */
    boolean deleteBatch(List<UserId> userIds);

    /**
     * 更新用户状态
     *
     * @param userId 用户ID
     * @param status 状态
     * @return 是否更新成功
     */
    boolean updateStatus(UserId userId, Integer status);
    
    /**
     * 分页查询用户
     *
     * @param pageRequest 分页请求参数
     * @param condition 查询条件
     * @return 用户分页结果
     */
    PageResult<User> findPage(PageRequest pageRequest, UserQueryCondition condition);
    
    /**
     * 查询用户列表
     *
     * @param condition 查询条件
     * @return 用户列表
     */
    List<User> findList(UserQueryCondition condition);
    
    /**
     * 根据ID列表查询用户
     *
     * @param userIds 用户ID列表
     * @return 用户列表
     */
    List<User> findByIds(List<UserId> userIds);
    
    /**
     * 检查用户名是否存在
     *
     * @param username 用户名
     * @return 是否存在
     */
    boolean existsByUsername(String username);
    
    /**
     * 检查手机号是否存在
     *
     * @param mobile 手机号
     * @return 是否存在
     */
    boolean existsByMobile(Mobile mobile);
    
    /**
     * 检查邮箱是否存在
     *
     * @param email 邮箱
     * @return 是否存在
     */
    boolean existsByEmail(Email email);
} 