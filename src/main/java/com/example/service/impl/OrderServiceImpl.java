package com.example.service.impl;

import com.example.service.OrderService;
import com.example.service.OrderSettlementService;

import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.List;
import java.util.ArrayList;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Date;
import java.text.SimpleDateFormat;

import com.example.mapper.AddressMapper;
import java.util.Date;
import java.text.SimpleDateFormat;

import com.example.mapper.OrderItemMapper;
import com.example.mapper.OrderMapper;
import com.example.model.Order;
import com.example.model.Address;
import com.example.model.OrderItem;
import com.example.util.DateUtils;
import com.example.config.ImageConfig;
import com.example.dto.AddressDto;
import com.example.common.result.Result;
import com.example.dto.OrderCreateDTO;
import com.example.dto.OrderItemDTO;
import com.example.service.AddressService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;
import com.example.model.StockLock;
import com.example.service.StockService;
import org.springframework.http.MediaType;
import javax.servlet.http.HttpServletRequest;
import java.util.Map;
import java.util.HashMap;
import java.util.UUID;
import java.util.stream.Collectors;




@Slf4j
@Service
public class OrderServiceImpl implements OrderService {
    private static final Logger logger = LoggerFactory.getLogger(OrderServiceImpl.class);
   
    @Autowired
    private OrderSettlementService orderSettlementService;


    @Autowired
    private OrderMapper orderMapper;
    
    @Autowired
    private AddressMapper userAddressMapper;
    
    @Autowired
    private OrderItemMapper orderItemMapper;


    @Override
    public Order createOrder(Order order, AddressDto addressDto) {
        // 实现你的订单创建逻辑，下面是一个简单示例
        // 你可以根据实际业务完善
        if (addressDto != null) {
            // 处理地址信息
            Address userAddress = new Address();
            userAddress.setName(addressDto.getName());
            userAddress.setPhone(addressDto.getPhone());
            userAddress.setAddress(addressDto.getAddress());
            userAddress.setProvince(addressDto.getProvince());
            userAddress.setCity(addressDto.getCity());
            userAddress.setDistrict(addressDto.getDistrict());
            userAddress.setIsDefault(false);
            userAddress.setUserId(order.getUserId());
            userAddressMapper.save(userAddress);
            order.setAddressId(userAddress.getId());
        }
        orderMapper.insertOrder(order);
        return order;
    }

    public Order getOrderById(Long orderId) {
        return orderMapper.findById(orderId);
    }

 
    /**
     * 创建订单（先锁定库存）
     * @param orderDTO 订单信息
     * @return 订单信息
     */
    // 订单结算服务，如果需要使用请先注入
    @Transactional
    public Result<Order> createOrder(OrderCreateDTO orderDTO) {
        try {
            // 处理地址信息
            if (orderDTO.getAddressDto() != null) {
                handleAddressInfo(orderDTO.getOrder(), orderDTO.getAddressDto());
            }
    
            // 计算订单总金额
            BigDecimal totalAmount = orderDTO.getOrder().getItems().stream()
                    .map(OrderItem::getSubtotal)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
    
            // 验证订单总金额
            if (!totalAmount.equals(orderDTO.getOrder().getPaymentAmount())) {
                throw new IllegalArgumentException("订单总金额不匹配");
            }
            
            // 初始化订单佣金相关字段
            orderDTO.getOrder().setSettlementStatus(0); // 未结算

            // 按商品计算佣金，调用OrderSettlementService
            Order orderWithCommission = orderSettlementService.calculateCommission(orderDTO.getOrder());
            orderDTO.setOrder(orderWithCommission);

            // 使用常量设置订单状态
            orderDTO.getOrder().setStatus(ORDER_STATUS_PENDING_PAYMENT); 
            // 调用抽取的公共方法
            Date date = DateUtils.convertLocalDateTimeToDate(LocalDateTime.now());
            orderDTO.getOrder().setCreatedAt(date);
            
            // 插入订单数据
            orderMapper.insertOrder(orderDTO.getOrder()); // 调用 Mapper 方法
            System.out.println("订单创建成功，订单号：" + orderDTO.getOrder().getOrderNumber());
    
            // 保存订单商品项
            if (orderDTO.getOrder().getItems() != null && !orderDTO.getOrder().getItems().isEmpty()) {
                // 避免空指针异常
                List<OrderItem> orderItems = orderDTO.getOrder().getItems();
                for (OrderItem item : orderItems) {
                    item.setOrderId(orderDTO.getOrder().getId()); // 设置订单ID
                    insertOrderItems(Collections.singletonList(item)); // 插入单个商品项
                }
            }
    
            return Result.success(orderDTO.getOrder());
        } catch (Exception e) {
            logger.error("Error creating order: " + e.getMessage());
            return Result.failed(e.getMessage());
        }
    }

    /**
     * 根据订单 ID 获取订单
     * @param orderNumber 订单 ID
     * @return 订单对象
     */
    public Order getOrderById(String orderNumber) {
        // 这里假设你有一个 OrderMapper 来操作数据库
        return orderMapper.findByOrderNumber(orderNumber);
    }

    /**
     * 根据订单号获取订单
     * @param orderNumber 订单号
     * @return 订单对象
     */
    public Order getOrderByNumber(String orderNumber) {
        try {
            // 修改为使用 OrderMapper 进行查询
            Order order = orderMapper.findByOrderNumber(orderNumber);
            System.out.println("获取查询订单：" + order);
            return processOrderData(order);
        } catch (Exception e) {
            // 打印异常信息，方便排查
            logger.error("获取订单详情失败，订单号: {}", orderNumber, e);
            throw e;
        }
    }
    // 插入订单商品项的方法
    private void insertOrderItems(List<OrderItem> items) {
        if (items != null && !items.isEmpty()) {
            for (OrderItem item : items) {
                System.out.println("插入订单商品项：" + item);
                orderItemMapper.insertOrderItem(item);  // 使用 insertOrderItem 方法
            }
        }
    }

    /**
     * 获取用户的订单列表
     * @param userId 用户ID
     * @param status 订单状态，如果为"all"则查询所有状态
     * @return 订单列表
     */
    public Page<Order> getUserOrders(Long userId, String status, Pageable pageable) {
        if (status != null && !"all".equals(status)) {
            try {
                Integer statusCode = Integer.parseInt(status);
                System.out.println("getUserOrders statusCode:" + pageable);
                return orderMapper.getUserOrdersByStatus(userId, statusCode, pageable);
            } catch (NumberFormatException e) {
                logger.warn("无效的订单状态值: {}", status);
                return orderMapper.getUserOrders(userId, pageable);
            }
        } else {
            return orderMapper.getUserOrders(userId, pageable);
        }
    }

    /**
     * 取消订单（恢复库存）
     * @param orderId 订单ID
     * @return 取消结果
     */
    @Transactional
    public Result<Boolean> cancelOrder(Long orderId) {
        Order order = getOrderById(orderId.toString());
            if (order == null) {
            logger.warn("订单不存在，订单ID: {}", orderId);
                return Result.failed("订单不存在");
            }
            
        // 验证订单是否属于该用户
        if (!order.getUserId().equals(orderId)) {
            logger.warn("订单不属于该用户，订单ID: {}, 用户ID: {}", orderId, order.getUserId());
            return Result.failed("订单不属于该用户");
        }
        
        // 只有待付款状态的订单可以取消
            if (order.getStatus() != ORDER_STATUS_PENDING_PAYMENT) {
            logger.warn("订单状态不允许取消，订单ID: {}, 当前状态: {}", orderId, order.getStatus());
            return Result.failed("订单状态不允许取消");
            }
            
            // 更新订单状态为已取消
        orderMapper.updateOrderStatus(orderId.toString(), ORDER_STATUS_CANCELLED);
        return Result.success(true);
    }


    /**
     * 处理订单的地址信息
     * @param order 订单对象
     * @param addressDto 地址信息
     */
    private void handleAddressInfo(Order order, AddressDto addressDto) {
        Address userAddress = new Address();
        // 确保字段名称统一
        userAddress.setName(addressDto.getName());
        userAddress.setPhone(addressDto.getPhone());
        userAddress.setAddress(addressDto.getAddress());
        userAddress.setProvince(addressDto.getProvince());
        userAddress.setCity(addressDto.getCity());
        userAddress.setDistrict(addressDto.getDistrict());
        userAddress.setIsDefault(false); // 默认不设为默认地址
        userAddress.setUserId(order.getUserId()); // 设置用户ID
        userAddressMapper.save(userAddress); //
        
        System.out.println("订单地址信息处理完成"+userAddress);
        // 插入后获取插入地址的ID
        order.setAddressId(userAddress.getId()); // 设置订单的收货地址ID
    }

    // 删除重复的常量定义

    /**
     * 根据用户ID获取订单列表，支持分页和状态筛选
     * @param userId 用户ID
     * @param status 订单状态，整数类型
     * @param pageable 分页参数
     * @return 分页后的订单列表
     */
    public Page<Order> getUserOrdersById(Long userId, Integer status, Pageable pageable) {
    
        List<Order> orders;
        int total;
    
        if (status != null && status != 0) { // 0表示全部
            orders = orderMapper.getUserOrdersByIdAndStatusWithPage(userId, status);
            total = orderMapper.countOrdersByUserIdAndStatus(userId, status);
        } else {
            orders = orderMapper.getUserOrdersByIdWithPage(userId);
            total = orderMapper.countOrdersByUserId(userId);
        }
    
        // 处理订单数据
        if (orders != null) {
            for (int i = 0; i < orders.size(); i++) {
                Order originalOrder = orders.get(i);
                // 调用 processOrderData 方法处理订单数据
                Order processedOrder = processOrderData(originalOrder);
                orders.set(i, processedOrder);
            }
        }
       
        System.out.println("getUserOrdersById orders:" + orders);
    
        // 手动分页
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), orders.size());
    
        // 防止索引越界
        if (start >= orders.size()) {
            return new org.springframework.data.domain.PageImpl<>(
                    new ArrayList<>(), pageable, total);
        }
    
        List<Order> pageContent = orders.subList(start, end);
    
        // 创建 Page 对象
        return new org.springframework.data.domain.PageImpl<>(pageContent, pageable, total);
    }

    /**
     * 处理订单数据，添加前端需要的字段
     * @param order 订单对象
     * @return 处理后的订单对象，如果传入的订单对象为 null，则返回 null
     */
    public Order processOrderData(Order order) {
        if (order == null) {
            return null;
        }
        // 打印原始订单数据
        System.out.println("原始订单: " + order);

        // 格式化日期
        if (order.getCreatedAt() != null) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String formattedDate = sdf.format(order.getCreatedAt());
            order.setDate(formattedDate);
            System.out.println("设置日期: " + formattedDate);
        }
        
        // 设置状态文本
        if (order.getStatus() != null) {
            int status = order.getStatus();

            // 设置状态文本
            String statusText;
            switch (status) {
                case ORDER_STATUS_PENDING_PAYMENT:
                    statusText = "待付款";
                    // 设置倒计时（对于待付款订单）
                    order.setCountdown("1800");
                    break;
                case ORDER_STATUS_PENDING_DELIVERY:
                    statusText = "待发货";
                    break;
                case ORDER_STATUS_PENDING_RECEIPT:
                    statusText = "待收货";
                    break;
                case ORDER_STATUS_COMPLETED:
                    statusText = "已完成";
                    break;
                case ORDER_STATUS_CANCELLED:
                    statusText = "已取消";
                    break;
                default:
                    statusText = "未知状态";
            }
            order.setStatusText(statusText);
            System.out.println("设置状态文本: " + statusText);
        }

        // 设置支付金额
        if (order.getPaymentAmount() == null) {
            order.setPaymentAmount(BigDecimal.ZERO);
        }
        System.out.println("设置支付金额: " + order.getPaymentAmount());

        // 处理订单项并计算总金额
        BigDecimal totalAmount = BigDecimal.ZERO;
        List<OrderItem> processedItems = new ArrayList<>();

        if (order.getItems() != null && !order.getItems().isEmpty()) {
            System.out.println("处理订单项，数量: " + order.getItems().size());

            for (OrderItem item : order.getItems()) {
                OrderItem processedItem = new OrderItem();
                BeanUtils.copyProperties(item, processedItem);
                
                // 设置价格
                if (processedItem.getPrice() == null) {
                    processedItem.setPrice(BigDecimal.ZERO);
                }

                // 设置小计
                if (processedItem.getSubtotal() != null) {
                    totalAmount = totalAmount.add(processedItem.getSubtotal());
                }

                // 设置商品属性
                // processedItem.setAttributes("默认属性");

                processedItems.add(processedItem);
                System.out.println("处理订单项: 商品ID=" + processedItem.getProductId() + ", 价格: " + processedItem.getPrice());
            }
        }

        // 设置总金额
        order.setTotalAmount(totalAmount);

        // 设置处理后的订单项
        order.setItems(processedItems);

        // 处理支付方式
        if (order.getPaymentMethod() != null) {
            // 从订单对象中获取支付方式代码，并通过 getPaymentMethodText 方法转换为对应的文字描述
            String paymentMethodDescription = getPaymentMethodText(order.getPaymentMethod());
            // 将支付方式的文字描述设置到订单对象中
            order.setPaymentMethodText(paymentMethodDescription);
            System.out.println("设置支付方式文本: " + paymentMethodDescription);
        }

        return order;
    }

    /**
     * 根据支付方式代码获取支付方式文字描述
     * @param paymentMethod 支付方式代码
     * @return 支付方式文字描述
     */
    private String getPaymentMethodText(int paymentMethod) {
        switch (paymentMethod) {
            case PAYMENT_METHOD_ALIPAY:
                return "支付宝";
            case PAYMENT_METHOD_WECHAT:
                return "微信支付";
            case PAYMENT_METHOD_CREDIT_CARD:
                return "信用卡支付";
            default:
                return "未知支付方式";
        }
    }

    /**
     * 更新订单信息
     * @param order 订单对象
     */
    @Transactional
    public void updateOrder(Order order) {
        Order existingOrder = orderMapper.findByOrderNumber(order.getOrderNumber());
        if (existingOrder == null) {
            log.error("订单不存在：{}", order.getOrderNumber());
            throw new RuntimeException("订单不存在");
        }
    
        // 设置ID
        order.setId(existingOrder.getId());
        System.out.println("订单信息：" + order);
    
        // 更新订单信息
        orderMapper.updateOrder(order);
        log.info("订单信息已更新：{}", order.getOrderNumber());
    }

    /**
     * 更新订单状态
     * @param orderNumber 订单号
     * @param status 订单状态
     * @return 是否成功更新
     */
    @Transactional
    public boolean updateOrderStatus(String orderNumber, int status) {
        try {
            // 假设这里有一个方法可以根据订单号更新订单状态
            orderMapper.updateOrderStatus(orderNumber, status);
            return true;
        } catch (Exception e) {
            logger.error("更新订单状态失败，订单号: {}, 状态: {}", orderNumber, status, e);
            return false;
        }
    }

    /**
     * 支付订单（确认扣减库存）
     * @param orderId 订单ID
     * @return 支付结果
     */
    @Transactional
    public Result<Boolean> payOrder(Long orderId) {
        Order order = getOrderById(orderId.toString());
        if (order == null) {
            logger.warn("订单不存在，订单ID: {}", orderId);
            return Result.failed("订单不存在");
        }
        
        // 验证订单是否属于该用户
        if (!order.getUserId().equals(orderId)) {
            logger.warn("订单不属于该用户，订单ID: {}, 用户ID: {}", orderId, order.getUserId());
            return Result.failed("订单不属于该用户");
        }
        
        // 只有待付款状态的订单可以支付
        if (order.getStatus() != ORDER_STATUS_PENDING_PAYMENT) {
            logger.warn("订单状态不允许支付，订单ID: {}, 当前状态: {}", orderId, order.getStatus());
            return Result.failed("订单状态不允许支付");
        }
        
        // 更新订单状态为待发货
        orderMapper.updateOrderStatus(orderId.toString(), ORDER_STATUS_PENDING_DELIVERY);
        return Result.success(true);
    }

    /**
     * 申请退款（整单退款，恢复库存）
     * @param orderId 订单ID
     * @param reason 退款原因
     * @return 退款申请结果
     */
    @Transactional
    public Result<Boolean> refundOrder(Long orderId, String reason) {
        Order order = getOrderById(orderId.toString());
            if (order == null) {
            logger.warn("订单不存在，订单ID: {}", orderId);
                return Result.failed("订单不存在");
            }
            
        // 验证订单是否属于该用户
        if (!order.getUserId().equals(orderId)) {
            logger.warn("订单不属于该用户，订单ID: {}, 用户ID: {}", orderId, order.getUserId());
            return Result.failed("订单不属于该用户");
        }
        
        // 只有待付款状态的订单可以退款
        if (order.getStatus() != ORDER_STATUS_PENDING_PAYMENT) {
            logger.warn("订单状态不允许退款，订单ID: {}, 当前状态: {}", orderId, order.getStatus());
            return Result.failed("订单状态不允许退款");
        }
        
        // 更新订单状态为已退款
        orderMapper.updateOrderStatus(orderId.toString(), ORDER_STATUS_COMPLETED);
        return Result.success(true);
    }

    /**
     * 申请退货退款（部分退款，恢复部分库存）
     * @param orderId 订单ID
     * @param items 退货商品项
     * @param reason 退货原因
     * @return 退货申请结果
     */
    @Transactional
    public Result<Boolean> returnOrder(Long orderId, List<OrderItemDTO> items, String reason) {
        Order order = getOrderById(orderId.toString());
            if (order == null) {
            logger.warn("订单不存在，订单ID: {}", orderId);
                return Result.failed("订单不存在");
            }
            
        // 验证订单是否属于该用户
        if (!order.getUserId().equals(orderId)) {
            logger.warn("订单不属于该用户，订单ID: {}, 用户ID: {}", orderId, order.getUserId());
            return Result.failed("订单不属于该用户");
        }
        
        // 只有待付款状态的订单可以退货
        if (order.getStatus() != ORDER_STATUS_PENDING_PAYMENT) {
            logger.warn("订单状态不允许退货，订单ID: {}, 当前状态: {}", orderId, order.getStatus());
            return Result.failed("订单状态不允许退货");
        }
        
        // 更新订单状态为已退货
        orderMapper.updateOrderStatus(orderId.toString(), ORDER_STATUS_COMPLETED);
        return Result.success(true);
    }

    /**
     * 确认收货（订单完成）
     * @param orderId 订单ID
     * @return 确认结果
     */
    @Transactional
    public Result<Boolean> confirmReceiveOrder(Long orderId) {
        Order order = getOrderById(orderId.toString());
            if (order == null) {
            logger.warn("订单不存在，订单ID: {}", orderId);
                return Result.failed("订单不存在");
            }
            
        // 验证订单是否属于该用户
        if (!order.getUserId().equals(orderId)) {
            logger.warn("订单不属于该用户，订单ID: {}, 用户ID: {}", orderId, order.getUserId());
            return Result.failed("订单不属于该用户");
        }
        
        // 只有待收货状态的订单可以确认收货
        if (order.getStatus() != ORDER_STATUS_PENDING_RECEIPT) {
            logger.warn("订单状态不允许确认收货，订单ID: {}, 当前状态: {}", orderId, order.getStatus());
            return Result.failed("订单状态不允许确认收货");
        }
        
        // 更新订单状态为已完成
        orderMapper.updateOrderStatus(orderId.toString(), ORDER_STATUS_COMPLETED);
        return Result.success(true);
    }

    /**
     * 根据用户ID查询订单列表
     * @param userId 用户ID
     * @param status 订单状态，null表示全部
     * @return 订单列表
     */
    public Result<List<Order>> getOrdersByUserId(Long userId, Integer status) {
            List<Order> orders;
        if (status != null && status != 0) {
            orders = orderMapper.getUserOrdersByIdAndStatus(userId, status);
            } else {
            orders = orderMapper.getUserOrdersById(userId);
        }
        return Result.success(orders);
    }
   
} 