package com.example.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 分类查询请求
 */
@Data
public class CategoryQueryRequest implements Serializable {

    /**
     * 分类名称
     */
    private String name;

    /**
     * 父分类ID
     */
    private Long parentId;

    /**
     * 分类层级，1为顶级
     */
    private Integer level;

    /**
     * 状态：1-启用，0-禁用
     */
    private Integer status;

    private static final long serialVersionUID = 1L;
} 