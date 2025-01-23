package com.example.model;

import java.util.List;

import com.example.config.ImageConfig;
public class Recommendation {
    private Long id;
    private String title;
    private String imageUrl;
    private String shopName;
    private String shopAvatarUrl;
    private Double price;
    private Integer soldCount;
    private String productDetails; // 产品详情
    private List<String> specifications; // 规格参数
    private String purchaseNotice; // 购买须知
    private String storeDescription; // 店铺描述
    private List<Comment> comments; // 用户评论
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public String getImageUrl() {
        return imageUrl;
    }
    public void setImageUrl(String imageUrl) {
        this.imageUrl = ImageConfig.getImagePrefix() + imageUrl;
    }
    public String getShopName() {
        return shopName;
    }
    public void setShopName(String shopName) {
        this.shopName = shopName;
    }
    public String getShopAvatarUrl() {
        return shopAvatarUrl;
    }
    public void setShopAvatarUrl(String shopAvatarUrl) {
        this.shopAvatarUrl = ImageConfig.getImagePrefix() + shopAvatarUrl;
    }
    public Double getPrice() {
        return price;
    }
    public void setPrice(Double price) {
        this.price = price;
    }
    public Integer getSoldCount() {
        return soldCount;
    }
    public void setSoldCount(Integer soldCount) {
        this.soldCount = soldCount;
    }
    public String getProductDetails() {
        return productDetails;
    }
    public void setProductDetails(String productDetails) {
        this.productDetails = productDetails;
    }
    public List<String> getSpecifications() {
        return specifications;
    }
    public void setSpecifications(List<String> specifications) {
        this.specifications = specifications;
    }
    public String getPurchaseNotice() {
        return purchaseNotice;
    }
    public void setPurchaseNotice(String purchaseNotice) {
        this.purchaseNotice = purchaseNotice;
    }
    public String getStoreDescription() {
        return storeDescription;
    }
    public void setStoreDescription(String storeDescription) {
        this.storeDescription = storeDescription;
    }
    public List<Comment> getComments() {
        return comments;
    }
    public void setComments(List<Comment> comments) {
        this.comments = comments;
    }

    
}