package com.dailydiscover.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dailydiscover.mapper.RegionMapper;
import com.dailydiscover.model.Region;
import com.dailydiscover.service.RegionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@Slf4j
public class RegionServiceImpl extends ServiceImpl<RegionMapper, Region> implements RegionService {
    
    @Autowired
    private RegionMapper regionMapper;
    
    @Override
    public Region getByRegionCode(String regionCode) {
        return regionMapper.findByRegionCode(regionCode);
    }
    
    @Override
    public List<Region> searchRegions(String keyword) {
        return regionMapper.findByNameLike(keyword);
    }
    
    @Override
    public java.util.List<Region> getByParentId(String parentId) {
        return regionMapper.findByParentId(parentId);
    }
    
    @Override
    public java.util.List<Region> getTopLevelRegions() {
        return regionMapper.findTopLevelRegions();
    }
    
    @Override
    public java.util.List<Region> getByNameLike(String name) {
        return regionMapper.findByNameLike(name);
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