package com.example.entity;

import java.util.List;

public class Warehouse {

    private Long id;
    private String name;
    private String location;
    private Double latitude;
    private Double longitude;
    private Integer delivery_time;
    private List<Inventory> inventories;

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLocation() {
        return this.location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Double getLatitude() {
        return this.latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return this.longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public Integer getDelivery_time() {
        return this.delivery_time;
    }

    public void setDelivery_time(Integer delivery_time) {
        this.delivery_time = delivery_time;
    }

    public List<Inventory> getInventories() {
        return this.inventories;
    }

    public void setInventories(List<Inventory> inventories) {
        this.inventories = inventories;
    }

    @Override
    public String toString() {
        return "{" +
                " id='" + getId() + "'" +
                ", name='" + getName() + "'" +
                ", location='" + getLocation() + "'" +
                ", latitude='" + getLatitude() + "'" +
                ", longitude='" + getLongitude() + "'" +
                ", delivery_time='" + getDelivery_time() + "'" +
                ", inventories='" + getInventories() + "'" +
                "}";
    }

}