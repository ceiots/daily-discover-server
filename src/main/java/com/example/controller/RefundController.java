package com.example.controller;

import com.example.common.api.CommonResult;
import com.example.model.RefundRequest;
import com.example.service.RefundService;
import com.example.util.UserIdExtractor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/refund")
public class RefundController {

    @Autowired
    private RefundService refundService;
    
    @Autowired
    private UserIdExtractor userIdExtractor;
    
    /**
     * 创建退款申请
     */
    @PostMapping("/create")
    public CommonResult<RefundRequest> createRefundRequest(
            @RequestHeader(value = "Authorization", required = false) String token,
            @RequestHeader(value = "userId", required = false) String userIdHeader,
            @RequestBody RefundRequest refundRequest) {
        try {
            Long userId = userIdExtractor.extractUserId(token, userIdHeader);
            if (userId == null) {
                return CommonResult.unauthorized(null);
            }
            
            // 设置申请人ID
            refundRequest.setUserId(userId);
            
            RefundRequest createdRequest = refundService.createRefundRequest(refundRequest);
            return CommonResult.success(createdRequest);
        } catch (Exception e) {
            return CommonResult.failed("创建退款申请失败：" + e.getMessage());
        }
    }
    
    /**
     * 获取退款申请详情
     */
    @GetMapping("/{id}")
    public CommonResult<RefundRequest> getRefundRequest(
            @PathVariable Long id,
            @RequestHeader(value = "Authorization", required = false) String token,
            @RequestHeader(value = "userId", required = false) String userIdHeader) {
        try {
            Long userId = userIdExtractor.extractUserId(token, userIdHeader);
            if (userId == null) {
                return CommonResult.unauthorized(null);
            }
            
            RefundRequest refundRequest = refundService.getRefundRequestById(id);
            if (refundRequest == null) {
                return CommonResult.failed("退款申请不存在");
            }
            
            // 检查权限（用户只能查看自己的申请）
            if (!refundRequest.getUserId().equals(userId)) {
                return CommonResult.forbidden(null);
            }
            
            return CommonResult.success(refundRequest);
        } catch (Exception e) {
            return CommonResult.failed("获取退款申请失败：" + e.getMessage());
        }
    }
    
    /**
     * 获取用户的所有退款申请
     */
    @GetMapping("/user")
    public CommonResult<List<RefundRequest>> getUserRefundRequests(
            @RequestHeader(value = "Authorization", required = false) String token,
            @RequestHeader(value = "userId", required = false) String userIdHeader) {
        try {
            Long userId = userIdExtractor.extractUserId(token, userIdHeader);
            if (userId == null) {
                return CommonResult.unauthorized(null);
            }
            
            List<RefundRequest> requests = refundService.getRefundRequestsByUserId(userId);
            return CommonResult.success(requests);
        } catch (Exception e) {
            return CommonResult.failed("获取退款申请列表失败：" + e.getMessage());
        }
    }
    
    /**
     * 获取订单的所有退款申请
     */
    @GetMapping("/order/{orderId}")
    public CommonResult<List<RefundRequest>> getOrderRefundRequests(
            @PathVariable Long orderId,
            @RequestHeader(value = "Authorization", required = false) String token,
            @RequestHeader(value = "userId", required = false) String userIdHeader) {
        try {
            Long userId = userIdExtractor.extractUserId(token, userIdHeader);
            if (userId == null) {
                return CommonResult.unauthorized(null);
            }
            
            List<RefundRequest> requests = refundService.getRefundRequestsByOrderId(orderId);
            return CommonResult.success(requests);
        } catch (Exception e) {
            return CommonResult.failed("获取订单退款申请列表失败：" + e.getMessage());
        }
    }
    
    /**
     * 取消退款申请
     */
    @PostMapping("/{id}/cancel")
    public CommonResult<RefundRequest> cancelRefundRequest(
            @PathVariable Long id,
            @RequestHeader(value = "Authorization", required = false) String token,
            @RequestHeader(value = "userId", required = false) String userIdHeader) {
        try {
            Long userId = userIdExtractor.extractUserId(token, userIdHeader);
            if (userId == null) {
                return CommonResult.unauthorized(null);
            }
            
            RefundRequest refundRequest = refundService.getRefundRequestById(id);
            if (refundRequest == null) {
                return CommonResult.failed("退款申请不存在");
            }
            
            // 检查权限（用户只能取消自己的申请）
            if (!refundRequest.getUserId().equals(userId)) {
                return CommonResult.forbidden(null);
            }
            
            // 检查状态（只有待处理的申请才能取消）
            if (refundRequest.getStatus() != 0) {
                return CommonResult.failed("只有待处理的退款申请才能取消");
            }
            
            RefundRequest cancelledRequest = refundService.cancelRefundRequest(id);
            return CommonResult.success(cancelledRequest);
        } catch (Exception e) {
            return CommonResult.failed("取消退款申请失败：" + e.getMessage());
        }
    }
    
    /**
     * 商家处理退款申请（同意/拒绝）
     */
    @PostMapping("/{id}/process")
    public CommonResult<RefundRequest> processRefundRequest(
            @PathVariable Long id,
            @RequestParam Integer status,
            @RequestParam(required = false) String refusalReason,
            @RequestHeader(value = "Authorization", required = false) String token,
            @RequestHeader(value = "userId", required = false) String userIdHeader) {
        try {
            Long userId = userIdExtractor.extractUserId(token, userIdHeader);
            if (userId == null) {
                return CommonResult.unauthorized(null);
            }
            
            RefundRequest refundRequest = refundService.getRefundRequestById(id);
            if (refundRequest == null) {
                return CommonResult.failed("退款申请不存在");
            }
            
            // TODO: 这里应该检查用户是否为店铺所有者
            
            // 如果是拒绝退款，必须提供拒绝原因
            if (status == 2 && (refusalReason == null || refusalReason.trim().isEmpty())) {
                return CommonResult.validateFailed("拒绝退款时必须提供拒绝原因");
            }
            
            RefundRequest processedRequest = refundService.processRefundRequest(id, status, refusalReason);
            return CommonResult.success(processedRequest);
        } catch (Exception e) {
            return CommonResult.failed("处理退款申请失败：" + e.getMessage());
        }
    }
} 