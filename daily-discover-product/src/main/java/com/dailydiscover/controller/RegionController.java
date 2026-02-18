package com.dailydiscover.controller;

import com.dailydiscover.common.annotation.ApiLog;
import com.dailydiscover.model.Region;
import com.dailydiscover.service.RegionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/regions")
@RequiredArgsConstructor
public class RegionController {

    private final RegionService regionService;

    @GetMapping
    @ApiLog("获取所有地区")
    public ResponseEntity<List<Region>> getAllRegions() {
        try {
            List<Region> regions = regionService.list();
            return ResponseEntity.ok(regions);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{id}")
    @ApiLog("根据ID获取地区")
    public ResponseEntity<Region> getRegionById(@PathVariable Long id) {
        try {
            Region region = regionService.getById(id);
            return region != null ? ResponseEntity.ok(region) : ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/code/{regionCode}")
    @ApiLog("根据地区代码获取地区")
    public ResponseEntity<Region> getRegionByCode(@PathVariable String regionCode) {
        try {
            Region region = regionService.getByRegionCode(regionCode);
            return region != null ? ResponseEntity.ok(region) : ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/parent/{parentId}")
    @ApiLog("根据父级地区ID获取子地区")
    public ResponseEntity<List<Region>> getRegionsByParentId(@PathVariable Long parentId) {
        try {
            List<Region> regions = regionService.getByParentId(parentId);
            return ResponseEntity.ok(regions);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/top-level")
    @ApiLog("查询顶级地区")
    public ResponseEntity<List<Region>> getTopLevelRegions() {
        try {
            List<Region> regions = regionService.getTopLevelRegions();
            return ResponseEntity.ok(regions);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/tree")
    @ApiLog("获取地区树结构")
    public ResponseEntity<List<Region>> getRegionTree() {
        try {
            List<Region> regions = regionService.getRegionTree();
            return ResponseEntity.ok(regions);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{id}/path")
    @ApiLog("获取完整的地区路径")
    public ResponseEntity<String> getFullRegionPath(@PathVariable Long id) {
        try {
            String path = regionService.getFullRegionPath(id);
            return ResponseEntity.ok(path);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping
    @ApiLog("创建地区")
    public ResponseEntity<Region> createRegion(@RequestBody Region region) {
        try {
            boolean success = regionService.save(region);
            return success ? ResponseEntity.ok(region) : ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PutMapping("/{regionId}")
    @ApiLog("更新地区")
    public ResponseEntity<Region> updateRegion(@PathVariable String regionId, @RequestBody Region region) {
        try {
            region.setRegionId(regionId);
            boolean success = regionService.updateById(region);
            return success ? ResponseEntity.ok(region) : ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @DeleteMapping("/{id}")
    @ApiLog("删除地区")
    public ResponseEntity<Void> deleteRegion(@PathVariable Long id) {
        try {
            boolean success = regionService.removeById(id);
            return success ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}