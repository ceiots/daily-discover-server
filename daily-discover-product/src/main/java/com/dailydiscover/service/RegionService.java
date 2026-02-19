package com.dailydiscover.service;

import com.dailydiscover.model.Region;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * 地区服务接口
 */
public interface RegionService extends IService<Region> {
    
    /**
     * 根据地区代码查询地区
     */
    Region getByRegionCode(String regionCode);
    
    /**
     * 根据父级地区ID查询子地区
     */
    java.util.List<Region> getByParentId(String parentId);
    
    /**
     * 查询顶级地区
     */
    java.util.List<Region> getTopLevelRegions();
    
    /**
     * 根据地区名称模糊查询
     */
    java.util.List<Region> getByNameLike(String name);
    
    /**
     * 获取地区树结构
     */
    java.util.List<Region> getRegionTree();
    
    /**
     * 获取完整的地区路径
     */
    String getFullRegionPath(String regionId);
    
    /**
     * 搜索地区
     */
    java.util.List<Region> searchRegions(String keyword);
}