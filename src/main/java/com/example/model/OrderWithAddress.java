package com.example.model;

// 定义一个新的类来封装订单和收货信息
public class OrderWithAddress {
    private Order order;
    private OrderAddr address;

    public OrderWithAddress(Order order, OrderAddr address) {
        this.order = order;
        this.address = address;
    }

    // Getters and Setters
    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    public OrderAddr getAddress() {
        return address;
    }

    public void setAddress(OrderAddr address) {
        this.address = address;
    }
}