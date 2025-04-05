package com.example.mapper;

import java.util.Date;
import java.util.List;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Many;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.example.model.Order;
import com.example.model.OrderItem;
import com.example.util.SpecificationsTypeHandler;

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
    

    @Update(CANCEL_ORDER_SQL)
    void cancelOrder(Long orderId);
    
    // 获取用户所有订单
    @Select("SELECT * FROM `order` WHERE user_id = #{userId} ORDER BY ${pageable.sort}")
    @Results({
            @Result(property = "status", column = "status"),
            @Result(property = "paymentTime", column = "payment_time"),
            @Result(property = "paymentMethod", column = "payment_method"),
            @Result(property = "paymentAmount", column = "payment_amount"),
            @Result(property = "items", column = "id",
                    many = @Many(select = "findItemsByOrderId"))
    })
    Page<Order> getUserOrders(@Param("userId") Long userId, @Param("pageable") Pageable pageable);

    @Select("SELECT * FROM `order` WHERE user_id = #{userId} AND status = #{status} ORDER BY ${pageable.sort}")
    @Results({
            @Result(property = "status", column = "status"),
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
            @Result(property = "id", column = "id"),
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
    @Select("SELECT * FROM `order` WHERE order_number = #{orderNumber}")
    @Results({
        @Result(property = "id", column = "id"),
        @Result(property = "status", column = "status"),
        @Result(property = "items", column = "id",
                many = @Many(select = "findItemsByOrderId"))
    })
    Order findByOrderNumber(String orderNumber);
    
    // 根据用户ID获取订单列表
    @Select("SELECT * FROM `order` WHERE user_id = #{userId} ORDER BY created_at DESC")
    @Results({
        @Result(property = "id", column = "id"),
        @Result(property = "status", column = "status"),
        @Result(property = "items", column = "id",
                many = @Many(select = "findItemsByOrderId"))
    })
    List<Order> getUserOrdersById(Long userId);
    
    // 根据用户ID和状态获取订单列表
    @Select("SELECT * FROM `order` WHERE user_id = #{userId} AND status = #{status} ORDER BY created_at DESC")
    @Results({
        @Result(property = "id", column = "id"),
        @Result(property = "status", column = "status"),
        @Result(property = "items", column = "id",
                many = @Many(select = "findItemsByOrderId"))
    })
    List<Order> getUserOrdersByIdAndStatus(@Param("userId") Long userId, @Param("status") Integer status);
    
    // 根据用户ID获取订单列表（分页）- 修改返回结果映射
    @Select("SELECT * FROM `order` WHERE user_id = #{userId} ORDER BY created_at DESC")
    @Results({
        @Result(property = "id", column = "id"),
        @Result(property = "status", column = "status"),
        @Result(property = "createdAt", column = "created_at"),
        @Result(property = "paymentTime", column = "payment_time"),
        @Result(property = "paymentMethod", column = "payment_method"),
        @Result(property = "paymentAmount", column = "payment_amount"),
        @Result(property = "totalAmount", column = "payment_amount"),
        @Result(property = "date", column = "created_at"),
        @Result(property = "items", column = "id",
                many = @Many(select = "findItemsByOrderId"))
    })
    List<Order> getUserOrdersByIdWithPage(@Param("userId") Long userId);
    
    // 根据用户ID和状态获取订单列表（分页）- 修改返回结果映射
    @Select("SELECT * FROM `order` WHERE user_id = #{userId} AND status = #{status} ORDER BY created_at DESC")
    @Results({
        @Result(property = "id", column = "id"),
        @Result(property = "paymentTime", column = "payment_time"),
        @Result(property = "paymentMethod", column = "payment_method"),
        @Result(property = "paymentAmount", column = "payment_amount"),
        @Result(property = "totalAmount", column = "payment_amount"),
        @Result(property = "date", column = "created_at"),
        @Result(property = "items", column = "id",
                many = @Many(select = "findItemsByOrderId"))
    })
    List<Order> getUserOrdersByIdAndStatusWithPage(@Param("userId") Long userId, @Param("status") Integer status);
    
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

    // 添加计数方法
    @Select("SELECT COUNT(*) FROM `order` WHERE user_id = #{userId}")
    int countOrdersByUserId(@Param("userId") Long userId);

    @Select("SELECT COUNT(*) FROM `order` WHERE user_id = #{userId} AND status = #{status}")
    int countOrdersByUserIdAndStatus(@Param("userId") Long userId, @Param("status") Integer status);

    // 修改订单项查询，关联产品表获取更多信息
    @Select("SELECT oi.*, p.title as name, p.imageUrl, " +
            "p.specifications as specs, p.shopName, p.shopAvatarUrl " +
            "FROM order_item oi " +
            "LEFT JOIN recommendations p ON oi.product_id = p.id " +
            "WHERE oi.order_id = #{orderId}")
    @Results({
        @Result(property = "id", column = "id"),
        @Result(property = "orderId", column = "order_id"),
        @Result(property = "shopName", column = "shopName"),
        @Result(property = "shopAvatarUrl", column = "shopAvatarUrl"),
        @Result(property = "productId", column = "product_id"),
        @Result(property = "quantity", column = "quantity"),
        @Result(property = "price", column = "price"),
        @Result(property = "subtotal", column = "subtotal"),
        @Result(property = "name", column = "name"),
        @Result(property = "imageUrl", column = "imageUrl"),
        @Result(property = "specs", column = "specs"),
        @Result(property = "specifications", column = "specifications", 
                typeHandler = SpecificationsTypeHandler.class)
    })
    List<OrderItem> findItemsByOrderId(Long orderId);
}