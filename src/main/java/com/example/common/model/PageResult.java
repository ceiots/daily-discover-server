package com.example.common.model;

import java.io.Serializable;
import java.util.List;

/**
 * 分页结果
 */
public class PageResult<T> implements Serializable {
    private static final long serialVersionUID = 1L;
    
    /**
     * 总记录数
     */
    private Long total;
    
    /**
     * 每页数据
     */
    private List<T> list;
    
    /**
     * 当前页码
     */
    private Integer pageNum;
    
    /**
     * 每页数量
     */
    private Integer pageSize;
    
    public PageResult() {
    }
    
    public PageResult(Long total, List<T> list) {
        this.total = total;
        this.list = list;
    }
    
    public PageResult(Long total, List<T> list, Integer pageNum, Integer pageSize) {
        this.total = total;
        this.list = list;
        this.pageNum = pageNum;
        this.pageSize = pageSize;
    }
    
    public Long getTotal() {
        return total;
    }
    
    public void setTotal(Long total) {
        this.total = total;
    }
    
    public List<T> getList() {
        return list;
    }
    
    public void setList(List<T> list) {
        this.list = list;
    }
    
    public Integer getPageNum() {
        return pageNum;
    }
    
    public void setPageNum(Integer pageNum) {
        this.pageNum = pageNum;
    }
    
    public Integer getPageSize() {
        return pageSize;
    }
    
    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }
} 