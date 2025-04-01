package com.example.mapper;

import com.example.model.Order;
import com.example.model.OrderItem;
import com.example.util.OrderStatusTypeHandler;
import org.apache.ibatis.annotations.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.example.util.SpecificationsTypeHandler;
import java.util.Date;
import java.util.List;

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
            "ORDER BY created_at DESC" +
            "</script>";
    
    String SELECT_ALL_ORDERS_SQL = "SELECT * FROM `order`";
    String CANCEL_ORDER_SQL = "UPDATE `order` SET status = -1 WHERE id = #{orderId}";
    
    // 修改insertOrder方法
    @Insert("INSERT INTO `order` (user_id, order_number, payment_amount, payment_method, payment_time, status, created_at, order_addr_id) " +
            "VALUES (#{userId}, #{orderNumber}, #{paymentAmount}, #{paymentMethod}, #{paymentTime}, #{status}, #{createdAt}, #{orderAddrId})")
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

    // 添加缺失的常量定义
    String SELECT_ORDER_ITEMS_BY_ORDER_ID_SQL = "SELECT * FROM order_item WHERE order_id = #{orderId}";
    
    @Select(SELECT_ORDER_ITEMS_BY_ORDER_ID_SQL)
    @Results({
        @Result(property = "id", column = "id"),
        @Result(property = "orderId", column = "order_id"),
        @Result(property = "productId", column = "product_id"),
        @Result(property = "quantity", column = "quantity"),
        @Result(property = "price", column = "price"),
        @Result(property = "subtotal", column = "subtotal"),
        @Result(property = "specifications", column = "specifications", 
                typeHandler = SpecificationsTypeHandler.class)
    })
    List<OrderItem> findItemsByOrderId(Long orderId);

    // 获取所有订单
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
    
    // 获取用户所有订单
    @Select("SELECT * FROM `order` WHERE user_id = #{userId} ORDER BY ${pageable.sort}")
    @Results({
            @Result(property = "statusStr", column = "status",
                    typeHandler = OrderStatusTypeHandler.class),
            @Result(property = "paymentTime", column = "payment_time"),
            @Result(property = "paymentMethod", column = "payment_method"),
            @Result(property = "paymentAmount", column = "payment_amount"),
            @Result(property = "items", column = "id",
                    many = @Many(select = "findItemsByOrderId"))
    })
    Page<Order> getUserOrders(@Param("userId") Long userId, @Param("pageable") Pageable pageable);

    @Select("SELECT * FROM `order` WHERE user_id = #{userId} AND status = #{status} ORDER BY ${pageable.sort}")
    @Results({
            @Result(property = "statusStr", column = "status",
                    typeHandler = OrderStatusTypeHandler.class),
            @Result(property = "paymentTime", column = "payment_time"),
            @Result(property = "paymentMethod", column = "payment_method"),
            @Result(property = "paymentAmount", column = "payment_amount"),
            @Result(property = "items", column = "id",
                    many = @Many(select = "findItemsByOrderId"))
    })
    Page<Order> getUserOrdersByStatus(@Param("userId") Long userId, @Param("status") Integer status, @Param("pageable") Pageable pageable);

    // 根据ID获取订单
    @Select("SELECT * FROM `order` WHERE id = #{orderId}")
    @Results({
            @Result(property = "status", column = "status"),
            @Result(property = "paymentTime", column = "payment_time"),
            @Result(property = "paymentMethod", column = "payment_method"),
            @Result(property = "paymentAmount", column = "payment_amount"),
            @Result(property = "items", column = "id",
                    many = @Many(select = "findItemsByOrderId"))
    })
    Order getOrderById(Long orderId);
    
    // 插入订单商品项
    @Insert("INSERT INTO order_item (order_id, product_id, quantity, price, subtotal) VALUES (#{orderId}, #{productId}, #{quantity}, #{price}, #{subtotal})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insertOrderItem(OrderItem orderItem);
    
    // 根据订单ID查询订单商品项
    @Select("SELECT * FROM order_item WHERE order_id = #{orderId}")
    @Results({
        @Result(property = "id", column = "id"),
        @Result(property = "orderId", column = "order_id"),
        @Result(property = "productId", column = "product_id"),
        @Result(property = "quantity", column = "quantity"),
        @Result(property = "price", column = "price"),
        @Result(property = "subtotal", column = "subtotal")
    })
    List<OrderItem> findOrderItemsByOrderId(@Param("orderId") Long orderId);
    
    // 更新订单状态
    @Update("UPDATE `order` SET status = #{status} WHERE id = #{orderId}")
    void updateOrderStatus(@Param("orderId") Long orderId, @Param("status") Integer status);
    
    // 根据订单号查询订单
    @Select("SELECT * FROM `order` WHERE order_number = #{orderNo}")
    @Results({
        @Result(property = "id", column = "id"),
        @Result(property = "statusStr", column = "status", 
                typeHandler = OrderStatusTypeHandler.class),
        @Result(property = "items", column = "id",
                many = @Many(select = "findItemsByOrderId"))
    })
    Order findByOrderNo(String orderNo);
    
    // 更新支付时间
    @Update("UPDATE `order` SET payment_time = #{paymentTime} WHERE id = #{orderId}")
    void updatePaymentTime(@Param("orderId") Long orderId, @Param("paymentTime") Date paymentTime);

    // 更新支付方式
    @Update("UPDATE `order` SET payment_method = #{paymentMethod} WHERE id = #{orderId}")
    void updatePaymentMethod(@Param("orderId") Long orderId, @Param("paymentMethod") String paymentMethod);
    
    /**
     * 取消订单
     * @param orderId 订单ID
     * @param userId 用户ID（用于验证权限）
     * @param status 更新的状态值
     * @return 影响的行数
     */
    @Update("UPDATE `order` SET status = #{status} WHERE id = #{orderId} AND user_id = #{userId}")
    int cancelOrder(@Param("orderId") Long orderId, @Param("userId") Long userId, @Param("status") Integer status);
}