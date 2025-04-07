package com.example.mapper;

import com.example.model.Province;
import com.example.model.City;
import com.example.model.District;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface RegionMapper {
    @Select("SELECT * FROM provinces")
    List<Province> getProvinces();

    @Select("SELECT * FROM cities WHERE province_code = #{provinceCode}")
    List<City> getCitiesByProvinceCode(String provinceCode);

    @Select("SELECT * FROM districts WHERE city_code = #{cityCode}")
    List<District> getDistrictsByCityCode(String cityCode);
}