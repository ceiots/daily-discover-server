package com.dailydiscover.mapper;

import com.dailydiscover.model.Region;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 地区表 Mapper
 */
@Mapper
public interface RegionMapper extends BaseMapper<Region> {
    
    /**
     * 根据父级地区ID查询子地区
     */
    @Select("SELECT * FROM regions WHERE region_parent_id = #{parentId} ORDER BY region_code ASC")
    List<Region> findByParentId(@Param("parentId") String parentId);
    
    /**
     * 查询顶级地区（省份）
     */
    @Select("SELECT * FROM regions WHERE region_parent_id IS NULL OR region_parent_id = '0' ORDER BY region_code ASC")
    List<Region> findTopLevelRegions();
    
    /**
     * 根据地区代码查询
     */
    @Select("SELECT * FROM regions WHERE region_code = #{regionCode}")
    Region findByRegionCode(@Param("regionCode") String regionCode);
    
    /**
     * 根据地区名称模糊查询
     */
    @Select("SELECT * FROM regions WHERE region_name LIKE CONCAT('%', #{name}, '%') ORDER BY region_code ASC")
    List<Region> findByNameLike(@Param("name") String name);
    
    /**
     * 查询所有地区
     */
    @Select("SELECT * FROM regions ORDER BY region_level ASC, region_code ASC")
    List<Region> findAllRegions();
}