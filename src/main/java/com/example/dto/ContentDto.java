package com.example.dto;

import lombok.Data;
import java.util.List;

/**
 * 内容传输对象，用于接收前端传来的内容创建/更新请求
 */
@Data
public class ContentDto {
    private Long id;
    private String title;
    private String content;
    private List<String> images; // 图片URL列表
    private List<String> tags; // 标签列表
    private Integer status; // 状态：0草稿，1已发布
    private Integer auditStatus; // 0-待审核，1-审核通过，2-审核不通过
    private String auditRemark; // 审核备注
} 