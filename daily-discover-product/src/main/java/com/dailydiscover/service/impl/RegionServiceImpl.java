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
    public List<Region> getProvinces() {
        return lambdaQuery().eq(Region::getLevel, 1).orderByAsc(Region::getRegionCode).list();
    }
    
    @Override
    public List<Region> getCitiesByProvinceCode(String provinceCode) {
        return lambdaQuery().eq(Region::getParentCode, provinceCode).eq(Region::getLevel, 2).orderByAsc(Region::getRegionCode).list();
    }
    
    @Override
    public List<Region> getDistrictsByCityCode(String cityCode) {
        return lambdaQuery().eq(Region::getParentCode, cityCode).eq(Region::getLevel, 3).orderByAsc(Region::getRegionCode).list();
    }
    
    @Override
    public Region getByRegionCode(String regionCode) {
        return lambdaQuery().eq(Region::getRegionCode, regionCode).one();
    }
    
    @Override
    public Region getByRegionName(String regionName) {
        return lambdaQuery().eq(Region::getRegionName, regionName).one();
    }
    
    @Override
    public String getFullRegionPath(String regionCode) {
        Region region = getByRegionCode(regionCode);
        if (region == null) {
            return "";
        }
        
        StringBuilder path = new StringBuilder(region.getRegionName());
        
        // 递归获取父级区域
        String parentCode = region.getParentCode();
        while (parentCode != null && !parentCode.isEmpty()) {
            Region parent = getByRegionCode(parentCode);
            if (parent != null) {
                path.insert(0, parent.getRegionName() + " ");
                parentCode = parent.getParentCode();
            } else {
                break;
            }
        }
        
        return path.toString();
    }
    
    @Override
    public List<Region> searchRegions(String keyword) {
        return lambdaQuery()
                .like(Region::getRegionName, keyword)
                .or()
                .like(Region::getRegionCode, keyword)
                .orderByAsc(Region::getLevel)
                .orderByAsc(Region::getRegionCode)
                .list();
    }
    
    @Override
    public java.util.List<Region> getByParentId(Long parentId) {
        return lambdaQuery().eq(Region::getRegionParentId, String.valueOf(parentId)).orderByAsc(Region::getRegionCode).list();
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
        // 获取所有地区并按层级排序
        List<Region> allRegions = lambdaQuery().orderByAsc(Region::getLevel).orderByAsc(Region::getRegionCode).list();
        
        // 构建树形结构
        Map<Long, Region> regionMap = new HashMap<>();
        List<Region> rootRegions = new ArrayList<>();
        
        for (Region region : allRegions) {
            regionMap.put(region.getId(), region);
        }
        
        for (Region region : allRegions) {
            String parentId = region.getRegionParentId();
            if (parentId == null || parentId.isEmpty() || "0".equals(parentId)) {
                rootRegions.add(region);
            } else {
                // Since regionParentId is String, we need to find parent by regionId
                Region parent = regionMap.values().stream()
                    .filter(r -> r.getRegionId().equals(parentId))
                    .findFirst()
                    .orElse(null);
                if (parent != null) {
                    if (parent.getChildren() == null) {
                        parent.setChildren(new ArrayList<>());
                    }
                    parent.getChildren().add(region);
                }
            }
        }
        
        return rootRegions;
    }
    
    @Override
    public String getFullRegionPath(Long regionId) {
        Region region = getById(String.valueOf(regionId));
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