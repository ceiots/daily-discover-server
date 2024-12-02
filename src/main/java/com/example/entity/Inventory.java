package com.example.entity;

public class Inventory {
    private Long id;
    private Long warehouse_id;
    private String product_name;
    private Integer quantity;

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getWarehouse_id() {
        return this.warehouse_id;
    }

    public void setWarehouse_id(Long warehouse_id) {
        this.warehouse_id = warehouse_id;
    }

    public String getProduct_name() {
        return this.product_name;
    }

    public void setProduct_name(String product_name) {
        this.product_name = product_name;
    }

    public Integer getQuantity() {
        return this.quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    @Override
    public String toString() {
        return "{" +
                " id='" + getId() + "'" +
                ", warehouse_id='" + getWarehouse_id() + "'" +
                ", product_name='" + getProduct_name() + "'" +
                ", quantity='" + getQuantity() + "'" +
                "}";
    }

}