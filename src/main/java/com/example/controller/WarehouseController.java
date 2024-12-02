package com.example.controller;

import com.example.entity.Warehouse;
import com.example.service.WarehouseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// WarehouseController.java
@RestController
@RequestMapping("/api/warehouses")
public class WarehouseController {
    @Autowired
    private WarehouseService warehouseService;

    @GetMapping
    public List<Warehouse> getAllWarehouses() {
        return warehouseService.getAllWarehouses();
    }

    @GetMapping("/{id}")
    public Warehouse getWarehouseById(@PathVariable Long id) {
        return warehouseService.getWarehouseById(id);
    }

    @PostMapping("/{id}/inventory")
    public ResponseEntity<String> updateInventory(
            @PathVariable Long id,
            @RequestParam String productName,
            @RequestParam int quantity) {
        warehouseService.updateInventory(id, productName, quantity);
        return ResponseEntity.ok("Inventory updated successfully");
    }
}