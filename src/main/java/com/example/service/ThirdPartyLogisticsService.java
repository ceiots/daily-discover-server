package com.example.service;

import com.example.model.LogisticsTrack;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 第三方物流查询服务
 * 支持实时查询物流信息
 */
@Slf4j
@Service
public class ThirdPartyLogisticsService {

    @Value("${logistics.kdniao.app-id:test1234567}")
    private String appId;

    @Value("${logistics.kdniao.app-key:12345678}")
    private String appKey;

    @Value("${logistics.kdniao.api-url:http://api.kdniao.com/Ebusiness/EbusinessOrderHandle.aspx}")
    private String apiUrl;

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * 查询物流轨迹
     * @param companyCode 物流公司编码
     * @param trackingNumber 物流单号
     * @return 物流轨迹列表
     */
    public List<LogisticsTrack> queryLogisticsTrack(String companyCode, String trackingNumber) {
        try {
            log.info("查询物流轨迹，物流公司: {}，物流单号: {}", companyCode, trackingNumber);
            
            // 这里是模拟查询，实际环境中应该调用真实的API
            // 如果是真实环境，需要调用下面的 doQueryFromApi 方法
            
            // 模拟查询结果
            return generateMockLogisticsTrack(companyCode, trackingNumber);
            
            // 真实API调用
            // return doQueryFromApi(companyCode, trackingNumber);
        } catch (Exception e) {
            log.error("查询物流轨迹异常", e);
            return Collections.emptyList();
        }
    }
    
    /**
     * 从第三方API查询物流轨迹
     * 这里以快递鸟API为例
     */
    private List<LogisticsTrack> doQueryFromApi(String companyCode, String trackingNumber) {
        try {
            // 构建请求参数
            Map<String, String> requestParams = new HashMap<>();
            requestParams.put("ShipperCode", translateCompanyCode(companyCode));
            requestParams.put("LogisticCode", trackingNumber);
            
            // 签名加密逻辑（根据快递鸟API文档实现）
            String dataSign = sign(objectMapper.writeValueAsString(requestParams), appKey);
            
            // 构建完整请求参数
            Map<String, String> params = new HashMap<>();
            params.put("RequestData", objectMapper.writeValueAsString(requestParams));
            params.put("EBusinessID", appId);
            params.put("RequestType", "1002"); // 轨迹查询接口
            params.put("DataSign", dataSign);
            params.put("DataType", "2"); // 返回JSON格式
            
            // 发送请求
            HttpHeaders headers = new HttpHeaders();
            headers.set("Content-Type", "application/x-www-form-urlencoded;charset=utf-8");
            
            HttpEntity<Map<String, String>> requestEntity = new HttpEntity<>(params, headers);
            ResponseEntity<String> response = restTemplate.exchange(
                    apiUrl,
                    HttpMethod.POST,
                    requestEntity,
                    String.class
            );
            
            // 解析响应
            if (response.getStatusCode().is2xxSuccessful()) {
                return parseApiResponse(response.getBody());
            } else {
                log.error("API请求失败，状态码: {}", response.getStatusCode());
                return Collections.emptyList();
            }
            
        } catch (Exception e) {
            log.error("调用物流API异常", e);
            return Collections.emptyList();
        }
    }
    
    /**
     * 解析API响应
     */
    private List<LogisticsTrack> parseApiResponse(String responseBody) {
        try {
            List<LogisticsTrack> trackList = new ArrayList<>();
            JsonNode rootNode = objectMapper.readTree(responseBody);
            
            if (rootNode.has("Success") && rootNode.get("Success").asBoolean()) {
                JsonNode traces = rootNode.get("Traces");
                if (traces != null && traces.isArray()) {
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    
                    for (JsonNode trace : traces) {
                        LogisticsTrack track = new LogisticsTrack();
                        track.setTrackTime(dateFormat.parse(trace.get("AcceptTime").asText()));
                        track.setLocation(trace.get("AcceptStation").asText());
                        track.setDescription(trace.get("Remark").asText(""));
                        track.setStatus(mapStatusFromApi(trace.get("Action").asText("")));
                        track.setStatusCode(trace.get("Action").asText(""));
                        
                        trackList.add(track);
                    }
                }
            } else {
                log.error("API响应失败: {}", rootNode.get("Reason").asText("未知原因"));
            }
            
            return trackList;
        } catch (Exception e) {
            log.error("解析API响应异常", e);
            return Collections.emptyList();
        }
    }
    
    /**
     * 模拟生成物流轨迹
     */
    private List<LogisticsTrack> generateMockLogisticsTrack(String companyCode, String trackingNumber) {
        List<LogisticsTrack> trackList = new ArrayList<>();
        
        // 当前时间
        Calendar calendar = Calendar.getInstance();
        
        // 模拟物流轨迹
        // 1. 已揽收
        LogisticsTrack track1 = new LogisticsTrack();
        track1.setTrackTime(calendar.getTime());
        track1.setLocation("北京市海淀区中关村");
        track1.setDescription("快件已揽收");
        track1.setStatus("已揽收");
        track1.setStatusCode("COLLECTED");
        trackList.add(track1);
        
        // 2. 运输中 - 1
        calendar.add(Calendar.HOUR, -2);
        LogisticsTrack track2 = new LogisticsTrack();
        track2.setTrackTime(calendar.getTime());
        track2.setLocation("北京转运中心");
        track2.setDescription("快件已到达");
        track2.setStatus("运输中");
        track2.setStatusCode("IN_TRANSIT");
        trackList.add(track2);
        
        // 3. 运输中 - 2
        calendar.add(Calendar.HOUR, -6);
        LogisticsTrack track3 = new LogisticsTrack();
        track3.setTrackTime(calendar.getTime());
        track3.setLocation("北京转运中心");
        track3.setDescription("快件已发出，下一站：上海转运中心");
        track3.setStatus("运输中");
        track3.setStatusCode("IN_TRANSIT");
        trackList.add(track3);
        
        // 4. 运输中 - 3
        calendar.add(Calendar.HOUR, -12);
        LogisticsTrack track4 = new LogisticsTrack();
        track4.setTrackTime(calendar.getTime());
        track4.setLocation("上海转运中心");
        track4.setDescription("快件已到达");
        track4.setStatus("运输中");
        track4.setStatusCode("IN_TRANSIT");
        trackList.add(track4);
        
        // 5. 派送中
        calendar.add(Calendar.HOUR, -2);
        LogisticsTrack track5 = new LogisticsTrack();
        track5.setTrackTime(calendar.getTime());
        track5.setLocation("上海市浦东新区");
        track5.setDescription("快件已发出，正在派送途中");
        track5.setStatus("派送中");
        track5.setStatusCode("DELIVERING");
        trackList.add(track5);
        
        return trackList;
    }
    
    /**
     * 映射状态
     */
    private String mapStatusFromApi(String actionCode) {
        switch (actionCode) {
            case "1":
                return "已揽收";
            case "2":
                return "运输中";
            case "3":
                return "派送中";
            case "4":
                return "已签收";
            case "5":
                return "问题件";
            default:
                return "未知状态";
        }
    }
    
    /**
     * 转换物流公司编码
     */
    private String translateCompanyCode(String companyCode) {
        // 根据系统内部编码转换为快递鸟API使用的编码
        Map<String, String> codeMap = new HashMap<>();
        codeMap.put("SF", "SF");
        codeMap.put("YTO", "YTO");
        codeMap.put("ZTO", "ZTO");
        codeMap.put("STO", "STO");
        codeMap.put("YD", "YD");
        codeMap.put("EMS", "EMS");
        codeMap.put("HTKY", "HTKY");
        codeMap.put("JD", "JD");
        codeMap.put("QFKD", "QFKD");
        codeMap.put("GTO", "GTO");
        
        return codeMap.getOrDefault(companyCode, companyCode);
    }
    
    /**
     * 签名
     */
    private String sign(String data, String appKey) {
        try {
            // 快递鸟签名算法
            String signData = Base64.getEncoder().encodeToString((data + appKey).getBytes("UTF-8"));
            java.security.MessageDigest md = java.security.MessageDigest.getInstance("MD5");
            byte[] digest = md.digest(signData.getBytes("UTF-8"));
            
            // 将MD5转为16进制字符串
            StringBuilder sb = new StringBuilder();
            for (byte b : digest) {
                sb.append(String.format("%02x", b & 0xff));
            }
            
            return Base64.getEncoder().encodeToString(sb.toString().getBytes("UTF-8"));
        } catch (Exception e) {
            log.error("签名异常", e);
            return "";
        }
    }
} 