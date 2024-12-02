package com.example.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import com.example.entity.Inventory;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
public class InventoryDao {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public List<Inventory> findByWarehouseId(Long warehouseId) {
        String sql = "SELECT * FROM inventory WHERE warehouse_id = ?";
        return jdbcTemplate.query(sql, new Object[] { warehouseId }, new InventoryRowMapper());
    }

    public Inventory findByWarehouseIdAndProductName(Long warehouseId, String productName) {
        String sql = "SELECT * FROM inventory WHERE warehouse_id = ? AND product_name = ?";
        List<Inventory> result = jdbcTemplate.query(sql, new Object[] { warehouseId, productName },
                new InventoryRowMapper());
        return result.isEmpty() ? null : result.get(0);
    }

    public void save(Inventory inventory) {
        if (inventory.getId() == null) {
            String sql = "INSERT INTO inventory (warehouse_id, product_name, quantity) VALUES (?, ?, ?)";
            jdbcTemplate.update(sql, inventory.getWarehouse_id(), inventory.getProduct_name(), inventory.getQuantity());
        } else {
            String sql = "UPDATE inventory SET quantity = ? WHERE id = ?";
            jdbcTemplate.update(sql, inventory.getQuantity(), inventory.getId());
        }
    }

    private static class InventoryRowMapper implements RowMapper<Inventory> {
        @Override
        public Inventory mapRow(ResultSet rs, int rowNum) throws SQLException {
            Inventory inventory = new Inventory();
            inventory.setId(rs.getLong("id"));
            inventory.setWarehouse_id(rs.getLong("warehouse_id"));
            inventory.setProduct_name(rs.getString("product_name"));
            inventory.setQuantity(rs.getInt("quantity"));
            return inventory;
        }
    }
}