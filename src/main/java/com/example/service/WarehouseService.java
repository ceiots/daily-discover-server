package com.example.service;

// WarehouseService.java
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.dao.InventoryDao;
import com.example.dao.WarehouseDao;
import com.example.entity.Inventory;
import com.example.entity.Warehouse;

import java.util.List;

@Service
public class WarehouseService {

    @Autowired
    private WarehouseDao warehouseDao;

    @Autowired
    private InventoryDao inventoryDao;

    public List<Warehouse> getAllWarehouses() {
        return warehouseDao.findAll();
    }

    public Warehouse getWarehouseById(Long id) {
        return warehouseDao.findById(id);
    }

    public void updateInventory(Long warehouseId, String productName, int quantity) {
        Inventory inventory = inventoryDao.findByWarehouseIdAndProductName(warehouseId, productName);
        if (inventory != null) {
            inventory.setQuantity(quantity);
            inventoryDao.save(inventory);
        } else {
            Inventory newInventory = new Inventory();
            newInventory.setWarehouse_id(warehouseId);
            newInventory.setProduct_name(productName);
            newInventory.setQuantity(quantity);
            inventoryDao.save(newInventory);
        }
    }
}