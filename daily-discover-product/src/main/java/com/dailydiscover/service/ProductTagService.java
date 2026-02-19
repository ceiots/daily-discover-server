package com.dailydiscover.service;

import com.dailydiscover.model.ProductTag;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * 商品标签服务接口
 */
public interface ProductTagService extends IService<ProductTag> {
    
    /**
     * 根据标签名称查询标签
     * @param tagName 标签名称
     * @return 标签信息
     */
    ProductTag getByTagName(String tagName);
    
    /**
     * 获取热门标签
     * @param limit 限制数量
     * @return 热门标签列表
     */
    java.util.List<ProductTag> getPopularTags(int limit);
    
    /**
     * 创建标签
     * @param tagName 标签名称
     * @param tagType 标签类型
     * @return 创建的标签
     */
    ProductTag createTag(String tagName, String tagType);
    
    /**
     * 增加标签使用次数
     * @param tagId 标签ID
     * @return 是否成功
     */
    boolean incrementUsageCount(Long tagId);
    
    /**
     * 获取活跃标签
     * @return 活跃标签列表
     */
    java.util.List<ProductTag> getActiveTags();
    
    /**
     * 根据标签类型查询标签
     * @param tagType 标签类型
     * @return 标签列表
     */
    java.util.List<ProductTag> getTagsByType(String tagType);
    
    /**
     * 更新标签状态
     * @param tagId 标签ID
     * @param status 状态
     * @return 是否成功
     */
    boolean updateTagStatus(Long tagId, String status);
    
    /**
     * 更新标签排序
     * @param tagId 标签ID
     * @param tagOrder 排序值
     * @return 是否成功
     */
    boolean updateTagOrder(Long tagId, Integer tagOrder);
}