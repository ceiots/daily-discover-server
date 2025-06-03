package com.example.model;

// 定义一个新的类来封装订单和收货信息
public class OrderWithAddress {
    private Order order;
    private Address userAddress;

    public OrderWithAddress(Order order, Address userAddress) {
        this.order = order;
        this.userAddress = userAddress;
    }

    // Getters and Setters
    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    public Address getAddress() {
        return userAddress;
    }

    public void setAddress(Address userAddress) {
        this.userAddress = userAddress;
    }
}