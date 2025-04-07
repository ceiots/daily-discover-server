package com.example.service;

import com.example.model.Province;
import com.example.model.City;
import com.example.model.District;
import com.example.mapper.RegionMapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RegionService  {
    @Autowired
    private RegionMapper regionMapper;

    public List<Province> getProvinces() {
        return regionMapper.getProvinces();
    }

    public List<City> getCitiesByProvinceCode(String provinceCode) {
        return regionMapper.getCitiesByProvinceCode(provinceCode);
    }

    public List<District> getDistrictsByCityCode(String cityCode) {
        return regionMapper.getDistrictsByCityCode(cityCode);
    }
}