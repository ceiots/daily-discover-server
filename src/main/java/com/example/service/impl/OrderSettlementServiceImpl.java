package com.example.service.impl;

import com.example.model.Order;
import com.example.service.OrderSettlementService;
import com.example.service.OrderService;
import com.example.service.ShopService;
import com.example.mapper.OrderMapper;
import com.example.model.Shop;
import com.example.model.OrderItem;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 订单结算服务实现类
 */
@Service
public class OrderSettlementServiceImpl implements OrderSettlementService {
    
    private static final Logger logger = LoggerFactory.getLogger(OrderSettlementServiceImpl.class);
    
    @Autowired
    private OrderMapper orderMapper;
    
    @Autowired
    private OrderService orderService;
    
    @Autowired
    private ShopService shopService;
    
    // 默认平台佣金比例
    private static final BigDecimal DEFAULT_COMMISSION_RATE = new BigDecimal("0.05"); // 5%
    
    /**
     * 计算订单佣金
     */
    @Override
    public Order calculateCommission(Order order) {
        if (order == null || order.getPaymentAmount() == null) {
            logger.warn("订单或支付金额为空，无法计算佣金");
            return order;
        }
        
        // 初始化总佣金金额
        BigDecimal totalCommissionAmount = BigDecimal.ZERO;
        
        // 按商品计算佣金
        if (order.getItems() != null && !order.getItems().isEmpty()) {
            for (OrderItem item : order.getItems()) {
                // 根据商品类别获取佣金比例
                BigDecimal commissionRate = getCommissionRateByProductCategory(item.getProductId());
                
                // 计算单个商品的佣金金额
                BigDecimal itemCommissionAmount = item.getSubtotal().multiply(commissionRate)
                    .setScale(2, java.math.RoundingMode.HALF_UP);
                
                // 设置商品的佣金比例和金额
                item.setCommissionRate(commissionRate);
                item.setCommissionAmount(itemCommissionAmount);
                
                // 累加总佣金
                totalCommissionAmount = totalCommissionAmount.add(itemCommissionAmount);
                
                logger.info("商品佣金计算 - 商品ID: {}, 小计: {}, 佣金比例: {}, 佣金金额: {}", 
                    item.getProductId(), item.getSubtotal(), commissionRate, itemCommissionAmount);
            }
        }
        
        order.setPlatformCommissionAmount(totalCommissionAmount);
        
        // 计算店铺实际收款金额
        BigDecimal shopAmount = order.getPaymentAmount().subtract(totalCommissionAmount);
        order.setShopAmount(shopAmount);
        
        logger.info("订单佣金计算 - 总金额: {}, 佣金总额: {}, 店铺收款: {}", 
            order.getPaymentAmount(), totalCommissionAmount, shopAmount);
        
        order.setSettlementStatus(0); // 未结算
        
        return order;
    }
    
    /**
     * 结算单个订单
     */
    @Override
    @Transactional
    public boolean settleOrder(Long orderId) {
        try {
            // 获取订单信息
            Order order = orderService.getOrderById(orderId);
            if (order == null) {
                logger.warn("订单不存在，无法结算，订单ID: {}", orderId);
                return false;
            }
            
            // 只有已完成状态的订单可以结算
            if (order.getStatus() != OrderService.ORDER_STATUS_COMPLETED) {
                logger.warn("订单状态不允许结算，订单ID: {}, 当前状态: {}", orderId, order.getStatus());
                return false;
            }
            
            // 检查是否已结算
            if (order.getSettlementStatus() != null && order.getSettlementStatus() == 1) {
                logger.warn("订单已结算，无需重复结算，订单ID: {}", orderId);
                return false;
            }
            
            // 如果未计算佣金，先计算佣金
            if (order.getPlatformCommissionAmount() == null) {
                order = calculateCommission(order);
            }
            
            // 更新结算状态
            order.setSettlementStatus(1); // 已结算
            order.setSettlementTime(new Date());
            
            // 获取店铺ID
            Long shopId = null;
            if (order.getItems() != null && !order.getItems().isEmpty()) {
                shopId = order.getItems().get(0).getShopId();
            }
            
            // 调用支付服务，将资金转入商家账户（实际项目中需要实现）
            boolean transferSuccess = transferFundsToShop(order);
            if (!transferSuccess) {
                logger.error("资金转账失败，订单ID: {}", orderId);
                return false;
            }
            
            // 更新订单信息
            orderService.updateOrder(order);
            
            logger.info("订单结算成功，订单ID: {}, 店铺ID: {}, 结算金额: {}", 
                    orderId, shopId, order.getShopAmount());
            return true;
        } catch (Exception e) {
            logger.error("订单结算异常，订单ID: {}", orderId, e);
            return false;
        }
    }
    
    /**
     * 批量结算订单
     */
    @Override
    @Transactional
    public int batchSettleOrders(List<Long> orderIds) {
        int successCount = 0;
        for (Long orderId : orderIds) {
            if (settleOrder(orderId)) {
                successCount++;
            }
        }
        return successCount;
    }
    
    /**
     * 获取店铺待结算订单
     */
    @Override
    public List<Order> getPendingSettlementOrders(Long shopId, Date startDate, Date endDate) {
        // 实际项目中需要实现查询逻辑
        // 这里简单模拟返回空列表
        return new ArrayList<>();
    }
    
    /**
     * 获取所有店铺的待结算订单
     */
    @Override
    public List<Order> getAllPendingSettlementOrders(Date startDate, Date endDate) {
        // 实际项目中需要实现查询逻辑，查询所有店铺的待结算订单
        // 这里简单模拟返回空列表
        return new ArrayList<>();
    }
    
    /**
     * 获取店铺已结算订单
     */
    @Override
    public List<Order> getSettledOrders(Long shopId, Date startDate, Date endDate) {
        // 实际项目中需要实现查询逻辑
        // 这里简单模拟返回空列表
        return new ArrayList<>();
    }
    
    /**
     * 获取所有店铺的已结算订单
     */
    @Override
    public List<Order> getAllSettledOrders(Date startDate, Date endDate) {
        // 实际项目中需要实现查询逻辑，查询所有店铺的已结算订单
        // 这里简单模拟返回空列表
        return new ArrayList<>();
    }
    
    /**
     * 获取店铺结算汇总
     */
    @Override
    public SettlementSummary getSettlementSummary(Long shopId, Date startDate, Date endDate) {
        // 实际项目中需要实现查询逻辑
        // 这里简单模拟返回空汇总
        SettlementSummary summary = new SettlementSummary();
        summary.setShopId(shopId);
        
        // 获取店铺信息
        Shop shop = shopService.getShopById(shopId);
        if (shop != null) {
            summary.setShopName(shop.getShopName());
        }
        
        summary.setTotalOrders(0);
        summary.setTotalAmount(BigDecimal.ZERO);
        summary.setTotalCommission(BigDecimal.ZERO);
        summary.setTotalSettlementAmount(BigDecimal.ZERO);
        
        return summary;
    }
    
    /**
     * 获取平台结算统计数据
     */
    @Override
    public Map<String, Object> getSettlementStatistics(Date startDate, Date endDate) {
        // 实际项目中需要实现查询逻辑
        // 这里简单模拟返回空统计数据
        Map<String, Object> statistics = new HashMap<>();
        statistics.put("pendingCount", 0);
        statistics.put("settledCount", 0);
        statistics.put("pendingAmount", BigDecimal.ZERO);
        statistics.put("settledAmount", BigDecimal.ZERO);
        statistics.put("commissionAmount", BigDecimal.ZERO);
        return statistics;
    }
    
    /**
     * 模拟资金转账到店铺账户
     * 实际项目中需要对接支付系统实现
     */
    private boolean transferFundsToShop(Order order) {
        // 获取店铺ID
        Long shopId = null;
        if (order.getItems() != null && !order.getItems().isEmpty()) {
            shopId = order.getItems().get(0).getShopId();
        }
        
        // 这里模拟转账成功
        logger.info("模拟资金转账到店铺账户，订单ID: {}, 店铺ID: {}, 金额: {}", 
                order.getId(), shopId, order.getShopAmount());
        return true;
    }
    
    /**
     * 根据商品类别获取佣金比例
     * @param productId 商品ID
     * @return 佣金比例
     */
    private BigDecimal getCommissionRateByProductCategory(Long productId) {
        // 这里可以根据商品ID查询商品类别，然后根据类别返回对应的佣金比例
        // 简化实现，实际应该查询数据库获取商品类别和对应的佣金比例
        
        // 模拟不同类别的佣金比例
        Map<String, BigDecimal> categoryCommissionRates = new HashMap<>();
        categoryCommissionRates.put("电子产品", new BigDecimal("0.05")); // 5%
        categoryCommissionRates.put("服装", new BigDecimal("0.10")); // 10%
        categoryCommissionRates.put("食品", new BigDecimal("0.03")); // 3%
        categoryCommissionRates.put("家居", new BigDecimal("0.07")); // 7%
        categoryCommissionRates.put("美妆", new BigDecimal("0.15")); // 15%
        
        // 默认佣金比例
        BigDecimal defaultRate = DEFAULT_COMMISSION_RATE;
        
        // 这里应该根据productId查询商品类别，然后返回对应的佣金比例
        // 简化实现，随机返回一个类别的佣金比例
        String[] categories = {"电子产品", "服装", "食品", "家居", "美妆"};
        String category = categories[Math.abs(productId.intValue() % categories.length)];
        
        logger.info("商品ID: {}, 类别: {}, 佣金比例: {}", productId, category, categoryCommissionRates.get(category));
        
        return categoryCommissionRates.getOrDefault(category, defaultRate);
    }
}