package com.example.common.model;

import com.example.common.constant.Constants;

import java.io.Serializable;

/**
 * 分页请求
 */
public class PageRequest implements Serializable {
    private static final long serialVersionUID = 1L;
    
    /**
     * 当前页码
     */
    private Integer pageNum;
    
    /**
     * 每页数量
     */
    private Integer pageSize;
    
    /**
     * 排序字段
     */
    private String orderBy;
    
    /**
     * 排序方式（asc/desc）
     */
    private String orderType;
    
    public PageRequest() {
        this.pageNum = Constants.DEFAULT_PAGE_NUM;
        this.pageSize = Constants.DEFAULT_PAGE_SIZE;
    }
    
    public Integer getPageNum() {
        return pageNum != null && pageNum > 0 ? pageNum : Constants.DEFAULT_PAGE_NUM;
    }
    
    public void setPageNum(Integer pageNum) {
        this.pageNum = pageNum;
    }
    
    public Integer getPageSize() {
        return pageSize != null && pageSize > 0 ? pageSize : Constants.DEFAULT_PAGE_SIZE;
    }
    
    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }
    
    public String getOrderBy() {
        return orderBy;
    }
    
    public void setOrderBy(String orderBy) {
        this.orderBy = orderBy;
    }
    
    public String getOrderType() {
        return orderType;
    }
    
    public void setOrderType(String orderType) {
        this.orderType = orderType;
    }
} 