package com.dailydiscover.service.impl;

import com.dailydiscover.mapper.CustomerMapper;
import com.dailydiscover.model.Customer;
import com.dailydiscover.model.CustomerAddress;
import com.dailydiscover.model.CustomerPreference;
import com.dailydiscover.model.CustomerStats;
import com.dailydiscover.service.CustomerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * 客户服务实现类
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class CustomerServiceImpl implements CustomerService {
    
    private final CustomerMapper customerMapper;
    
    // ==================== 客户基础信息管理 ====================
    
    @Override
    public Customer findById(Long id) {
        try {
            return customerMapper.findById(id);
        } catch (Exception e) {
            log.error("获取客户信息失败: id={}", id, e);
            throw new RuntimeException("获取客户信息失败", e);
        }
    }
    
    @Override
    public Customer findByUserId(Long userId) {
        try {
            return customerMapper.findByUserId(userId);
        } catch (Exception e) {
            log.error("获取客户信息失败: userId={}", userId, e);
            throw new RuntimeException("获取客户信息失败", e);
        }
    }
    
    @Override
    @Transactional
    public void save(Customer customer) {
        try {
            customerMapper.insert(customer);
            log.info("客户信息保存成功: id={}, name={}", customer.getId(), customer.getName());
        } catch (Exception e) {
            log.error("保存客户信息失败: name={}", customer.getName(), e);
            throw new RuntimeException("保存客户信息失败", e);
        }
    }
    
    @Override
    @Transactional
    public void update(Customer customer) {
        try {
            customerMapper.update(customer);
            log.info("客户信息更新成功: id={}, name={}", customer.getId(), customer.getName());
        } catch (Exception e) {
            log.error("更新客户信息失败: id={}, name={}", customer.getId(), customer.getName(), e);
            throw new RuntimeException("更新客户信息失败", e);
        }
    }
    
    @Override
    @Transactional
    public void delete(Long id) {
        try {
            customerMapper.delete(id);
            log.info("客户信息删除成功: id={}", id);
        } catch (Exception e) {
            log.error("删除客户信息失败: id={}", id, e);
            throw new RuntimeException("删除客户信息失败", e);
        }
    }
    
    @Override
    public List<Customer> findAll() {
        try {
            return customerMapper.findAll();
        } catch (Exception e) {
            log.error("获取所有客户信息失败", e);
            throw new RuntimeException("获取所有客户信息失败", e);
        }
    }
    
    @Override
    public List<Customer> findByCriteria(Map<String, Object> criteria) {
        try {
            return customerMapper.findByCriteria(criteria);
        } catch (Exception e) {
            log.error("根据条件查询客户信息失败: criteria={}", criteria, e);
            throw new RuntimeException("根据条件查询客户信息失败", e);
        }
    }
    
    @Override
    public List<Customer> findPaginated(int page, int size) {
        try {
            int offset = (page - 1) * size;
            return customerMapper.findPaginated(offset, size);
        } catch (Exception e) {
            log.error("分页查询客户信息失败: page={}, size={}", page, size, e);
            throw new RuntimeException("分页查询客户信息失败", e);
        }
    }
    
    @Override
    public Long countAll() {
        try {
            return customerMapper.countAll();
        } catch (Exception e) {
            log.error("统计客户总数失败", e);
            throw new RuntimeException("统计客户总数失败", e);
        }
    }
    
    // ==================== 客户地址管理 ====================
    
    @Override
    public List<CustomerAddress> findAddressesByCustomerId(Long customerId) {
        try {
            return customerMapper.findAddressesByCustomerId(customerId);
        } catch (Exception e) {
            log.error("获取客户地址列表失败: customerId={}", customerId, e);
            throw new RuntimeException("获取客户地址列表失败", e);
        }
    }
    
    @Override
    public CustomerAddress findDefaultAddress(Long customerId) {
        try {
            return customerMapper.findDefaultAddress(customerId);
        } catch (Exception e) {
            log.error("获取客户默认地址失败: customerId={}", customerId, e);
            throw new RuntimeException("获取客户默认地址失败", e);
        }
    }
    
    @Override
    @Transactional
    public void saveAddress(CustomerAddress address) {
        try {
            customerMapper.insertAddress(address);
            log.info("客户地址保存成功: id={}, customerId={}", address.getId(), address.getCustomerId());
        } catch (Exception e) {
            log.error("保存客户地址失败: customerId={}", address.getCustomerId(), e);
            throw new RuntimeException("保存客户地址失败", e);
        }
    }
    
    @Override
    @Transactional
    public void updateAddress(CustomerAddress address) {
        try {
            customerMapper.updateAddress(address);
            log.info("客户地址更新成功: id={}, customerId={}", address.getId(), address.getCustomerId());
        } catch (Exception e) {
            log.error("更新客户地址失败: id={}, customerId={}", address.getId(), address.getCustomerId(), e);
            throw new RuntimeException("更新客户地址失败", e);
        }
    }
    
    @Override
    @Transactional
    public void deleteAddress(Long addressId) {
        try {
            customerMapper.deleteAddress(addressId);
            log.info("客户地址删除成功: id={}", addressId);
        } catch (Exception e) {
            log.error("删除客户地址失败: id={}", addressId, e);
            throw new RuntimeException("删除客户地址失败", e);
        }
    }
    
    @Override
    @Transactional
    public void setDefaultAddress(Long customerId, Long addressId) {
        try {
            // 先清除该客户的所有默认地址
            customerMapper.clearDefaultAddresses(customerId);
            // 设置新的默认地址
            customerMapper.setAddressAsDefault(addressId);
            log.info("设置默认地址成功: customerId={}, addressId={}", customerId, addressId);
        } catch (Exception e) {
            log.error("设置默认地址失败: customerId={}, addressId={}", customerId, addressId, e);
            throw new RuntimeException("设置默认地址失败", e);
        }
    }
    
    // ==================== 客户偏好设置 ====================
    
    @Override
    public CustomerPreference findPreferenceByCustomerId(Long customerId) {
        try {
            return customerMapper.findPreferenceByCustomerId(customerId);
        } catch (Exception e) {
            log.error("获取客户偏好设置失败: customerId={}", customerId, e);
            throw new RuntimeException("获取客户偏好设置失败", e);
        }
    }
    
    @Override
    @Transactional
    public void savePreference(CustomerPreference preference) {
        try {
            customerMapper.insertPreference(preference);
            log.info("客户偏好设置保存成功: id={}, customerId={}", preference.getId(), preference.getCustomerId());
        } catch (Exception e) {
            log.error("保存客户偏好设置失败: customerId={}", preference.getCustomerId(), e);
            throw new RuntimeException("保存客户偏好设置失败", e);
        }
    }
    
    @Override
    @Transactional
    public void updatePreference(CustomerPreference preference) {
        try {
            customerMapper.updatePreference(preference);
            log.info("客户偏好设置更新成功: id={}, customerId={}", preference.getId(), preference.getCustomerId());
        } catch (Exception e) {
            log.error("更新客户偏好设置失败: id={}, customerId={}", preference.getId(), preference.getCustomerId(), e);
            throw new RuntimeException("更新客户偏好设置失败", e);
        }
    }
    
    @Override
    @Transactional
    public void updateNotificationPreference(Long customerId, Map<String, Boolean> preferences) {
        try {
            CustomerPreference preference = customerMapper.findPreferenceByCustomerId(customerId);
            if (preference == null) {
                preference = new CustomerPreference();
                preference.setCustomerId(customerId);
            }
            
            // 更新通知偏好设置
            if (preferences.containsKey("emailNotifications")) {
                preference.setEmailNotifications(preferences.get("emailNotifications"));
            }
            if (preferences.containsKey("smsNotifications")) {
                preference.setSmsNotifications(preferences.get("smsNotifications"));
            }
            if (preferences.containsKey("pushNotifications")) {
                preference.setPushNotifications(preferences.get("pushNotifications"));
            }
            if (preferences.containsKey("marketingEmails")) {
                preference.setMarketingEmails(preferences.get("marketingEmails"));
            }
            
            if (preference.getId() == null) {
                customerMapper.insertPreference(preference);
            } else {
                customerMapper.updatePreference(preference);
            }
            
            log.info("更新客户通知偏好设置成功: customerId={}", customerId);
        } catch (Exception e) {
            log.error("更新客户通知偏好设置失败: customerId={}", customerId, e);
            throw new RuntimeException("更新客户通知偏好设置失败", e);
        }
    }
    
    @Override
    @Transactional
    public void updatePrivacyPreference(Long customerId, Map<String, Boolean> preferences) {
        try {
            CustomerPreference preference = customerMapper.findPreferenceByCustomerId(customerId);
            if (preference == null) {
                preference = new CustomerPreference();
                preference.setCustomerId(customerId);
            }
            
            // 更新隐私偏好设置
            if (preferences.containsKey("privacyPublicProfile")) {
                preference.setPrivacyPublicProfile(preferences.get("privacyPublicProfile"));
            }
            if (preferences.containsKey("privacyShowEmail")) {
                preference.setPrivacyShowEmail(preferences.get("privacyShowEmail"));
            }
            if (preferences.containsKey("privacyShowPhone")) {
                preference.setPrivacyShowPhone(preferences.get("privacyShowPhone"));
            }
            if (preferences.containsKey("privacyShowPurchaseHistory")) {
                preference.setPrivacyShowPurchaseHistory(preferences.get("privacyShowPurchaseHistory"));
            }
            
            if (preference.getId() == null) {
                customerMapper.insertPreference(preference);
            } else {
                customerMapper.updatePreference(preference);
            }
            
            log.info("更新客户隐私偏好设置成功: customerId={}", customerId);
        } catch (Exception e) {
            log.error("更新客户隐私偏好设置失败: customerId={}", customerId, e);
            throw new RuntimeException("更新客户隐私偏好设置失败", e);
        }
    }
    
    // ==================== 客户统计和分析 ====================
    
    @Override
    public CustomerStats getCustomerStats(Long customerId) {
        try {
            return customerMapper.findStatsByCustomerId(customerId);
        } catch (Exception e) {
            log.error("获取客户统计信息失败: customerId={}", customerId, e);
            throw new RuntimeException("获取客户统计信息失败", e);
        }
    }
    
    @Override
    public Map<String, Object> getCustomerActivityAnalysis(Long customerId) {
        try {
            CustomerStats stats = customerMapper.findStatsByCustomerId(customerId);
            Map<String, Object> analysis = new HashMap<>();
            
            if (stats != null) {
                analysis.put("daysSinceLastLogin", stats.getDaysSinceLastLogin());
                analysis.put("daysSinceLastPurchase", stats.getDaysSinceLastPurchase());
                analysis.put("purchaseFrequency", stats.getPurchaseFrequency());
                analysis.put("totalOrders", stats.getTotalOrders());
                analysis.put("totalSpent", stats.getTotalSpent());
                
                // 计算活跃度评分
                double activityScore = calculateActivityScore(stats);
                analysis.put("activityScore", activityScore);
                analysis.put("activityLevel", getActivityLevel(activityScore));
            }
            
            return analysis;
        } catch (Exception e) {
            log.error("获取客户活跃度分析失败: customerId={}", customerId, e);
            throw new RuntimeException("获取客户活跃度分析失败", e);
        }
    }
    
    @Override
    public Map<String, Object> getCustomerPurchaseBehavior(Long customerId) {
        try {
            CustomerStats stats = customerMapper.findStatsByCustomerId(customerId);
            Map<String, Object> behavior = new HashMap<>();
            
            if (stats != null) {
                behavior.put("averageOrderValue", stats.getAverageOrderValue());
                behavior.put("purchaseFrequency", stats.getPurchaseFrequency());
                behavior.put("totalSpent", stats.getTotalSpent());
                behavior.put("customerLifetimeValue", stats.getCustomerLifetimeValue());
                behavior.put("customerTier", stats.getCustomerTier());
                behavior.put("loyaltyLevel", stats.getLoyaltyLevel());
            }
            
            return behavior;
        } catch (Exception e) {
            log.error("获取客户购买行为分析失败: customerId={}", customerId, e);
            throw new RuntimeException("获取客户购买行为分析失败", e);
        }
    }
    
    @Override
    public Map<String, Object> getCustomerValueAnalysis(Long customerId) {
        try {
            CustomerStats stats = customerMapper.findStatsByCustomerId(customerId);
            Map<String, Object> valueAnalysis = new HashMap<>();
            
            if (stats != null) {
                valueAnalysis.put("customerLifetimeValue", stats.getCustomerLifetimeValue());
                valueAnalysis.put("averageOrderValue", stats.getAverageOrderValue());
                valueAnalysis.put("purchaseFrequency", stats.getPurchaseFrequency());
                valueAnalysis.put("retentionRate", calculateRetentionRate(stats));
                valueAnalysis.put("profitability", calculateProfitability(stats));
                valueAnalysis.put("valueCategory", getValueCategory(stats.getCustomerLifetimeValue()));
            }
            
            return valueAnalysis;
        } catch (Exception e) {
            log.error("获取客户价值分析失败: customerId={}", customerId, e);
            throw new RuntimeException("获取客户价值分析失败", e);
        }
    }
    
    @Override
    public Map<String, Object> getCustomerLifecycleAnalysis(Long customerId) {
        try {
            Customer customer = customerMapper.findById(customerId);
            CustomerStats stats = customerMapper.findStatsByCustomerId(customerId);
            Map<String, Object> lifecycle = new HashMap<>();
            
            if (customer != null && stats != null) {
                lifecycle.put("registrationDate", customer.getRegistrationDate());
                lifecycle.put("lastLoginDate", customer.getLastLoginDate());
                lifecycle.put("lastPurchaseDate", customer.getLastPurchaseDate());
                lifecycle.put("customerCategory", stats.getCustomerCategory());
                lifecycle.put("lifecycleStage", determineLifecycleStage(customer, stats));
                lifecycle.put("daysSinceRegistration", calculateDaysSinceRegistration(customer));
            }
            
            return lifecycle;
        } catch (Exception e) {
            log.error("获取客户生命周期分析失败: customerId={}", customerId, e);
            throw new RuntimeException("获取客户生命周期分析失败", e);
        }
    }
    
    // ==================== 客户标签和分类 ====================
    
    @Override
    @Transactional
    public void addCustomerTag(Long customerId, String tag) {
        try {
            String currentTags = customerMapper.findTagsByCustomerId(customerId);
            String newTags = currentTags == null ? tag : currentTags + "," + tag;
            customerMapper.updateCustomerTags(customerId, newTags);
            log.info("添加客户标签成功: customerId={}, tag={}", customerId, tag);
        } catch (Exception e) {
            log.error("添加客户标签失败: customerId={}, tag={}", customerId, tag, e);
            throw new RuntimeException("添加客户标签失败", e);
        }
    }
    
    @Override
    @Transactional
    public void removeCustomerTag(Long customerId, String tag) {
        try {
            String currentTags = customerMapper.findTagsByCustomerId(customerId);
            if (currentTags != null && currentTags.contains(tag)) {
                String newTags = currentTags.replace("," + tag, "").replace(tag + ",", "").replace(tag, "");
                customerMapper.updateCustomerTags(customerId, newTags);
                log.info("移除客户标签成功: customerId={}, tag={}", customerId, tag);
            }
        } catch (Exception e) {
            log.error("移除客户标签失败: customerId={}, tag={}", customerId, tag, e);
            throw new RuntimeException("移除客户标签失败", e);
        }
    }
    
    @Override
    public List<String> getCustomerTags(Long customerId) {
        try {
            String tags = customerMapper.findTagsByCustomerId(customerId);
            if (tags != null && !tags.trim().isEmpty()) {
                return Arrays.asList(tags.split(","));
            }
            return new ArrayList<>();
        } catch (Exception e) {
            log.error("获取客户标签失败: customerId={}", customerId, e);
            throw new RuntimeException("获取客户标签失败", e);
        }
    }
    
    @Override
    public List<Customer> findCustomersByTag(String tag) {
        try {
            return customerMapper.findCustomersByTag(tag);
        } catch (Exception e) {
            log.error("根据标签查询客户失败: tag={}", tag, e);
            throw new RuntimeException("根据标签查询客户失败", e);
        }
    }
    
    @Override
    public String classifyCustomer(Long customerId) {
        try {
            CustomerStats stats = customerMapper.findStatsByCustomerId(customerId);
            if (stats == null) {
                return "new";
            }
            
            Integer daysSinceLastPurchase = stats.getDaysSinceLastPurchase();
            Integer purchaseFrequency = stats.getPurchaseFrequency();
            
            if (daysSinceLastPurchase == null || daysSinceLastPurchase <= 30) {
                return "active";
            } else if (daysSinceLastPurchase <= 90) {
                return "silent";
            } else {
                return "churned";
            }
        } catch (Exception e) {
            log.error("客户分类失败: customerId={}", customerId, e);
            throw new RuntimeException("客户分类失败", e);
        }
    }
    
    @Override
    public List<Customer> findCustomersByCategory(String category) {
        try {
            return customerMapper.findCustomersByCategory(category);
        } catch (Exception e) {
            log.error("根据分类查询客户失败: category={}", category, e);
            throw new RuntimeException("根据分类查询客户失败", e);
        }
    }
    
    // ==================== 客户关系管理 ====================
    
    @Override
    public Map<String, Object> getCustomerNetwork(Long customerId) {
        try {
            Map<String, Object> network = new HashMap<>();
            List<Customer> referrals = customerMapper.findCustomerReferrals(customerId);
            
            network.put("referrals", referrals);
            network.put("referralCount", referrals.size());
            network.put("networkSize", calculateNetworkSize(customerId));
            
            return network;
        } catch (Exception e) {
            log.error("获取客户关系网络失败: customerId={}", customerId, e);
            throw new RuntimeException("获取客户关系网络失败", e);
        }
    }
    
    @Override
    public List<Customer> getCustomerReferrals(Long customerId) {
        try {
            return customerMapper.findCustomerReferrals(customerId);
        } catch (Exception e) {
            log.error("获取客户推荐关系失败: customerId={}", customerId, e);
            throw new RuntimeException("获取客户推荐关系失败", e);
        }
    }
    
    @Override
    @Transactional
    public void addCustomerReferral(Long referrerId, Long referredId) {
        try {
            customerMapper.insertCustomerReferral(referrerId, referredId);
            log.info("添加客户推荐关系成功: referrerId={}, referredId={}", referrerId, referredId);
        } catch (Exception e) {
            log.error("添加客户推荐关系失败: referrerId={}, referredId={}", referrerId, referredId, e);
            throw new RuntimeException("添加客户推荐关系失败", e);
        }
    }
    
    @Override
    public Map<String, Object> getCustomerSocialInfluence(Long customerId) {
        try {
            Map<String, Object> influence = new HashMap<>();
            List<Customer> referrals = customerMapper.findCustomerReferrals(customerId);
            
            influence.put("referralCount", referrals.size());
            influence.put("influenceScore", calculateInfluenceScore(referrals));
            influence.put("socialTier", determineSocialTier(referrals.size()));
            
            return influence;
        } catch (Exception e) {
            log.error("获取客户社交影响力分析失败: customerId={}", customerId, e);
            throw new RuntimeException("获取客户社交影响力分析失败", e);
        }
    }
    
    // ==================== 客户服务和关怀 ====================
    
    @Override
    public List<Map<String, Object>> getCustomerServiceRecords(Long customerId) {
        try {
            // 这里需要实现获取客户服务记录的逻辑
            // 暂时返回空列表
            return new ArrayList<>();
        } catch (Exception e) {
            log.error("获取客户服务记录失败: customerId={}", customerId, e);
            throw new RuntimeException("获取客户服务记录失败", e);
        }
    }
    
    @Override
    public void addCustomerServiceRecord(Long customerId, String serviceType, String description) {
        try {
            // 这里需要实现添加客户服务记录的逻辑
            log.info("添加客户服务记录: customerId={}, serviceType={}", customerId, serviceType);
        } catch (Exception e) {
            log.error("添加客户服务记录失败: customerId={}, serviceType={}", customerId, serviceType, e);
            throw new RuntimeException("添加客户服务记录失败", e);
        }
    }
    
    @Override
    public Map<String, Object> getCustomerSatisfactionAnalysis(Long customerId) {
        try {
            CustomerStats stats = customerMapper.findStatsByCustomerId(customerId);
            Map<String, Object> satisfaction = new HashMap<>();
            
            if (stats != null) {
                satisfaction.put("satisfactionScore", stats.getSatisfactionScore());
                satisfaction.put("complaints", stats.getComplaintsFiled());
                satisfaction.put("returns", stats.getReturnsRequested());
                satisfaction.put("satisfactionLevel", determineSatisfactionLevel(stats.getSatisfactionScore()));
            }
            
            return satisfaction;
        } catch (Exception e) {
            log.error("获取客户满意度分析失败: customerId={}", customerId, e);
            throw new RuntimeException("获取客户满意度分析失败", e);
        }
    }
    
    @Override
    public void sendCustomerCareMessage(Long customerId, String messageType, String content) {
        try {
            // 这里需要实现发送客户关怀消息的逻辑
            log.info("发送客户关怀消息: customerId={}, messageType={}", customerId, messageType);
        } catch (Exception e) {
            log.error("发送客户关怀消息失败: customerId={}, messageType={}", customerId, messageType, e);
            throw new RuntimeException("发送客户关怀消息失败", e);
        }
    }
    
    @Override
    public List<Map<String, Object>> getCustomerCarePlan(Long customerId) {
        try {
            // 这里需要实现获取客户关怀计划的逻辑
            // 暂时返回空列表
            return new ArrayList<>();
        } catch (Exception e) {
            log.error("获取客户关怀计划失败: customerId={}", customerId, e);
            throw new RuntimeException("获取客户关怀计划失败", e);
        }
    }
    
    // ==================== 客户生命周期管理 ====================
    
    @Override
    @Transactional
    public void registerCustomer(Customer customer) {
        try {
            customer.setStatus("active");
            customer.setRegistrationDate(java.time.LocalDateTime.now());
            customerMapper.insert(customer);
            
            // 初始化客户统计信息
            CustomerStats stats = new CustomerStats();
            stats.setCustomerId(customer.getId());
            stats.setTotalOrders(0);
            stats.setTotalSpent(0.0);
            stats.setCustomerCategory("new");
            customerMapper.insertStats(stats);
            
            log.info("客户注册成功: id={}, name={}", customer.getId(), customer.getName());
        } catch (Exception e) {
            log.error("客户注册失败: name={}", customer.getName(), e);
            throw new RuntimeException("客户注册失败", e);
        }
    }
    
    @Override
    @Transactional
    public void activateCustomer(Long customerId) {
        try {
            Customer customer = customerMapper.findById(customerId);
            if (customer != null) {
                customer.setStatus("active");
                customerMapper.update(customer);
                log.info("客户激活成功: id={}", customerId);
            }
        } catch (Exception e) {
            log.error("客户激活失败: customerId={}", customerId, e);
            throw new RuntimeException("客户激活失败", e);
        }
    }
    
    @Override
    public void handleInactiveCustomer(Long customerId) {
        try {
            Customer customer = customerMapper.findById(customerId);
            if (customer != null) {
                customer.setStatus("inactive");
                customerMapper.update(customer);
                log.info("客户沉默处理成功: id={}", customerId);
            }
        } catch (Exception e) {
            log.error("客户沉默处理失败: customerId={}", customerId, e);
            throw new RuntimeException("客户沉默处理失败", e);
        }
    }
    
    @Override
    public void sendChurnWarning(Long customerId) {
        try {
            // 这里需要实现发送客户流失预警的逻辑
            log.info("发送客户流失预警: customerId={}", customerId);
        } catch (Exception e) {
            log.error("发送客户流失预警失败: customerId={}", customerId, e);
            throw new RuntimeException("发送客户流失预警失败", e);
        }
    }
    
    @Override
    public void implementRetentionStrategy(Long customerId, String strategy) {
        try {
            // 这里需要实现客户挽回策略的逻辑
            log.info("实施客户挽回策略: customerId={}, strategy={}", customerId, strategy);
        } catch (Exception e) {
            log.error("实施客户挽回策略失败: customerId={}, strategy={}", customerId, strategy, e);
            throw new RuntimeException("实施客户挽回策略失败", e);
        }
    }
    
    // ==================== 批量操作 ====================
    
    @Override
    @Transactional
    public void batchUpdateCustomerStatus(List<Long> customerIds, String status) {
        try {
            customerMapper.batchUpdateCustomerStatus(customerIds, status);
            log.info("批量更新客户状态成功: count={}, status={}", customerIds.size(), status);
        } catch (Exception e) {
            log.error("批量更新客户状态失败: count={}, status={}", customerIds.size(), status, e);
            throw new RuntimeException("批量更新客户状态失败", e);
        }
    }
    
    @Override
    @Transactional
    public void batchAddCustomerTags(List<Long> customerIds, List<String> tags) {
        try {
            for (String tag : tags) {
                customerMapper.batchAddCustomerTag(customerIds, tag);
            }
            log.info("批量添加客户标签成功: customerCount={}, tagCount={}", customerIds.size(), tags.size());
        } catch (Exception e) {
            log.error("批量添加客户标签失败: customerCount={}, tagCount={}", customerIds.size(), tags.size(), e);
            throw new RuntimeException("批量添加客户标签失败", e);
        }
    }
    
    @Override
    public void batchSendCustomerMessages(List<Long> customerIds, String messageType, String content) {
        try {
            // 这里需要实现批量发送客户消息的逻辑
            log.info("批量发送客户消息: customerCount={}, messageType={}", customerIds.size(), messageType);
        } catch (Exception e) {
            log.error("批量发送客户消息失败: customerCount={}, messageType={}", customerIds.size(), messageType, e);
            throw new RuntimeException("批量发送客户消息失败", e);
        }
    }
    
    @Override
    public void batchImportCustomers(List<Customer> customers) {
        try {
            for (Customer customer : customers) {
                customerMapper.insert(customer);
            }
            log.info("批量导入客户数据成功: count={}", customers.size());
        } catch (Exception e) {
            log.error("批量导入客户数据失败: count={}", customers.size(), e);
            throw new RuntimeException("批量导入客户数据失败", e);
        }
    }
    
    @Override
    public List<Map<String, Object>> batchExportCustomers(List<Long> customerIds) {
        try {
            List<Map<String, Object>> exportData = new ArrayList<>();
            for (Long customerId : customerIds) {
                Customer customer = customerMapper.findById(customerId);
                if (customer != null) {
                    Map<String, Object> customerData = new HashMap<>();
                    customerData.put("id", customer.getId());
                    customerData.put("name", customer.getName());
                    customerData.put("email", customer.getEmail());
                    customerData.put("phone", customer.getPhone());
                    customerData.put("status", customer.getStatus());
                    customerData.put("registrationDate", customer.getRegistrationDate());
                    exportData.add(customerData);
                }
            }
            log.info("批量导出客户数据成功: count={}", exportData.size());
            return exportData;
        } catch (Exception e) {
            log.error("批量导出客户数据失败: count={}", customerIds.size(), e);
            throw new RuntimeException("批量导出客户数据失败", e);
        }
    }
    
    // ==================== 高级分析功能 ====================
    
    @Override
    public Map<String, Object> getCustomerSegmentationAnalysis() {
        try {
            Map<String, Object> segmentation = new HashMap<>();
            
            // 这里需要实现客户细分分析的逻辑
            // 暂时返回示例数据
            segmentation.put("segments", Arrays.asList("高价值客户", "活跃客户", "新客户", "沉默客户"));
            segmentation.put("segmentCounts", Map.of(
                "高价值客户", 150,
                "活跃客户", 500,
                "新客户", 200,
                "沉默客户", 100
            ));
            
            return segmentation;
        } catch (Exception e) {
            log.error("获取客户细分分析失败", e);
            throw new RuntimeException("获取客户细分分析失败", e);
        }
    }
    
    @Override
    public Map<String, Object> getCustomerChurnPrediction(Long customerId) {
        try {
            CustomerStats stats = customerMapper.findStatsByCustomerId(customerId);
            Map<String, Object> prediction = new HashMap<>();
            
            if (stats != null) {
                double churnProbability = calculateChurnProbability(stats);
                prediction.put("churnProbability", churnProbability);
                prediction.put("churnRisk", getChurnRiskLevel(churnProbability));
                prediction.put("predictedChurnDate", predictChurnDate(stats));
            }
            
            return prediction;
        } catch (Exception e) {
            log.error("获取客户流失预测失败: customerId={}", customerId, e);
            throw new RuntimeException("获取客户流失预测失败", e);
        }
    }
    
    @Override
    public Map<String, Object> getCustomerLifetimeValuePrediction(Long customerId) {
        try {
            CustomerStats stats = customerMapper.findStatsByCustomerId(customerId);
            Map<String, Object> prediction = new HashMap<>();
            
            if (stats != null) {
                double predictedLTV = predictLifetimeValue(stats);
                prediction.put("predictedLifetimeValue", predictedLTV);
                prediction.put("currentLifetimeValue", stats.getCustomerLifetimeValue());
                prediction.put("growthPotential", calculateGrowthPotential(stats));
            }
            
            return prediction;
        } catch (Exception e) {
            log.error("获取客户生命周期价值预测失败: customerId={}", customerId, e);
            throw new RuntimeException("获取客户生命周期价值预测失败", e);
        }
    }
    
    @Override
    public Map<String, Object> getCustomerBehaviorPatternAnalysis(Long customerId) {
        try {
            Map<String, Object> patternAnalysis = new HashMap<>();
            
            // 这里需要实现客户行为模式分析的逻辑
            // 暂时返回示例数据
            patternAnalysis.put("purchasePattern", "regular");
            patternAnalysis.put("preferredCategories", Arrays.asList("电子产品", "家居用品"));
            patternAnalysis.put("shoppingTime", "evening");
            patternAnalysis.put("paymentMethod", "credit_card");
            
            return patternAnalysis;
        } catch (Exception e) {
            log.error("获取客户行为模式分析失败: customerId={}", customerId, e);
            throw new RuntimeException("获取客户行为模式分析失败", e);
        }
    }
    
    @Override
    public Map<String, Object> getCustomerReferralValueAnalysis(Long customerId) {
        try {
            Map<String, Object> referralAnalysis = new HashMap<>();
            List<Customer> referrals = customerMapper.findCustomerReferrals(customerId);
            
            double totalReferralValue = calculateTotalReferralValue(referrals);
            referralAnalysis.put("referralCount", referrals.size());
            referralAnalysis.put("totalReferralValue", totalReferralValue);
            referralAnalysis.put("averageReferralValue", totalReferralValue / Math.max(referrals.size(), 1));
            referralAnalysis.put("referralEfficiency", calculateReferralEfficiency(referrals));
            
            return referralAnalysis;
        } catch (Exception e) {
            log.error("获取客户推荐价值分析失败: customerId={}", customerId, e);
            throw new RuntimeException("获取客户推荐价值分析失败", e);
        }
    }
    
    // ==================== 辅助方法 ====================
    
    private double calculateActivityScore(CustomerStats stats) {
        // 简化的活跃度评分计算
        double score = 0;
        if (stats.getDaysSinceLastLogin() != null && stats.getDaysSinceLastLogin() <= 7) score += 30;
        if (stats.getDaysSinceLastPurchase() != null && stats.getDaysSinceLastPurchase() <= 30) score += 40;
        if (stats.getPurchaseFrequency() != null && stats.getPurchaseFrequency() >= 3) score += 30;
        return score;
    }
    
    private String getActivityLevel(double score) {
        if (score >= 80) return "high";
        if (score >= 50) return "medium";
        return "low";
    }
    
    private double calculateRetentionRate(CustomerStats stats) {
        // 简化的留存率计算
        return stats.getTotalOrders() > 0 ? 0.85 : 0.0;
    }
    
    private double calculateProfitability(CustomerStats stats) {
        // 简化的盈利能力计算
        return stats.getTotalSpent() != null ? stats.getTotalSpent() * 0.2 : 0.0;
    }
    
    private String getValueCategory(double lifetimeValue) {
        if (lifetimeValue >= 10000) return "high";
        if (lifetimeValue >= 1000) return "medium";
        return "low";
    }
    
    private String determineLifecycleStage(Customer customer, CustomerStats stats) {
        if (stats.getDaysSinceLastPurchase() == null || stats.getDaysSinceLastPurchase() <= 30) {
            return "active";
        } else if (stats.getDaysSinceLastPurchase() <= 90) {
            return "at_risk";
        } else {
            return "churned";
        }
    }
    
    private long calculateDaysSinceRegistration(Customer customer) {
        if (customer.getRegistrationDate() == null) return 0;
        return java.time.temporal.ChronoUnit.DAYS.between(
            customer.getRegistrationDate(), java.time.LocalDateTime.now()
        );
    }
    
    private int calculateNetworkSize(Long customerId) {
        // 简化的网络规模计算
        return 10; // 示例值
    }
    
    private double calculateInfluenceScore(List<Customer> referrals) {
        // 简化的影响力评分计算
        return referrals.size() * 10.0;
    }
    
    private String determineSocialTier(int referralCount) {
        if (referralCount >= 10) return "influencer";
        if (referralCount >= 5) return "advocate";
        return "regular";
    }
    
    private String determineSatisfactionScore(Double score) {
        if (score == null) return "unknown";
        if (score >= 4.5) return "very_satisfied";
        if (score >= 3.5) return "satisfied";
        if (score >= 2.5) return "neutral";
        return "dissatisfied";
    }
    
    private double calculateChurnProbability(CustomerStats stats) {
        // 简化的流失概率计算
        if (stats.getDaysSinceLastPurchase() == null) return 0.1;
        if (stats.getDaysSinceLastPurchase() > 90) return 0.8;
        if (stats.getDaysSinceLastPurchase() > 60) return 0.5;
        return 0.1;
    }
    
    private String getChurnRiskLevel(double probability) {
        if (probability >= 0.7) return "high";
        if (probability >= 0.4) return "medium";
        return "low";
    }
    
    private String predictChurnDate(CustomerStats stats) {
        // 简化的流失日期预测
        return java.time.LocalDateTime.now().plusDays(30).toString();
    }
    
    private double predictLifetimeValue(CustomerStats stats) {
        // 简化的生命周期价值预测
        return stats.getCustomerLifetimeValue() != null ? 
               stats.getCustomerLifetimeValue() * 1.2 : 1000.0;
    }
    
    private double calculateGrowthPotential(CustomerStats stats) {
        // 简化的增长潜力计算
        return 0.3; // 示例值
    }
    
    private double calculateTotalReferralValue(List<Customer> referrals) {
        // 简化的推荐总价值计算
        return referrals.size() * 500.0;
    }
    
    private double calculateReferralEfficiency(List<Customer> referrals) {
        // 简化的推荐效率计算
        return referrals.size() > 0 ? 0.75 : 0.0;
    }
}