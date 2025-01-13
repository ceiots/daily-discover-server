package com.example.model;

import java.util.List;
import lombok.Data;
@Data
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
}