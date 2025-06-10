package com.example.user.domain.repository;

import com.example.user.domain.model.member.MemberLevel;

import java.util.List;
import java.util.Optional;

/**
 * 会员等级仓储接口
 */
public interface MemberLevelRepository {
    /**
     * 根据ID查询会员等级
     *
     * @param id 会员等级ID
     * @return 会员等级
     */
    Optional<MemberLevel> findById(Long id);

    /**
     * 根据等级查询会员等级
     *
     * @param level 等级
     * @return 会员等级
     */
    Optional<MemberLevel> findByLevel(Integer level);

    /**
     * 根据成长值查询对应的会员等级
     *
     * @param growthValue 成长值
     * @return 会员等级
     */
    Optional<MemberLevel> findByGrowthValue(Integer growthValue);

    /**
     * 查询所有会员等级
     *
     * @return 会员等级列表
     */
    List<MemberLevel> findAll();

    /**
     * 查询启用的会员等级
     *
     * @return 会员等级列表
     */
    List<MemberLevel> findAllEnabled();

    /**
     * 保存会员等级
     *
     * @param memberLevel 会员等级
     * @return 保存后的会员等级
     */
    MemberLevel save(MemberLevel memberLevel);

    /**
     * 更新会员等级
     *
     * @param memberLevel 会员等级
     * @return 更新后的会员等级
     */
    MemberLevel update(MemberLevel memberLevel);

    /**
     * 删除会员等级
     *
     * @param id 会员等级ID
     * @return 是否删除成功
     */
    boolean delete(Long id);

    /**
     * 更新会员等级状态
     *
     * @param id 会员等级ID
     * @param status 状态
     * @return 是否更新成功
     */
    boolean updateStatus(Long id, Integer status);
    
    /**
     * 检查等级是否存在
     *
     * @param level 等级
     * @return 是否存在
     */
    boolean existsByLevel(Integer level);
} 