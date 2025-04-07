package com.example.controller;

import com.example.model.Province;
import com.example.model.City;
import com.example.model.District;
import com.example.service.RegionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/regions")
public class RegionController {
    @Autowired
    private RegionService regionService;

    @GetMapping("/provinces")
    public List<Province> getProvinces() {
        return regionService.getProvinces();
    }

    @GetMapping("/cities")
    public List<City> getCitiesByProvinceCode(@RequestParam String provinceCode) {
        return regionService.getCitiesByProvinceCode(provinceCode);
    }

    @GetMapping("/districts")
    public List<District> getDistrictsByCityCode(@RequestParam String cityCode) {
        return regionService.getDistrictsByCityCode(cityCode);
    }
}