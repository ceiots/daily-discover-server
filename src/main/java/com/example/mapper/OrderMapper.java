package com.example.mapper;

import java.util.Date;
import java.util.List;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Many;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.One;
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
    String UPDATE_ORDER_STATUS_SQL = "UPDATE `order` SET status = #{status} WHERE id = #{id}";
    
    String SELECT_ORDER_BY_ID_SQL = "SELECT * FROM `order` WHERE id = #{orderId}";
    
    String SELECT_ORDERS_BY_USER_ID_AND_STATUS_SQL = "<script>" +
            "SELECT * FROM `order` WHERE user_id = #{userId} " +
            "<when test='status != null'> AND status = #{status} </when>" +
            "ORDER BY created_at DESC" +
            "</script>";
    
    String SELECT_ALL_ORDERS_SQL = "SELECT * FROM `order`";
    String CANCEL_ORDER_SQL = "UPDATE `order` SET status = -1 WHERE id = #{orderId}";
    
    // 修改insertOrder方法
    @Insert("INSERT INTO `order` (user_id, order_number, payment_amount, payment_method, payment_time, status, created_at, address_id) " +
            "VALUES (#{userId}, #{orderNumber}, #{paymentAmount}, #{paymentMethod}, #{paymentTime}, #{status}, #{createdAt}, #{addressId})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insertOrder(Order order);
    

    @Select(SELECT_ORDER_BY_ID_SQL)
    @Results({
            @Result(property = "status", column = "status"),
            @Result(property = "paymentTime", column = "payment_time"),
            @Result(property = "paymentMethod", column = "payment_method"),
            @Result(property = "paymentAmount", column = "payment_amount")
    })
    Order findById(Long orderId);

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
    @Update("UPDATE `order` SET status = #{status} WHERE order_number = #{orderNumber}")
    void updateOrderStatus(@Param("orderNumber") String orderNumber, @Param("status") Integer status);
    
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

    // 修改订单项查询，关联产品表、SKU表和店铺表获取更多信息
    @Select("SELECT oi.*, p.title as product_name, ps.image_url as sku_image, " +
            "ps.specifications, s.shop_name as shopName, s.shop_logo as shopAvatarUrl " +
            "FROM order_item oi " +
            "LEFT JOIN product p ON oi.product_id = p.id " +
            "LEFT JOIN product_sku ps ON oi.sku_id = ps.id " +
            "LEFT JOIN shop s ON oi.shop_id = s.id " + 
            "WHERE oi.order_id = #{orderId}")
    @Results({
        @Result(property = "id", column = "id"),
        @Result(property = "orderId", column = "order_id"),
        @Result(property = "shopId", column = "shop_id"),
        @Result(property = "shopName", column = "shopName"),
        @Result(property = "shopAvatarUrl", column = "shopAvatarUrl"),
        @Result(property = "productId", column = "product_id"),
        @Result(property = "skuId", column = "sku_id"),
        @Result(property = "skuImage", column = "sku_image"),
        @Result(property = "quantity", column = "quantity"),
        @Result(property = "price", column = "price"),
        @Result(property = "subtotal", column = "subtotal"),
        @Result(property = "product_name", column = "product_name"),
        @Result(property = "specifications", column = "specifications", 
                typeHandler = SpecificationsTypeHandler.class),
        @Result(property = "shop", column = "shop_id",
                one = @One(select = "com.example.mapper.ShopMapper.findById"))
    })
    List<OrderItem> findItemsByOrderId(Long orderId);
    
    /**
     * 更新订单信息
     * @param order 订单对象
     */
    @Update("UPDATE `order` SET " +
            "payment_method = #{paymentMethod}, " +
            "payment_time = #{paymentTime}, " +
            "status = #{status} " +
            "WHERE id = #{id}")
    void updateOrder(Order order);
}