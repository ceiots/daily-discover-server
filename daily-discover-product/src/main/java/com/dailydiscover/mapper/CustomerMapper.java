package com.dailydiscover.mapper;

import com.dailydiscover.model.Customer;
import com.dailydiscover.model.CustomerAddress;
import com.dailydiscover.model.CustomerPreference;
import com.dailydiscover.model.CustomerStats;
import org.apache.ibatis.annotations.*;
import java.util.List;
import java.util.Map;

/**
 * 客户数据访问接口
 */
@Mapper
public interface CustomerMapper {
    
    // ==================== 客户基础信息管理 ====================
    
    @Select("SELECT * FROM customers WHERE id = #{id}")
    Customer findById(Long id);
    
    @Select("SELECT * FROM customers WHERE user_id = #{userId}")
    Customer findByUserId(Long userId);
    
    @Insert("INSERT INTO customers (user_id, customer_code, name, email, phone, avatar, gender, birthday, " +
            "membership_level, points, status, registration_date, last_login_date, last_purchase_date, " +
            "total_orders, total_spent, tags, preferences, notes, created_at, updated_at) " +
            "VALUES (#{userId}, #{customerCode}, #{name}, #{email}, #{phone}, #{avatar}, #{gender}, #{birthday}, " +
            "#{membershipLevel}, #{points}, #{status}, #{registrationDate}, #{lastLoginDate}, #{lastPurchaseDate}, " +
            "#{totalOrders}, #{totalSpent}, #{tags}, #{preferences}, #{notes}, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insert(Customer customer);
    
    @Update("UPDATE customers SET name = #{name}, email = #{email}, phone = #{phone}, avatar = #{avatar}, " +
            "gender = #{gender}, birthday = #{birthday}, membership_level = #{membershipLevel}, points = #{points}, " +
            "status = #{status}, last_login_date = #{lastLoginDate}, last_purchase_date = #{lastPurchaseDate}, " +
            "total_orders = #{totalOrders}, total_spent = #{totalSpent}, tags = #{tags}, preferences = #{preferences}, " +
            "notes = #{notes}, updated_at = CURRENT_TIMESTAMP WHERE id = #{id}")
    void update(Customer customer);
    
    @Delete("DELETE FROM customers WHERE id = #{id}")
    void delete(Long id);
    
    @Select("SELECT * FROM customers ORDER BY created_at DESC")
    List<Customer> findAll();
    
    @Select("<script>" +
            "SELECT * FROM customers WHERE 1=1 " +
            "<if test='name != null'>AND name LIKE CONCAT('%', #{name}, '%')</if>" +
            "<if test='email != null'>AND email = #{email}</if>" +
            "<if test='status != null'>AND status = #{status}</if>" +
            "<if test='membershipLevel != null'>AND membership_level = #{membershipLevel}</if>" +
            "<if test='minPoints != null'>AND points >= #{minPoints}</if>" +
            "<if test='maxPoints != null'>AND points <= #{maxPoints}</if>" +
            "ORDER BY created_at DESC" +
            "</script>")
    List<Customer> findByCriteria(Map<String, Object> criteria);
    
    @Select("SELECT * FROM customers ORDER BY created_at DESC LIMIT #{size} OFFSET #{offset}")
    List<Customer> findPaginated(@Param("offset") int offset, @Param("size") int size);
    
    @Select("SELECT COUNT(*) FROM customers")
    Long countAll();
    
    // ==================== 客户地址管理 ====================
    
    @Select("SELECT * FROM customer_addresses WHERE customer_id = #{customerId} ORDER BY is_default DESC, created_at DESC")
    List<CustomerAddress> findAddressesByCustomerId(Long customerId);
    
    @Select("SELECT * FROM customer_addresses WHERE customer_id = #{customerId} AND is_default = true")
    CustomerAddress findDefaultAddress(Long customerId);
    
    @Insert("INSERT INTO customer_addresses (customer_id, address_type, recipient_name, phone, province, city, " +
            "district, street, detail_address, postal_code, is_default, notes, created_at, updated_at) " +
            "VALUES (#{customerId}, #{addressType}, #{recipientName}, #{phone}, #{province}, #{city}, " +
            "#{district}, #{street}, #{detailAddress}, #{postalCode}, #{isDefault}, #{notes}, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insertAddress(CustomerAddress address);
    
    @Update("UPDATE customer_addresses SET address_type = #{addressType}, recipient_name = #{recipientName}, " +
            "phone = #{phone}, province = #{province}, city = #{city}, district = #{district}, street = #{street}, " +
            "detail_address = #{detailAddress}, postal_code = #{postalCode}, is_default = #{isDefault}, " +
            "notes = #{notes}, updated_at = CURRENT_TIMESTAMP WHERE id = #{id}")
    void updateAddress(CustomerAddress address);
    
    @Delete("DELETE FROM customer_addresses WHERE id = #{id}")
    void deleteAddress(Long id);
    
    @Update("UPDATE customer_addresses SET is_default = false WHERE customer_id = #{customerId}")
    void clearDefaultAddresses(Long customerId);
    
    @Update("UPDATE customer_addresses SET is_default = true WHERE id = #{addressId}")
    void setAddressAsDefault(Long addressId);
    
    // ==================== 客户偏好设置 ====================
    
    @Select("SELECT * FROM customer_preferences WHERE customer_id = #{customerId}")
    CustomerPreference findPreferenceByCustomerId(Long customerId);
    
    @Insert("INSERT INTO customer_preferences (customer_id, language, currency, timezone, theme, " +
            "email_notifications, sms_notifications, push_notifications, marketing_emails, " +
            "product_recommendations, price_alerts, order_updates, privacy_public_profile, " +
            "privacy_show_email, privacy_show_phone, privacy_show_purchase_history, " +
            "preferred_categories, preferred_brands, preferred_price_range, " +
            "shopping_preferences, communication_preferences, created_at, updated_at) " +
            "VALUES (#{customerId}, #{language}, #{currency}, #{timezone}, #{theme}, " +
            "#{emailNotifications}, #{smsNotifications}, #{pushNotifications}, #{marketingEmails}, " +
            "#{productRecommendations}, #{priceAlerts}, #{orderUpdates}, #{privacyPublicProfile}, " +
            "#{privacyShowEmail}, #{privacyShowPhone}, #{privacyShowPurchaseHistory}, " +
            "#{preferredCategories}, #{preferredBrands}, #{preferredPriceRange}, " +
            "#{shoppingPreferences}, #{communicationPreferences}, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insertPreference(CustomerPreference preference);
    
    @Update("UPDATE customer_preferences SET language = #{language}, currency = #{currency}, timezone = #{timezone}, " +
            "theme = #{theme}, email_notifications = #{emailNotifications}, sms_notifications = #{smsNotifications}, " +
            "push_notifications = #{pushNotifications}, marketing_emails = #{marketingEmails}, " +
            "product_recommendations = #{productRecommendations}, price_alerts = #{priceAlerts}, " +
            "order_updates = #{orderUpdates}, privacy_public_profile = #{privacyPublicProfile}, " +
            "privacy_show_email = #{privacyShowEmail}, privacy_show_phone = #{privacyShowPhone}, " +
            "privacy_show_purchase_history = #{privacyShowPurchaseHistory}, preferred_categories = #{preferredCategories}, " +
            "preferred_brands = #{preferredBrands}, preferred_price_range = #{preferredPriceRange}, " +
            "shopping_preferences = #{shoppingPreferences}, communication_preferences = #{communicationPreferences}, " +
            "updated_at = CURRENT_TIMESTAMP WHERE customer_id = #{customerId}")
    void updatePreference(CustomerPreference preference);
    
    // ==================== 客户统计和分析 ====================
    
    @Select("SELECT * FROM customer_stats WHERE customer_id = #{customerId}")
    CustomerStats findStatsByCustomerId(Long customerId);
    
    @Insert("INSERT INTO customer_stats (customer_id, total_orders, total_spent, average_order_value, " +
            "favorite_products, wishlist_items, reviews_written, questions_asked, returns_requested, " +
            "complaints_filed, referrals_made, points_earned, points_redeemed, coupons_used, loyalty_level, " +
            "customer_tier, satisfaction_score, days_since_last_purchase, days_since_last_login, " +
            "purchase_frequency, customer_lifetime_value, customer_category, last_updated_at) " +
            "VALUES (#{customerId}, #{totalOrders}, #{totalSpent}, #{averageOrderValue}, " +
            "#{favoriteProducts}, #{wishlistItems}, #{reviewsWritten}, #{questionsAsked}, #{returnsRequested}, " +
            "#{complaintsFiled}, #{referralsMade}, #{pointsEarned}, #{pointsRedeemed}, #{couponsUsed}, " +
            "#{loyaltyLevel}, #{customerTier}, #{satisfactionScore}, #{daysSinceLastPurchase}, " +
            "#{daysSinceLastLogin}, #{purchaseFrequency}, #{customerLifetimeValue}, #{customerCategory}, CURRENT_TIMESTAMP)")
    void insertStats(CustomerStats stats);
    
    @Update("UPDATE customer_stats SET total_orders = #{totalOrders}, total_spent = #{totalSpent}, " +
            "average_order_value = #{averageOrderValue}, favorite_products = #{favoriteProducts}, " +
            "wishlist_items = #{wishlistItems}, reviews_written = #{reviewsWritten}, questions_asked = #{questionsAsked}, " +
            "returns_requested = #{returnsRequested}, complaints_filed = #{complaintsFiled}, " +
            "referrals_made = #{referralsMade}, points_earned = #{pointsEarned}, points_redeemed = #{pointsRedeemed}, " +
            "coupons_used = #{couponsUsed}, loyalty_level = #{loyaltyLevel}, customer_tier = #{customerTier}, " +
            "satisfaction_score = #{satisfactionScore}, days_since_last_purchase = #{daysSinceLastPurchase}, " +
            "days_since_last_login = #{daysSinceLastLogin}, purchase_frequency = #{purchaseFrequency}, " +
            "customer_lifetime_value = #{customerLifetimeValue}, customer_category = #{customerCategory}, " +
            "last_updated_at = CURRENT_TIMESTAMP WHERE customer_id = #{customerId}")
    void updateStats(CustomerStats stats);
    
    // ==================== 客户标签和分类 ====================
    
    @Select("SELECT tags FROM customers WHERE id = #{customerId}")
    String findTagsByCustomerId(Long customerId);
    
    @Update("UPDATE customers SET tags = #{tags} WHERE id = #{customerId}")
    void updateCustomerTags(@Param("customerId") Long customerId, @Param("tags") String tags);
    
    @Select("<script>" +
            "SELECT * FROM customers WHERE tags LIKE CONCAT('%', #{tag}, '%') " +
            "ORDER BY created_at DESC" +
            "</script>")
    List<Customer> findCustomersByTag(String tag);
    
    @Select("SELECT * FROM customers WHERE customer_category = #{category} ORDER BY created_at DESC")
    List<Customer> findCustomersByCategory(String category);
    
    // ==================== 客户关系管理 ====================
    
    @Select("SELECT * FROM customer_referrals WHERE referrer_id = #{customerId}")
    List<Customer> findCustomerReferrals(Long customerId);
    
    @Insert("INSERT INTO customer_referrals (referrer_id, referred_id, referral_date, status) " +
            "VALUES (#{referrerId}, #{referredId}, CURRENT_TIMESTAMP, 'active')")
    void insertCustomerReferral(@Param("referrerId") Long referrerId, @Param("referredId") Long referredId);
    
    // ==================== 批量操作 ====================
    
    @Update("<script>" +
            "UPDATE customers SET status = #{status}, updated_at = CURRENT_TIMESTAMP WHERE id IN " +
            "<foreach item='id' collection='customerIds' open='(' separator=',' close=')'>" +
            "#{id}" +
            "</foreach>" +
            "</script>")
    void batchUpdateCustomerStatus(@Param("customerIds") List<Long> customerIds, @Param("status") String status);
    
    @Update("<script>" +
            "UPDATE customers SET tags = CONCAT(tags, ',', #{tag}) WHERE id IN " +
            "<foreach item='id' collection='customerIds' open='(' separator=',' close=')'>" +
            "#{id}" +
            "</foreach>" +
            "</script>")
    void batchAddCustomerTag(@Param("customerIds") List<Long> customerIds, @Param("tag") String tag);
}