package com.example.mapper;

import com.example.model.Order;
import com.example.model.OrderItem;
import com.example.util.OrderStatusTypeHandler;
import org.apache.ibatis.annotations.*;
import java.util.List;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface OrderMapper {
    // 定义常量
    // 修改所有SQL语句中的order表名为`order`
    String UPDATE_ORDER_STATUS_SQL = "UPDATE `order` SET status = #{status}, payment_method = #{paymentMethod}, " +
            "payment_amount = #{paymentAmount}, payment_time = #{paymentTime} " +
            "WHERE id = #{id}";
    
    String SELECT_ORDER_BY_ID_SQL = "SELECT * FROM `order` WHERE id = #{orderId}";
    
    String SELECT_ORDERS_BY_USER_ID_AND_STATUS_SQL = "<script>" +
            "SELECT * FROM `order` WHERE user_id = #{userId} " +
            "<when test='status != null'> AND status = #{status} </when>" +
            "ORDER BY create_time DESC" +
            "</script>";
    String SELECT_ALL_ORDERS_SQL = "SELECT * FROM `order`";
    String CANCEL_ORDER_SQL = "UPDATE `order` SET status = -1 WHERE id = #{orderId}";
    
    // 修改insertOrder方法
    @Insert("INSERT INTO `order` (user_id, order_number, payment_amount, payment_method, status, created_at, order_addr_id) " +
            "VALUES (#{userId}, #{orderNumber}, #{paymentAmount}, #{paymentMethod}, #{status}, #{createdAt}, #{orderAddrId})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insertOrder(Order order);
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

    // 添加缺失的常量定义
    String SELECT_ORDER_ITEMS_BY_ORDER_ID_SQL = "SELECT * FROM order_item WHERE order_id = #{orderId}";
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
    // 新增 getUserOrders 方法
    @Select("SELECT * FROM order WHERE user_id = #{userId} ORDER BY create_time DESC")
    @Results({
            @Result(property = "statusStr", column = "status",
                    typeHandler = OrderStatusTypeHandler.class),
            @Result(property = "paymentTime", column = "payment_time"),
            @Result(property = "paymentMethod", column = "payment_method"),
            @Result(property = "paymentAmount", column = "payment_amount"),
            @Result(property = "items", column = "id",
                    many = @Many(select = "findItemsByOrderId"))
    })
    List<Order> getUserOrders(@Param("userId") Long userId);

    // 新增 getUserOrdersByStatus 方法
    @Select("SELECT * FROM order WHERE user_id = #{userId} AND status = #{status} ORDER BY create_time DESC")
    @Results({
            @Result(property = "statusStr", column = "status",
                    typeHandler = OrderStatusTypeHandler.class),
            @Result(property = "paymentTime", column = "payment_time"),
            @Result(property = "paymentMethod", column = "payment_method"),
            @Result(property = "paymentAmount", column = "payment_amount"),
            @Result(property = "items", column = "id",
                    many = @Many(select = "findItemsByOrderId"))
    })
    List<Order> getUserOrdersByStatus(@Param("userId") Long userId, @Param("status") Integer status);

    @Select("SELECT * FROM order WHERE id = #{orderId}")
    @Results({
            @Result(property = "status", column = "status"),
            @Result(property = "paymentTime", column = "payment_time"),
            @Result(property = "paymentMethod", column = "payment_method"),
            @Result(property = "paymentAmount", column = "payment_amount"),
            @Result(property = "items", column = "id",
                    many = @Many(select = "findItemsByOrderId"))
    })
    Order getOrderById(Long orderId);
    
    // 新增插入订单商品项的方法
    @Insert("INSERT INTO order_item (order_id, product_id, quantity, price, subtotal) VALUES (#{orderId}, #{productId}, #{quantity}, #{price}, #{subtotal})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insertOrderItem(OrderItem orderItem);
    
    // 根据订单 ID 查询订单商品项
    @Select("SELECT * FROM order_item WHERE order_id = #{orderId}")
    @Results({
        @Result(property = "id", column = "id"),
        @Result(property = "orderId", column = "order_id"),
        @Result(property = "productId", column = "product_id"),
        @Result(property = "quantity", column = "quantity"),
        @Result(property = "price", column = "price"),
        @Result(property = "subtotal", column = "subtotal")
    })
    List<OrderItem> findOrderItemsByOrderId(Long orderId);
    
    // 新增根据订单ID和状态更新订单状态的方法
    @Update("UPDATE order SET status = #{status} WHERE id = #{orderId}")
    void updateOrderStatus(Long orderId, Integer status);
    
    @Select("SELECT * FROM `order` WHERE order_number = #{orderNo}")
    @Results({
        @Result(property = "statusStr", column = "status", 
                typeHandler = OrderStatusTypeHandler.class),
        @Result(property = "items", column = "id",
                many = @Many(select = "findItemsByOrderId"))
    })
    Order findByOrderNo(String orderNo);
}    