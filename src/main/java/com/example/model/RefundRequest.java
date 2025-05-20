package com.example.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Data
@TableName("refund_requests")
public class RefundRequest {
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private String orderNumber;  // 订单编号
    private Long orderId;        // 订单ID
    private Long userId;         // 用户ID
    private Long shopId;         // 店铺ID
    private Integer refundType;  // 退款类型：1-仅退款，2-退货退款
    private Integer reason;      // 退款原因代码
    private String reasonDetail; // 退款原因详情
    private BigDecimal amount;   // 退款金额
    private String images;       // 退款凭证图片，以逗号分隔
    private Integer status;      // 状态：0-待处理，1-商家同意，2-商家拒绝，3-已完成，4-已取消
    private String refusalReason; // 拒绝原因
    private Date createdAt;      // 创建时间
    private Date updatedAt;      // 更新时间
    
    // 非数据库字段
    @TableField(exist = false)
    private List<String> imageList; // 图片列表，用于前端交互
    
    @TableField(exist = false)
    private String statusText;   // 状态文本描述
    
    @TableField(exist = false)
    private String shopName;     // 店铺名称
    
    @TableField(exist = false)
    private Product product;     // 关联的商品信息
} 