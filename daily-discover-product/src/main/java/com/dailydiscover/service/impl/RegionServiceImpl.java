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
}