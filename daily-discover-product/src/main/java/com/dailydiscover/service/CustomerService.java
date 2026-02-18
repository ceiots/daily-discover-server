package com.dailydiscover.service;

import com.dailydiscover.model.Customer;
import com.dailydiscover.model.CustomerAddress;
import com.dailydiscover.model.CustomerPreference;
import com.dailydiscover.model.CustomerStats;
import java.util.List;
import java.util.Map;

/**
 * 客户服务接口
 * 处理客户信息管理、地址管理、偏好设置、客户统计等功能
 */
public interface CustomerService {
    
    // ==================== 客户基础信息管理 ====================
    
    /**
     * 根据ID获取客户信息
     */
    Customer findById(Long id);
    
    /**
     * 根据用户ID获取客户信息
     */
    Customer findByUserId(Long userId);
    
    /**
     * 保存客户信息
     */
    void save(Customer customer);
    
    /**
     * 更新客户信息
     */
    void update(Customer customer);
    
    /**
     * 删除客户信息
     */
    void delete(Long id);
    
    /**
     * 获取所有客户列表
     */
    List<Customer> findAll();
    
    /**
     * 根据条件查询客户
     */
    List<Customer> findByCriteria(Map<String, Object> criteria);
    
    /**
     * 分页查询客户
     */
    List<Customer> findPaginated(int page, int size);
    
    /**
     * 获取客户总数
     */
    Long countAll();
    
    // ==================== 客户地址管理 ====================
    
    /**
     * 获取客户地址列表
     */
    List<CustomerAddress> findAddressesByCustomerId(Long customerId);
    
    /**
     * 获取默认地址
     */
    CustomerAddress findDefaultAddress(Long customerId);
    
    /**
     * 保存客户地址
     */
    void saveAddress(CustomerAddress address);
    
    /**
     * 更新客户地址
     */
    void updateAddress(CustomerAddress address);
    
    /**
     * 删除客户地址
     */
    void deleteAddress(Long addressId);
    
    /**
     * 设置默认地址
     */
    void setDefaultAddress(Long customerId, Long addressId);
    
    // ==================== 客户偏好设置 ====================
    
    /**
     * 获取客户偏好设置
     */
    CustomerPreference findPreferenceByCustomerId(Long customerId);
    
    /**
     * 保存客户偏好设置
     */
    void savePreference(CustomerPreference preference);
    
    /**
     * 更新客户偏好设置
     */
    void updatePreference(CustomerPreference preference);
    
    /**
     * 更新客户通知偏好
     */
    void updateNotificationPreference(Long customerId, Map<String, Boolean> preferences);
    
    /**
     * 更新客户隐私设置
     */
    void updatePrivacyPreference(Long customerId, Map<String, Boolean> preferences);
    
    // ==================== 客户统计和分析 ====================
    
    /**
     * 获取客户统计信息
     */
    CustomerStats getCustomerStats(Long customerId);
    
    /**
     * 获取客户活跃度分析
     */
    Map<String, Object> getCustomerActivityAnalysis(Long customerId);
    
    /**
     * 获取客户购买行为分析
     */
    Map<String, Object> getCustomerPurchaseBehavior(Long customerId);
    
    /**
     * 获取客户价值分析
     */
    Map<String, Object> getCustomerValueAnalysis(Long customerId);
    
    /**
     * 获取客户生命周期分析
     */
    Map<String, Object> getCustomerLifecycleAnalysis(Long customerId);
    
    // ==================== 客户标签和分类 ====================
    
    /**
     * 添加客户标签
     */
    void addCustomerTag(Long customerId, String tag);
    
    /**
     * 移除客户标签
     */
    void removeCustomerTag(Long customerId, String tag);
    
    /**
     * 获取客户所有标签
     */
    List<String> getCustomerTags(Long customerId);
    
    /**
     * 根据标签查询客户
     */
    List<Customer> findCustomersByTag(String tag);
    
    /**
     * 客户分类（新客户、活跃客户、沉默客户、流失客户）
     */
    String classifyCustomer(Long customerId);
    
    /**
     * 获取不同分类的客户列表
     */
    List<Customer> findCustomersByCategory(String category);
    
    // ==================== 客户关系管理 ====================
    
    /**
     * 获取客户关系网络
     */
    Map<String, Object> getCustomerNetwork(Long customerId);
    
    /**
     * 获取客户推荐关系
     */
    List<Customer> getCustomerReferrals(Long customerId);
    
    /**
     * 添加客户推荐关系
     */
    void addCustomerReferral(Long referrerId, Long referredId);
    
    /**
     * 获取客户社交影响力分析
     */
    Map<String, Object> getCustomerSocialInfluence(Long customerId);
    
    // ==================== 客户服务和关怀 ====================
    
    /**
     * 获取客户服务记录
     */
    List<Map<String, Object>> getCustomerServiceRecords(Long customerId);
    
    /**
     * 添加客户服务记录
     */
    void addCustomerServiceRecord(Long customerId, String serviceType, String description);
    
    /**
     * 获取客户满意度分析
     */
    Map<String, Object> getCustomerSatisfactionAnalysis(Long customerId);
    
    /**
     * 发送客户关怀消息
     */
    void sendCustomerCareMessage(Long customerId, String messageType, String content);
    
    /**
     * 获取客户关怀计划
     */
    List<Map<String, Object>> getCustomerCarePlan(Long customerId);
    
    // ==================== 客户生命周期管理 ====================
    
    /**
     * 客户注册
     */
    void registerCustomer(Customer customer);
    
    /**
     * 客户激活
     */
    void activateCustomer(Long customerId);
    
    /**
     * 客户沉默处理
     */
    void handleInactiveCustomer(Long customerId);
    
    /**
     * 客户流失预警
     */
    void sendChurnWarning(Long customerId);
    
    /**
     * 客户挽回策略
     */
    void implementRetentionStrategy(Long customerId, String strategy);
    
    // ==================== 批量操作 ====================
    
    /**
     * 批量更新客户状态
     */
    void batchUpdateCustomerStatus(List<Long> customerIds, String status);
    
    /**
     * 批量添加客户标签
     */
    void batchAddCustomerTags(List<Long> customerIds, List<String> tags);
    
    /**
     * 批量发送客户消息
     */
    void batchSendCustomerMessages(List<Long> customerIds, String messageType, String content);
    
    /**
     * 批量导入客户数据
     */
    void batchImportCustomers(List<Customer> customers);
    
    /**
     * 批量导出客户数据
     */
    List<Map<String, Object>> batchExportCustomers(List<Long> customerIds);
    
    // ==================== 高级分析功能 ====================
    
    /**
     * 客户细分分析
     */
    Map<String, Object> getCustomerSegmentationAnalysis();
    
    /**
     * 客户流失预测
     */
    Map<String, Object> getCustomerChurnPrediction(Long customerId);
    
    /**
     * 客户生命周期价值预测
     */
    Map<String, Object> getCustomerLifetimeValuePrediction(Long customerId);
    
    /**
     * 客户行为模式分析
     */
    Map<String, Object> getCustomerBehaviorPatternAnalysis(Long customerId);
    
    /**
     * 客户推荐价值分析
     */
    Map<String, Object> getCustomerReferralValueAnalysis(Long customerId);
}