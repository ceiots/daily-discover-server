package com.dailydiscover.service;

import com.dailydiscover.model.ShippingAddress;
import com.dailydiscover.model.ShippingCarrier;
import com.dailydiscover.model.ShippingMethod;
import com.dailydiscover.model.ShippingPackage;
import com.dailydiscover.model.ShippingRate;
import com.dailydiscover.model.ShippingTracking;
import java.util.List;
import java.util.Map;

/**
 * 物流服务接口
 * 处理物流地址管理、运输方式、运费计算、订单跟踪等功能
 */
public interface ShippingService {
    
    // ==================== 物流地址管理 ====================
    
    /**
     * 根据ID获取物流地址
     */
    ShippingAddress findAddressById(Long id);
    
    /**
     * 根据客户ID获取物流地址列表
     */
    List<ShippingAddress> findAddressesByCustomerId(Long customerId);
    
    /**
     * 保存物流地址
     */
    void saveAddress(ShippingAddress address);
    
    /**
     * 更新物流地址
     */
    void updateAddress(ShippingAddress address);
    
    /**
     * 删除物流地址
     */
    void deleteAddress(Long id);
    
    /**
     * 验证物流地址
     */
    boolean validateAddress(ShippingAddress address);
    
    /**
     * 获取默认物流地址
     */
    ShippingAddress findDefaultAddress(Long customerId);
    
    /**
     * 设置默认物流地址
     */
    void setDefaultAddress(Long customerId, Long addressId);
    
    // ==================== 运输方式管理 ====================
    
    /**
     * 获取所有运输方式
     */
    List<ShippingMethod> findAllShippingMethods();
    
    /**
     * 根据ID获取运输方式
     */
    ShippingMethod findShippingMethodById(Long id);
    
    /**
     * 根据地区获取可用运输方式
     */
    List<ShippingMethod> findAvailableShippingMethods(String region);
    
    /**
     * 保存运输方式
     */
    void saveShippingMethod(ShippingMethod method);
    
    /**
     * 更新运输方式
     */
    void updateShippingMethod(ShippingMethod method);
    
    /**
     * 删除运输方式
     */
    void deleteShippingMethod(Long id);
    
    /**
     * 启用/禁用运输方式
     */
    void toggleShippingMethod(Long id, boolean enabled);
    
    // ==================== 运费计算 ====================
    
    /**
     * 计算运费
     */
    ShippingRate calculateShippingRate(ShippingPackage shippingPackage, ShippingAddress address, ShippingMethod method);
    
    /**
     * 批量计算运费
     */
    List<ShippingRate> calculateBulkShippingRates(List<ShippingPackage> packages, ShippingAddress address);
    
    /**
     * 获取运费估算
     */
    Double estimateShippingCost(Double weight, String destination, String shippingMethod);
    
    /**
     * 获取实时运费
     */
    Double getRealTimeShippingCost(ShippingPackage shippingPackage, ShippingAddress address);
    
    /**
     * 获取运费规则
     */
    Map<String, Object> getShippingRules(String region);
    

    
    // ==================== 包裹管理 ====================
    
    /**
     * 创建包裹
     */
    ShippingPackage createPackage(ShippingPackage shippingPackage);
    
    /**
     * 根据ID获取包裹
     */
    ShippingPackage findPackageById(Long id);
    

    
    /**
     * 更新包裹信息
     */
    void updatePackage(ShippingPackage shippingPackage);
    
    /**
     * 删除包裹
     */
    void deletePackage(Long id);
    
    /**
     * 包裹重量验证
     */
    boolean validatePackageWeight(ShippingPackage shippingPackage);
    
    /**
     * 包裹尺寸验证
     */
    boolean validatePackageDimensions(ShippingPackage shippingPackage);
    
    // ==================== 物流跟踪 ====================
    
    /**
     * 创建跟踪记录
     */
    void createTracking(ShippingTracking tracking);
    

    
    /**
     * 获取最新跟踪状态
     */
    ShippingTracking getLatestTrackingStatus(Long shippingOrderId);
    
    /**
     * 更新跟踪状态
     */
    void updateTrackingStatus(Long shippingOrderId, String status, String location);
    
    /**
     * 同步第三方物流跟踪
     */
    void syncThirdPartyTracking(Long shippingOrderId);
    
    /**
     * 获取跟踪历史
     */
    List<ShippingTracking> getTrackingHistory(Long shippingOrderId);
    
    /**
     * 发送跟踪通知
     */
    void sendTrackingNotification(Long shippingOrderId, String notificationType);
    
    // ==================== 物流承运商管理 ====================
    
    /**
     * 获取所有承运商
     */
    List<ShippingCarrier> findAllCarriers();
    
    /**
     * 根据ID获取承运商
     */
    ShippingCarrier findCarrierById(Long id);
    
    /**
     * 根据地区获取可用承运商
     */
    List<ShippingCarrier> findAvailableCarriers(String region);
    
    /**
     * 保存承运商
     */
    void saveCarrier(ShippingCarrier carrier);
    
    /**
     * 更新承运商
     */
    void updateCarrier(ShippingCarrier carrier);
    
    /**
     * 删除承运商
     */
    void deleteCarrier(Long id);
    
    /**
     * 获取承运商费率
     */
    Map<String, Object> getCarrierRates(Long carrierId);
    
    /**
     * 验证承运商服务
     */
    boolean validateCarrierService(Long carrierId, String serviceType);
    
    // ==================== 物流分析和报告 ====================
    
    /**
     * 获取物流统计报告
     */
    Map<String, Object> getShippingStatistics(String period);
    
    /**
     * 获取承运商表现报告
     */
    Map<String, Object> getCarrierPerformanceReport(String period);
    
    /**
     * 获取物流时效分析
     */
    Map<String, Object> getShippingTimeAnalysis(String region);
    
    /**
     * 获取物流成本分析
     */
    Map<String, Object> getShippingCostAnalysis(String period);
    
    /**
     * 获取物流问题报告
     */
    Map<String, Object> getShippingIssuesReport(String period);
    
    /**
     * 获取客户满意度报告
     */
    Map<String, Object> getShippingSatisfactionReport(String period);
    
    // ==================== 物流优化 ====================
    
    /**
     * 优化物流路线
     */
    Map<String, Object> optimizeShippingRoute(ShippingAddress from, ShippingAddress to, List<ShippingPackage> packages);
    

    
    /**
     * 获取最佳物流方案
     */
    ShippingMethod getOptimalShippingMethod(ShippingPackage shippingPackage, ShippingAddress address);
    
    /**
     * 成本效益分析
     */
    Map<String, Object> analyzeCostEffectiveness(ShippingPackage shippingPackage, ShippingAddress address);
    
    /**
     * 时效性分析
     */
    Map<String, Object> analyzeDeliveryTime(ShippingPackage shippingPackage, ShippingAddress address);
    
    // ==================== 物流异常处理 ====================
    

    
    /**
     * 获取异常处理记录
     */
    List<Map<String, Object>> getShippingExceptionRecords(String period);
    
    // ==================== 国际物流 ====================
    
    /**
     * 计算国际运费
     */
    ShippingRate calculateInternationalShipping(ShippingPackage shippingPackage, ShippingAddress destination);
    
    /**
     * 获取海关信息
     */
    Map<String, Object> getCustomsInformation(String destinationCountry);
    
    /**
     * 验证国际地址
     */
    boolean validateInternationalAddress(ShippingAddress address);
    
    /**
     * 获取关税估算
     */
    Double estimateDuties(ShippingPackage shippingPackage, String destinationCountry);
    

    

}