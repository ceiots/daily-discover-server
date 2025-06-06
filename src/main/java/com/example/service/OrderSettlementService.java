package com.example.service;

import com.example.model.Order;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 订单结算服务接口
 * 负责处理平台与商家之间的订单结算
 */
public interface OrderSettlementService {
    
    /**
     * 计算订单佣金
     * @param order 订单对象
     * @return 计算佣金后的订单对象
     */
    Order calculateCommission(Order order);
    
    /**
     * 结算订单
     * @param orderId 订单ID
     * @return 是否结算成功
     */
    boolean settleOrder(Long orderId);
    
    /**
     * 批量结算订单
     * @param orderIds 订单ID列表
     * @return 成功结算的订单数量
     */
    int batchSettleOrders(List<Long> orderIds);
    
    /**
     * 获取店铺待结算订单
     * @param shopId 店铺ID
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 待结算订单列表
     */
    List<Order> getPendingSettlementOrders(Long shopId, Date startDate, Date endDate);
    
    /**
     * 获取所有店铺的待结算订单
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 待结算订单列表
     */
    List<Order> getAllPendingSettlementOrders(Date startDate, Date endDate);
    
    /**
     * 获取店铺已结算订单
     * @param shopId 店铺ID
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 已结算订单列表
     */
    List<Order> getSettledOrders(Long shopId, Date startDate, Date endDate);
    
    /**
     * 获取所有店铺的已结算订单
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 已结算订单列表
     */
    List<Order> getAllSettledOrders(Date startDate, Date endDate);
    
    /**
     * 获取店铺结算汇总
     * @param shopId 店铺ID
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 结算汇总信息
     */
    SettlementSummary getSettlementSummary(Long shopId, Date startDate, Date endDate);
    
    /**
     * 获取平台结算统计数据
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 结算统计数据
     */
    Map<String, Object> getSettlementStatistics(Date startDate, Date endDate);
    
    /**
     * 结算汇总信息
     */
    class SettlementSummary {
        private Long shopId;
        private String shopName;
        private int totalOrders;
        private BigDecimal totalAmount;
        private BigDecimal totalCommission;
        private BigDecimal totalSettlementAmount;
        
        // Getters and setters
        public Long getShopId() { return shopId; }
        public void setShopId(Long shopId) { this.shopId = shopId; }
        
        public String getShopName() { return shopName; }
        public void setShopName(String shopName) { this.shopName = shopName; }
        
        public int getTotalOrders() { return totalOrders; }
        public void setTotalOrders(int totalOrders) { this.totalOrders = totalOrders; }
        
        public BigDecimal getTotalAmount() { return totalAmount; }
        public void setTotalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; }
        
        public BigDecimal getTotalCommission() { return totalCommission; }
        public void setTotalCommission(BigDecimal totalCommission) { this.totalCommission = totalCommission; }
        
        public BigDecimal getTotalSettlementAmount() { return totalSettlementAmount; }
        public void setTotalSettlementAmount(BigDecimal totalSettlementAmount) { this.totalSettlementAmount = totalSettlementAmount; }
    }
}