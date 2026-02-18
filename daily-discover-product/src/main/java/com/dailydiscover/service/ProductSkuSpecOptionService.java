package com.dailydiscover.service;

import com.dailydiscover.model.ProductSkuSpecOption;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * 商品规格选项服务接口
 */
public interface ProductSkuSpecOptionService extends IService<ProductSkuSpecOption> {
    
    /**
     * 根据规格ID查询选项
     * @param specId 规格ID
     * @return 选项列表
     */
    java.util.List<ProductSkuSpecOption> getOptionsBySpecId(Long specId);
    
    /**
     * 根据商品ID查询所有规格选项
     * @param productId 商品ID
     * @return 选项列表
     */
    java.util.List<ProductSkuSpecOption> getOptionsByProductId(Long productId);
    
    /**
     * 创建规格选项
     * @param specId 规格ID
     * @param optionValue 选项值
     * @param optionImage 选项图片
     * @return 创建的选项
     */
    ProductSkuSpecOption createOption(Long specId, String optionValue, String optionImage);
    
    /**
     * 更新选项排序
     * @param optionId 选项ID
     * @param sortOrder 排序顺序
     * @return 是否成功
     */
    boolean updateOptionSortOrder(Long optionId, Integer sortOrder);
}