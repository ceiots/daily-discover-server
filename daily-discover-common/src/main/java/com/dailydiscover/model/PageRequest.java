package com.dailydiscover.model;

import lombok.Data;

import jakarta.validation.constraints.Min;
import java.io.Serializable;

/**
 * 分页请求参数
 */
@Data
public class PageRequest implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 当前页码
     */
    @Min(value = 1, message = "页码不能小于1")
    private int pageNum = 1;

    /**
     * 每页记录数
     */
    @Min(value = 1, message = "每页记录数不能小于1")
    private int pageSize = 10;

    /**
     * 排序字段
     */
    private String orderBy;

    /**
     * 排序方式
     */
    private String orderType = "asc";
}