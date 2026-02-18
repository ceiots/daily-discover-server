package com.dailydiscover.service;

import com.dailydiscover.model.ProductSkuSpec;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * 商品规格定义服务接口
 */
public interface ProductSkuSpecService extends IService<ProductSkuSpec> {
    
    /**
     * 根据商品ID查询规格定义
     * @param productId 商品ID
     * @return 规格定义列表
     */
    java.util.List<ProductSkuSpec> getSpecsByProductId(Long productId);
    
    /**
     * 创建商品规格定义
     * @param productId 商品ID
     * @param specName 规格名称
     * @param isRequired 是否必选
     * @return 创建的规格定义
     */
    ProductSkuSpec createSpec(Long productId, String specName, Boolean isRequired);
    
    /**
     * 更新规格排序
     * @param specId 规格ID
     * @param sortOrder 排序顺序
     * @return 是否成功
     */
    boolean updateSpecSortOrder(Long specId, Integer sortOrder);
}