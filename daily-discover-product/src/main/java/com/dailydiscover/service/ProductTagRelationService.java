package com.dailydiscover.service;

import com.dailydiscover.model.ProductTagRelation;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * 商品标签关联服务接口
 */
public interface ProductTagRelationService extends IService<ProductTagRelation> {
    
    /**
     * 根据商品ID查询标签关联
     * @param productId 商品ID
     * @return 标签关联列表
     */
    java.util.List<ProductTagRelation> getRelationsByProductId(Long productId);
    
    /**
     * 根据标签ID查询商品关联
     * @param tagId 标签ID
     * @return 商品关联列表
     */
    java.util.List<ProductTagRelation> getRelationsByTagId(Long tagId);
    
    /**
     * 为商品添加标签
     * @param productId 商品ID
     * @param tagId 标签ID
     * @return 是否成功
     */
    boolean addTagToProduct(Long productId, Long tagId);
    
    /**
     * 移除商品的标签
     * @param productId 商品ID
     * @param tagId 标签ID
     * @return 是否成功
     */
    boolean removeTagFromProduct(Long productId, Long tagId);
    
    /**
     * 批量添加标签到商品
     * @param productId 商品ID
     * @param tagIds 标签ID列表
     * @return 是否成功
     */
    boolean batchAddTagsToProduct(Long productId, java.util.List<Long> tagIds);
    
    /**
     * 根据商品ID和标签ID删除标签关系
     * @param productId 商品ID
     * @param tagId 标签ID
     * @return 是否成功
     */
    boolean removeByProductIdAndTagId(Long productId, Long tagId);
}