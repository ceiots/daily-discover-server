package com.dailydiscover.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dailydiscover.mapper.ProductSkuSpecOptionMapper;
import com.dailydiscover.model.ProductSkuSpecOption;
import com.dailydiscover.service.ProductSkuSpecOptionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class ProductSkuSpecOptionServiceImpl extends ServiceImpl<ProductSkuSpecOptionMapper, ProductSkuSpecOption> implements ProductSkuSpecOptionService {
    
    @Autowired
    private ProductSkuSpecOptionMapper productSkuSpecOptionMapper;
    
    @Override
    public List<ProductSkuSpecOption> getOptionsBySpecId(Long specId) {
        return lambdaQuery().eq(ProductSkuSpecOption::getSpecId, specId).orderByAsc(ProductSkuSpecOption::getSortOrder).list();
    }
    
    @Override
    public ProductSkuSpecOption createOption(Long specId, String optionValue, String optionImage) {
        ProductSkuSpecOption option = new ProductSkuSpecOption();
        option.setSpecId(specId);
        option.setOptionValue(optionValue);
        option.setOptionImage(optionImage);
        
        save(option);
        return option;
    }
    
    @Override
    public boolean updateOptionSortOrder(Long optionId, Integer sortOrder) {
        ProductSkuSpecOption option = getById(optionId);
        if (option != null) {
            option.setSortOrder(sortOrder);
            return updateById(option);
        }
        return false;
    }
    
    @Override
    public List<ProductSkuSpecOption> getOptionsByProductId(Long productId) {
        // 通过规格定义获取选项
        return productSkuSpecOptionMapper.findByProductId(productId);
    }
}