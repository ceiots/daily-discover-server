package com.dailydiscover.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dailydiscover.mapper.RegionMapper;
import com.dailydiscover.model.Region;
import com.dailydiscover.service.RegionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class RegionServiceImpl extends ServiceImpl<RegionMapper, Region> implements RegionService {
    
    @Autowired
    private RegionMapper regionMapper;
    
    @Override
    public Region getByRegionCode(String regionCode) {
        return lambdaQuery().eq(Region::getRegionCode, regionCode).one();
    }
    
    @Override
    public List<Region> searchRegions(String keyword) {
        return lambdaQuery()
                .like(Region::getRegionName, keyword)
                .or()
                .like(Region::getRegionCode, keyword)
                .orderByAsc(Region::getRegionLevel)
                .orderByAsc(Region::getRegionCode)
                .list();
    }
    
    @Override
    public java.util.List<Region> getByParentId(String parentId) {
        return lambdaQuery().eq(Region::getRegionParentId, parentId).orderByAsc(Region::getRegionCode).list();
    }
    
    @Override
    public java.util.List<Region> getTopLevelRegions() {
        return lambdaQuery().isNull(Region::getRegionParentId).or().eq(Region::getRegionParentId, "0").orderByAsc(Region::getRegionCode).list();
    }
    
    @Override
    public java.util.List<Region> getByNameLike(String name) {
        return lambdaQuery().like(Region::getRegionName, name).orderByAsc(Region::getRegionCode).list();
    }
    
    @Override
    public java.util.List<Region> getRegionTree() {
        // 获取所有地区并按层级和地区代码排序
        return regionMapper.findAllRegions();
    }
    
    @Override
    public String getFullRegionPath(String regionId) {
        Region region = getById(regionId);
        if (region == null) {
            return "";
        }
        
        StringBuilder path = new StringBuilder(region.getRegionName());
        
        // 递归获取父级区域
        String parentId = region.getRegionParentId();
        while (parentId != null && !parentId.isEmpty() && !"0".equals(parentId)) {
            Region parent = getById(parentId);
            if (parent != null) {
                path.insert(0, parent.getRegionName() + " ");
                parentId = parent.getRegionParentId();
            } else {
                break;
            }
        }
        
        return path.toString();
    }
}