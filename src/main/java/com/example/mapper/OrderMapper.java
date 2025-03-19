package com.example.mapper;

import com.example.model.Order;
import com.example.model.OrderItem;
import com.example.util.OrderStatusTypeHandler;
import org.apache.ibatis.annotations.*;

import java.util.List;

import com.example.model.Order;
import org.apache.ibatis.annotations.Select;

public interface OrderMapper {
    // 定义常量
    String INSERT_ORDER_SQL = "INSERT INTO orders (user_id, product_ids, shipping_address, status, created_at) " +
            "VALUES (#{userId}, #{productIds,typeHandler=com.example.config.ListTypeHandler}, " +
            "#{shippingAddress}, #{status}, #{createdAt})";
    String UPDATE_ORDER_STATUS_SQL = "UPDATE orders SET status = #{status}, payment_method = #{paymentMethod}, " +
            "payment_amount = #{paymentAmount}, payment_time = #{paymentTime} " +
            "WHERE id = #{id}";
    String SELECT_ORDER_BY_ID_SQL = "SELECT * FROM orders WHERE id = #{orderId}";
    String SELECT_ORDERS_BY_USER_ID_AND_STATUS_SQL = "<script>" +
            "SELECT * FROM orders WHERE user_id = #{userId} " +
            "<when test='status != null'> AND status = #{status} </when>" +
            "ORDER BY create_time DESC" +
            "</script>";
    String SELECT_ORDER_ITEMS_BY_ORDER_ID_SQL = "SELECT * FROM order_item WHERE order_id = #{orderId}";
    String SELECT_ALL_ORDERS_SQL = "SELECT * FROM orders";
    String CANCEL_ORDER_SQL = "UPDATE orders SET status = -1 WHERE id = #{orderId}"; // 假设 -1 表示订单已取消

    @Insert(INSERT_ORDER_SQL)
    void insert(Order order);

    @Update(UPDATE_ORDER_STATUS_SQL)
    void updateOrderStatus(Order order);

    @Select(SELECT_ORDER_BY_ID_SQL)
    @Results({
            @Result(property = "status", column = "status"),
            @Result(property = "paymentTime", column = "payment_time"),
            @Result(property = "paymentMethod", column = "payment_method"),
            @Result(property = "paymentAmount", column = "payment_amount")
    })
    Order findById(Long orderId);

    @Select(SELECT_ORDERS_BY_USER_ID_AND_STATUS_SQL)
    @Results({
            @Result(property = "statusStr", column = "status",
                    typeHandler = OrderStatusTypeHandler.class),
            @Result(property = "paymentTime", column = "payment_time"),
            @Result(property = "paymentMethod", column = "payment_method"),
            @Result(property = "paymentAmount", column = "payment_amount"),
            @Result(property = "items", column = "id",
                    many = @Many(select = "findItemsByOrderId"))
    })
    List<Order> findByUserIdAndStatus(@Param("userId") Long userId,
                                      @Param("status") Integer status);

    @Select(SELECT_ORDER_ITEMS_BY_ORDER_ID_SQL)
    List<OrderItem> findItemsByOrderId(Long orderId);

    // 新增方法
    @Select(SELECT_ALL_ORDERS_SQL)
    @Results({
            @Result(property = "statusStr", column = "status",
                    typeHandler = OrderStatusTypeHandler.class),
            @Result(property = "paymentTime", column = "payment_time"),
            @Result(property = "paymentMethod", column = "payment_method"),
            @Result(property = "paymentAmount", column = "payment_amount"),
            @Result(property = "items", column = "id",
                    many = @Many(select = "findItemsByOrderId"))
    })
    List<Order> getAllOrders();

    @Update(CANCEL_ORDER_SQL)
    void cancelOrder(Long orderId);

    // 为了保持一致性，将 insert 方法重命名为 createOrder
    default void createOrder(Order order) {
        insert(order);
    }

    // 新增 getUserOrders 方法，与 findByUserIdAndStatus 功能类似
    @Select(SELECT_ORDERS_BY_USER_ID_AND_STATUS_SQL)
    @Results({
            @Result(property = "statusStr", column = "status",
                    typeHandler = OrderStatusTypeHandler.class),
            @Result(property = "paymentTime", column = "payment_time"),
            @Result(property = "paymentMethod", column = "payment_method"),
            @Result(property = "paymentAmount", column = "payment_amount"),
            @Result(property = "items", column = "id",
                    many = @Many(select = "findItemsByOrderId"))
    })
    List<Order> getUserOrders(@Param("userId") Long userId, @Param("status") Integer status);

    @Select("SELECT * FROM orders WHERE id = #{orderId}")
    @Results({
            @Result(property = "status", column = "status"),
            @Result(property = "paymentTime", column = "payment_time"),
            @Result(property = "paymentMethod", column = "payment_method"),
            @Result(property = "paymentAmount", column = "payment_amount"),
            @Result(property = "items", column = "id",
                    many = @Many(select = "findItemsByOrderId"))
    })
    Order getOrderById(Long orderId);
}