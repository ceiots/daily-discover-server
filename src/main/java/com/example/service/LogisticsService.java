package com.example.service;

import com.example.mapper.LogisticsInfoMapper;
import com.example.mapper.LogisticsTrackMapper;
import com.example.mapper.LogisticsCompanyMapper;
import com.example.model.LogisticsInfo;
import com.example.model.LogisticsTrack;
import com.example.model.LogisticsCompany;
import com.example.model.Order;
import com.example.model.OrderAddr;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;

/**
 * 物流服务
 */
@Slf4j
@Service
public class LogisticsService {

    @Autowired
    private LogisticsInfoMapper logisticsInfoMapper;
    
    @Autowired
    private LogisticsTrackMapper logisticsTrackMapper;
    
    @Autowired
    private LogisticsCompanyMapper logisticsCompanyMapper;
    
    @Autowired
    private OrderService orderService;
    
    @Autowired
    private OrderAddrService orderAddrService;
    
    @Autowired
    private ThirdPartyLogisticsService thirdPartyLogisticsService;

    /**
     * 根据ID查询物流信息
     */
    public LogisticsInfo getLogisticsInfoById(Long id) {
        LogisticsInfo logisticsInfo = logisticsInfoMapper.findById(id);
        if (logisticsInfo != null) {
            // 查询物流轨迹
            List<LogisticsTrack> tracks = logisticsTrackMapper.findByLogisticsId(id);
            logisticsInfo.setTracks(tracks);
            // 设置状态文本
            setStatusText(logisticsInfo);
        }
        return logisticsInfo;
    }

    /**
     * 根据订单ID查询物流信息
     */
    public LogisticsInfo getLogisticsInfoByOrderId(Long orderId) {
        LogisticsInfo logisticsInfo = logisticsInfoMapper.findByOrderId(orderId);
        if (logisticsInfo != null) {
            // 查询物流轨迹
            List<LogisticsTrack> tracks = logisticsTrackMapper.findByLogisticsId(logisticsInfo.getId());
            logisticsInfo.setTracks(tracks);
            // 设置状态文本
            setStatusText(logisticsInfo);
        }
        return logisticsInfo;
    }

    /**
     * 根据物流单号查询物流信息
     */
    public LogisticsInfo getLogisticsInfoByTrackingNumber(String trackingNumber) {
        LogisticsInfo logisticsInfo = logisticsInfoMapper.findByTrackingNumber(trackingNumber);
        if (logisticsInfo != null) {
            // 查询物流轨迹
            List<LogisticsTrack> tracks = logisticsTrackMapper.findByLogisticsId(logisticsInfo.getId());
            logisticsInfo.setTracks(tracks);
            // 设置状态文本
            setStatusText(logisticsInfo);
        }
        return logisticsInfo;
    }
    
    /**
     * 根据订单ID获取实时物流信息
     * 先从数据库获取基本信息，再从第三方API获取最新轨迹
     */
    public Map<String, Object> getRealTimeLogisticsInfoByOrderId(Long orderId) {
        Map<String, Object> result = new HashMap<>();
        
        // 从数据库获取基本物流信息
        LogisticsInfo logisticsInfo = logisticsInfoMapper.findByOrderId(orderId);
        if (logisticsInfo == null) {
            result.put("success", false);
            result.put("message", "未找到物流信息");
            return result;
        }
        
        // 查询物流轨迹（获取本地存储的轨迹）
        List<LogisticsTrack> localTracks = logisticsTrackMapper.findByLogisticsId(logisticsInfo.getId());
        
        // 从第三方API获取最新轨迹
        List<LogisticsTrack> apiTracks = thirdPartyLogisticsService.queryLogisticsTrack(
                logisticsInfo.getCompanyCode(), 
                logisticsInfo.getTrackingNumber()
        );
        
        // 如果API返回了轨迹，则更新本地数据库并使用API轨迹
        if (apiTracks != null && !apiTracks.isEmpty()) {
            // 保存API返回的轨迹到数据库
            updateLogisticsTracksFromThirdParty(logisticsInfo.getId(), apiTracks);
            logisticsInfo.setTracks(apiTracks);
            
            // 根据最新的API轨迹更新物流状态
            updateLogisticsStatusFromTracks(logisticsInfo, apiTracks);
        } else {
            // 如果API没有返回轨迹，则使用本地轨迹
            logisticsInfo.setTracks(localTracks);
        }
        
        // 设置状态文本
        setStatusText(logisticsInfo);
        
        // 格式化物流信息，生成前端需要的数据结构
        result.put("success", true);
        result.put("data", formatLogisticsInfoForFrontend(logisticsInfo));
        
        return result;
    }
    
    /**
     * 更新第三方API返回的物流轨迹
     */
    @Transactional
    public void updateLogisticsTracksFromThirdParty(Long logisticsId, List<LogisticsTrack> apiTracks) {
        if (apiTracks == null || apiTracks.isEmpty()) {
            return;
        }
        
        try {
            // 先删除旧的轨迹数据
            logisticsTrackMapper.deleteByLogisticsId(logisticsId);
            
            // 保存新的轨迹数据
            for (LogisticsTrack track : apiTracks) {
                track.setLogisticsId(logisticsId);
                logisticsTrackMapper.insert(track);
            }
        } catch (Exception e) {
            log.error("更新物流轨迹异常", e);
            throw e;
        }
    }
    
    /**
     * 根据轨迹更新物流状态
     */
    private void updateLogisticsStatusFromTracks(LogisticsInfo logisticsInfo, List<LogisticsTrack> tracks) {
        if (tracks == null || tracks.isEmpty()) {
            return;
        }
        
        // 获取最新的轨迹状态
        LogisticsTrack latestTrack = tracks.get(0);
        String statusCode = latestTrack.getStatusCode();
        
        // 根据状态码更新物流状态
        Integer newStatus = mapTrackStatusToLogisticsStatus(statusCode);
        if (newStatus != null && !newStatus.equals(logisticsInfo.getStatus())) {
            // 更新物流状态
            logisticsInfoMapper.updateStatus(logisticsInfo.getId(), newStatus);
            logisticsInfo.setStatus(newStatus);
            
            // 更新发货、送达时间
            if (newStatus == 1 && logisticsInfo.getShippingTime() == null) {
                // 已发货状态，更新发货时间
                logisticsInfo.setShippingTime(latestTrack.getTrackTime());
                logisticsInfoMapper.updateShippingTime(logisticsInfo.getId(), latestTrack.getTrackTime());
            } else if (newStatus == 3 && logisticsInfo.getActualDeliveryTime() == null) {
                // 已签收状态，更新送达时间
                logisticsInfo.setActualDeliveryTime(latestTrack.getTrackTime());
                logisticsInfoMapper.updateActualDeliveryTime(logisticsInfo.getId(), latestTrack.getTrackTime());
            }
        }
    }
    
    /**
     * 将轨迹状态码映射为物流状态
     */
    private Integer mapTrackStatusToLogisticsStatus(String statusCode) {
        switch (statusCode) {
            case "COLLECTED":
                return 1; // 已发货
            case "IN_TRANSIT":
                return 2; // 运输中
            case "DELIVERING":
                return 2; // 运输中
            case "DELIVERED":
                return 3; // 已签收
            case "EXCEPTION":
                return 4; // 异常
            default:
                return null;
        }
    }
    
    /**
     * 格式化物流信息用于前端显示
     */
    private Map<String, Object> formatLogisticsInfoForFrontend(LogisticsInfo logisticsInfo) {
        Map<String, Object> result = new HashMap<>();
        
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        
        // 基本信息
        result.put("id", logisticsInfo.getId());
        result.put("orderId", logisticsInfo.getOrderId());
        result.put("company", logisticsInfo.getCompanyName());
        result.put("number", logisticsInfo.getTrackingNumber());
        result.put("status", logisticsInfo.getStatus());
        result.put("statusText", logisticsInfo.getStatusText());
        
        // 如果有轨迹，获取最新的轨迹信息作为概要
        if (logisticsInfo.getTracks() != null && !logisticsInfo.getTracks().isEmpty()) {
            LogisticsTrack latestTrack = logisticsInfo.getTracks().get(0);
            result.put("detail", latestTrack.getDescription());
            result.put("lastUpdate", dateFormat.format(latestTrack.getTrackTime()));
        }
        
        // 格式化物流轨迹
        List<Map<String, Object>> timeline = new ArrayList<>();
        if (logisticsInfo.getTracks() != null) {
            for (LogisticsTrack track : logisticsInfo.getTracks()) {
                Map<String, Object> item = new HashMap<>();
                item.put("time", dateFormat.format(track.getTrackTime()));
                item.put("status", track.getStatus());
                item.put("location", track.getLocation());
                item.put("description", track.getDescription());
                timeline.add(item);
            }
        }
        result.put("timeline", timeline);
        
        return result;
    }

    /**
     * 查询所有物流公司
     */
    public List<LogisticsCompany> getAllLogisticsCompanies() {
        return logisticsCompanyMapper.findAll();
    }

    /**
     * 查询所有启用的物流公司
     */
    public List<LogisticsCompany> getAllEnabledLogisticsCompanies() {
        return logisticsCompanyMapper.findAllEnabled();
    }

    /**
     * 创建物流信息
     */
    @Transactional
    public LogisticsInfo createLogisticsInfo(Long orderId, String companyCode) {
        // 查询订单信息
        Order order = orderService.getOrderByNumber(String.valueOf(orderId));
        if (order == null) {
            log.error("订单不存在，orderId: {}", orderId);
            return null;
        }
        
        // 查询收货地址
        OrderAddr orderAddr = orderAddrService.getByOrderAddrId(order.getOrderAddrId());
        if (orderAddr == null) {
            log.error("收货地址不存在，orderAddrId: {}", order.getOrderAddrId());
            return null;
        }
        
        // 查询物流公司
        LogisticsCompany company = logisticsCompanyMapper.findByCode(companyCode);
        if (company == null) {
            log.error("物流公司不存在，companyCode: {}", companyCode);
            return null;
        }
        
        // 生成物流单号
        String trackingNumber = generateTrackingNumber(companyCode);
        
        // 创建物流信息
        LogisticsInfo logisticsInfo = new LogisticsInfo();
        logisticsInfo.setOrderId(orderId);
        logisticsInfo.setTrackingNumber(trackingNumber);
        logisticsInfo.setCompanyCode(companyCode);
        logisticsInfo.setCompanyName(company.getName());
        logisticsInfo.setStatus(0); // 待发货
        logisticsInfo.setReceiverName(orderAddr.getName());
        logisticsInfo.setReceiverPhone(orderAddr.getPhone());
        logisticsInfo.setReceiverAddress(orderAddr.getProvince() + orderAddr.getCity() + orderAddr.getDistrict() + orderAddr.getAddress());
        
        // 保存物流信息
        logisticsInfoMapper.insert(logisticsInfo);
        
        // 创建初始物流轨迹
        LogisticsTrack track = new LogisticsTrack();
        track.setLogisticsId(logisticsInfo.getId());
        track.setTrackTime(new Date());
        track.setLocation("系统");
        track.setDescription("物流信息已创建");
        track.setStatus("待发货");
        track.setStatusCode("PENDING");
        
        logisticsTrackMapper.insert(track);
        
        // 设置状态文本
        setStatusText(logisticsInfo);
        
        return logisticsInfo;
    }

    /**
     * 更新物流状态
     */
    @Transactional
    public boolean updateLogisticsStatus(Long logisticsId, Integer status, String location, String description) {
        // 查询物流信息
        LogisticsInfo logisticsInfo = logisticsInfoMapper.findById(logisticsId);
        if (logisticsInfo == null) {
            log.error("物流信息不存在，logisticsId: {}", logisticsId);
            return false;
        }
        
        // 更新物流状态
        logisticsInfoMapper.updateStatus(logisticsId, status);
        
        // 创建物流轨迹
        LogisticsTrack track = new LogisticsTrack();
        track.setLogisticsId(logisticsId);
        track.setTrackTime(new Date());
        track.setLocation(location);
        track.setDescription(description);
        
        // 设置状态和状态码
        switch (status) {
            case 0:
                track.setStatus("待发货");
                track.setStatusCode("PENDING");
                break;
            case 1:
                track.setStatus("已发货");
                track.setStatusCode("SHIPPED");
                logisticsInfo.setShippingTime(new Date());
                break;
            case 2:
                track.setStatus("运输中");
                track.setStatusCode("IN_TRANSIT");
                break;
            case 3:
                track.setStatus("已签收");
                track.setStatusCode("DELIVERED");
                logisticsInfo.setActualDeliveryTime(new Date());
                break;
            case 4:
                track.setStatus("异常");
                track.setStatusCode("EXCEPTION");
                break;
            default:
                track.setStatus("未知状态");
                track.setStatusCode("UNKNOWN");
                break;
        }
        
        logisticsTrackMapper.insert(track);
        
        return true;
    }

    /**
     * 模拟物流轨迹更新
     */
    @Transactional
    public boolean simulateLogisticsUpdate(Long logisticsId) {
        // 查询物流信息
        LogisticsInfo logisticsInfo = logisticsInfoMapper.findById(logisticsId);
        if (logisticsInfo == null) {
            log.error("物流信息不存在，logisticsId: {}", logisticsId);
            return false;
        }
        
        // 根据当前状态模拟下一步状态
        int currentStatus = logisticsInfo.getStatus();
        int nextStatus;
        String location;
        String description;
        
        switch (currentStatus) {
            case 0: // 待发货 -> 已发货
                nextStatus = 1;
                location = "发货仓库";
                description = "包裹已发出";
                break;
            case 1: // 已发货 -> 运输中
                nextStatus = 2;
                location = "转运中心";
                description = "包裹已到达转运中心";
                break;
            case 2: // 运输中 -> 已签收
                nextStatus = 3;
                location = "收货地址";
                description = "包裹已签收";
                break;
            default:
                log.error("当前状态无法模拟更新，status: {}", currentStatus);
                return false;
        }
        
        // 更新物流状态
        return updateLogisticsStatus(logisticsId, nextStatus, location, description);
    }

    /**
     * 设置物流状态文本
     */
    private void setStatusText(LogisticsInfo logisticsInfo) {
        if (logisticsInfo == null) {
            return;
        }
        
        switch (logisticsInfo.getStatus()) {
            case 0:
                logisticsInfo.setStatusText("待发货");
                break;
            case 1:
                logisticsInfo.setStatusText("已发货");
                break;
            case 2:
                logisticsInfo.setStatusText("运输中");
                break;
            case 3:
                logisticsInfo.setStatusText("已签收");
                break;
            case 4:
                logisticsInfo.setStatusText("异常");
                break;
            default:
                logisticsInfo.setStatusText("未知状态");
                break;
        }
    }

    /**
     * 生成物流单号
     */
    private String generateTrackingNumber(String companyCode) {
        // 生成时间戳
        long timestamp = System.currentTimeMillis();
        // 生成随机数
        int random = (int) (Math.random() * 10000);
        // 组合物流单号：公司编码 + 时间戳 + 随机数
        return companyCode + timestamp + String.format("%04d", random);
    }
} 