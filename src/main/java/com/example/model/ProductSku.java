package com.example.model;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import lombok.Data;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * 商品SKU实体类 - 匹配优化后的product_sku表结构
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProductSku {
    private Long id;
    private Long productId;
    
    /**
     * SKU编码，用于外部系统对接和库存管理
     */
    private String skuCode;
    
    /**
     * 条形码
     */
    private String barcode;
    
    /**
     * 销售价格
     */
    private BigDecimal price;
    
    /**
     * 市场价/原价，用于显示折扣
     */
    private BigDecimal originalPrice;
    
    /**
     * 可售库存，实际可以被购买的库存数量
     */
    private Integer stock;
    
    /**
     * 锁定库存，已下单但未支付完成的库存数量
     * 用于防止超卖问题
     */
    private Integer lockedStock;
    
    /**
     * 销售数量
     */
    private Integer salesCount;
    
    /**
     * SKU图片URL
     */
    private String imageUrl;
    
    /**
     * 规格属性，使用JSON存储
     * 例如：{"颜色": "红色", "尺码": "XL"}
     */
    private Map<String, String> specifications;
    
    /**
     * 是否默认选中的SKU
     */
    private Boolean isDefault;
    
    /**
     * 状态: 1-正常，0-下架，2-缺货中
     */
    private Integer status;
    
    /**
     * 创建时间
     */
    private Date createTime;
    
    /**
     * 更新时间
     */
    private Date updateTime;
    
    /**
     * 删除标记
     */
    private Boolean deleted;
    
    /**
     * 关联的商品对象
     */
    @JsonIgnore
    private Product product;
    
    /**
     * 获取SKU的标题，组合商品名称和规格信息
     * @return 格式化的SKU标题
     */
    public String getSkuTitle() {
        StringBuilder title = new StringBuilder();
        
        if (product != null) {
            title.append(product.getTitle());
        }
        
        if (specifications != null && !specifications.isEmpty()) {
            title.append(" ");
            specifications.forEach((key, value) -> title.append(key).append(":").append(value).append(" "));
        }
        
        return title.toString().trim();
    }
    
    /**
     * 获取规格字符串表示
     */
    public String getSpecificationString() {
        if (specifications == null || specifications.isEmpty()) {
            return "";
        }
        
        StringBuilder sb = new StringBuilder();
        specifications.forEach((key, value) -> sb.append(key).append(":").append(value).append("; "));
        return sb.toString().trim();
    }
    
    /**
     * 计算折扣百分比
     * @return 折扣百分比，例如: 85 表示 85折 (15%的折扣)
     */
    public Integer getDiscountPercentage() {
        if (price == null || originalPrice == null || originalPrice.compareTo(BigDecimal.ZERO) <= 0) {
            return 100;
        }
        
        return price.multiply(new BigDecimal(100)).divide(originalPrice, 0, BigDecimal.ROUND_HALF_UP).intValue();
    }
    
    /**
     * 检查SKU是否有库存
     */
    public boolean hasStock() {
        return stock != null && stock > 0;
    }
    
    /**
     * 检查是否有足够的库存可以下单
     * @param quantity 需要的数量
     * @return 如果有足够库存返回true
     */
    public boolean hasEnoughStock(int quantity) {
        return stock != null && stock >= quantity;
    }
    
    /**
     * 锁定库存（下单时调用）
     * 将库存从可售库存转移到锁定库存
     * @param quantity 要锁定的数量
     * @return 是否成功锁定
     */
    public boolean lockStock(int quantity) {
        if (!hasEnoughStock(quantity)) {
            return false;
        }
        
        stock -= quantity;
        lockedStock = (lockedStock == null ? 0 : lockedStock) + quantity;
        return true;
    }
    
    /**
     * 解锁库存（取消订单或支付超时时调用）
     * 将库存从锁定库存返回到可售库存
     * @param quantity 要解锁的数量
     * @return 是否成功解锁
     */
    public boolean unlockStock(int quantity) {
        if (lockedStock == null || lockedStock < quantity) {
            return false;
        }
        
        lockedStock -= quantity;
        stock = (stock == null ? 0 : stock) + quantity;
        return true;
    }
    
    /**
     * 从锁定库存扣减并增加销量（支付成功时调用）
     * @param quantity 要扣减的数量
     * @return 是否成功扣减
     */
    public boolean deductLockedStock(int quantity) {
        if (lockedStock == null || lockedStock < quantity) {
            return false;
        }
        
        lockedStock -= quantity;
        salesCount = (salesCount == null ? 0 : salesCount) + quantity;
        return true;
    }
    
    /**
     * 增加SKU库存
     * @param quantity 要增加的数量
     */
    public void addStock(int quantity) {
        if (quantity <= 0) {
            return;
        }
        
        stock = (stock == null ? 0 : stock) + quantity;
    }
    
    /**
     * 获取显示用的图片URL
     * 如果SKU没有图片，则使用商品图片
     */
    public String getDisplayImageUrl() {
        if (imageUrl != null && !imageUrl.trim().isEmpty()) {
            return imageUrl;
        }
        
        if (product != null && product.getImageUrl() != null) {
            return product.getImageUrl();
        }
        
        return "";
    }
    
    /**
     * 初始化SKU，设置默认值
     */
    public void initialize() {
        if (skuCode == null || skuCode.trim().isEmpty()) {
            ensureSkuCode();
        }
        
        if (stock == null) {
            stock = 0;
        }
        
        if (lockedStock == null) {
            lockedStock = 0;
        }
        
        if (salesCount == null) {
            salesCount = 0;
        }
        
        if (status == null) {
            status = 1;
        }
        
        if (isDefault == null) {
            isDefault = false;
        }
        
        if (deleted == null) {
            deleted = false;
        }
        
        if (createTime == null) {
            createTime = new Date();
        }
    }
    
    /**
     * 检查SKU是否匹配指定的规格
     * @param selectedSpecs 选择的规格
     * @return 如果匹配返回true
     */
    public boolean matchSpecifications(Map<String, String> selectedSpecs) {
        if (specifications == null || selectedSpecs == null) {
            return false;
        }
        
        for (Map.Entry<String, String> entry : selectedSpecs.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            
            if (!specifications.containsKey(key) || !specifications.get(key).equals(value)) {
                return false;
            }
        }
        
        return true;
    }
    
    /**
     * 生成SKU编码
     */
    public static String generateSkuCode(Long productId, Map<String, String> specs) {
        StringBuilder codeBuilder = new StringBuilder();
        codeBuilder.append("SKU").append(productId);
        
        if (specs != null && !specs.isEmpty()) {
            // 按键排序，确保相同规格组合生成相同的编码
            specs.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .forEach(entry -> codeBuilder.append("-").append(entry.getValue().hashCode()));
        } else {
            codeBuilder.append("-DEFAULT");
        }
        
        // 添加随机后缀确保唯一性
        codeBuilder.append("-").append(UUID.randomUUID().toString().substring(0, 8));
        
        return codeBuilder.toString();
    }
    
    /**
     * 确保SKU有编码
     */
    public void ensureSkuCode() {
        if (skuCode == null || skuCode.trim().isEmpty()) {
            skuCode = generateSkuCode(productId, specifications);
        }
    }
} 