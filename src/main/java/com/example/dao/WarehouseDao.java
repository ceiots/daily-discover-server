package com.example.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import com.example.entity.Warehouse;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
public class WarehouseDao {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private InventoryDao inventoryDao;

    public List<Warehouse> findAll() {
        String sql = "SELECT * FROM warehouses";
        List<Warehouse> warehouses = jdbcTemplate.query(sql, new WarehouseRowMapper());
        for (Warehouse warehouse : warehouses) {
            warehouse.setInventories(inventoryDao.findByWarehouseId(warehouse.getId()));
        }
        return warehouses;
    }

    public Warehouse findById(Long id) {
        String sql = "SELECT * FROM warehouses WHERE id = ?";
        Warehouse warehouse = jdbcTemplate.queryForObject(sql, new Object[] { id }, new WarehouseRowMapper());
        if (warehouse != null) {
            warehouse.setInventories(inventoryDao.findByWarehouseId(warehouse.getId()));
        }
        return warehouse;
    }

    private static class WarehouseRowMapper implements RowMapper<Warehouse> {
        @Override
        public Warehouse mapRow(ResultSet rs, int rowNum) throws SQLException {
            Warehouse warehouse = new Warehouse();
            warehouse.setId(rs.getLong("id"));
            warehouse.setName(rs.getString("name"));
            warehouse.setLocation(rs.getString("location"));
            warehouse.setLatitude(rs.getDouble("latitude"));
            warehouse.setLongitude(rs.getDouble("longitude"));
            warehouse.setDelivery_time(rs.getInt("delivery_time"));
            return warehouse;
        }
    }
}
