package com.example.dto;

import com.example.model.CartItem;
import java.util.List;

public class ShopCartGroupDTO {
    private Long shopId;
    private String shopName;
    private String shopAvatarUrl;
    private List<CartItem> items;

    public Long getShopId() { return shopId; }
    public void setShopId(Long shopId) { this.shopId = shopId; }

    public String getShopName() { return shopName; }
    public void setShopName(String shopName) { this.shopName = shopName; }

    public String getShopAvatarUrl() { return shopAvatarUrl; }
    public void setShopAvatarUrl(String shopAvatarUrl) { this.shopAvatarUrl = shopAvatarUrl; }

    public List<CartItem> getItems() { return items; }
    public void setItems(List<CartItem> items) { this.items = items; }
} 