package com.example.service.impl;

import com.example.mapper.RefundRequestMapper;
import com.example.mapper.OrderMapper;
import com.example.model.RefundRequest;
import com.example.model.Order;
import com.example.service.RefundService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Date;
import java.util.List;

@Service
public class RefundServiceImpl implements RefundService {

    @Autowired
    private RefundRequestMapper refundRequestMapper;
    
    @Autowired
    private OrderMapper orderMapper;

    @Override
    @Transactional
    public RefundRequest createRefundRequest(RefundRequest refundRequest) {
        // 设置初始状态和时间
        refundRequest.setStatus(0); // 待处理
        Date now = new Date();
        refundRequest.setCreatedAt(now);
        refundRequest.setUpdatedAt(now);
        
        // 如果提供了图片列表，则转换为逗号分隔的字符串
        if (refundRequest.getImageList() != null && !refundRequest.getImageList().isEmpty()) {
            refundRequest.setImages(String.join(",", refundRequest.getImageList()));
        }
        
        // 插入数据库
        refundRequestMapper.insert(refundRequest);
        
        // 更新订单状态为申请退款中
        Order order = orderMapper.findById(refundRequest.getOrderId());
        if (order != null) {
            Order updateOrder = new Order();
            updateOrder.setId(order.getId());
            updateOrder.setStatus(6); // 6表示申请退款中
            orderMapper.updateOrder(updateOrder);
        }
        
        return refundRequest;
    }

    @Override
    public RefundRequest getRefundRequestById(Long id) {
        return refundRequestMapper.findById(id);
    }

    @Override
    public List<RefundRequest> getRefundRequestsByUserId(Long userId) {
        return refundRequestMapper.findByUserId(userId);
    }

    @Override
    public List<RefundRequest> getRefundRequestsByOrderId(Long orderId) {
        return refundRequestMapper.findByOrderId(orderId);
    }

    @Override
    @Transactional
    public RefundRequest processRefundRequest(Long id, Integer status, String refusalReason) {
        RefundRequest refundRequest = refundRequestMapper.findById(id);
        if (refundRequest == null) {
            return null;
        }
        
        Date now = new Date();
        
        if (status == 2) { // 拒绝退款
            refundRequestMapper.rejectRefund(id, status, refusalReason, now);
            
            // 恢复订单状态为已支付/已发货
            Order order = orderMapper.findById(refundRequest.getOrderId());
            if (order != null) {
                // 由于Order没有previousStatus字段，默认恢复到已支付状态(2)或保持当前状态
                Order updateOrder = new Order();
                updateOrder.setId(order.getId());
                updateOrder.setStatus(2); // 2表示已支付/待发货状态
                orderMapper.updateOrder(updateOrder);
            }
        } else { // 同意退款
            refundRequestMapper.updateStatus(id, status, now);
            
            // 如果是同意退款且状态为完成，则将订单状态更新为已退款
            if (status == 3) { // 已完成
                Order order = orderMapper.findById(refundRequest.getOrderId());
                if (order != null) {
                    Order updateOrder = new Order();
                    updateOrder.setId(order.getId());
                    updateOrder.setStatus(7); // 7表示已退款
                    orderMapper.updateOrder(updateOrder);
                }
            }
        }
        
        // 重新查询更新后的退款申请
        return refundRequestMapper.findById(id);
    }

    @Override
    @Transactional
    public RefundRequest cancelRefundRequest(Long id) {
        RefundRequest refundRequest = refundRequestMapper.findById(id);
        if (refundRequest == null) {
            return null;
        }
        
        Date now = new Date();
        refundRequestMapper.updateStatus(id, 4, now); // 4-已取消
        
        // 恢复订单状态
        Order order = orderMapper.findById(refundRequest.getOrderId());
        if (order != null) {
            // 由于Order没有previousStatus字段，默认恢复到已支付状态(2)
            Order updateOrder = new Order();
            updateOrder.setId(order.getId());
            updateOrder.setStatus(2); // 2表示已支付/待发货状态
            orderMapper.updateOrder(updateOrder);
        }
        
        return refundRequestMapper.findById(id);
    }
} 