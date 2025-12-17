package com.dailydiscover.common.model;

import lombok.Data;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

/**
 * 分页结果
 *
 * @param <T> 数据类型
 */
@Data
public class PageResult<T> implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 当前页码
     */
    private int pageNum;

    /**
     * 每页记录数
     */
    private int pageSize;

    /**
     * 总记录数
     */
    private long total;

    /**
     * 总页数
     */
    private int pages;

    /**
     * 数据列表
     */
    private List<T> list;

    /**
     * 构造函数
     */
    public PageResult() {
        this.pageNum = 1;
        this.pageSize = 10;
        this.total = 0;
        this.pages = 0;
        this.list = Collections.emptyList();
    }

    /**
     * 构造函数
     *
     * @param pageNum  当前页码
     * @param pageSize 每页记录数
     * @param total    总记录数
     * @param list     数据列表
     */
    public PageResult(int pageNum, int pageSize, long total, List<T> list) {
        this.pageNum = pageNum;
        this.pageSize = pageSize;
        this.total = total;
        this.list = list;
        this.pages = (int) Math.ceil((double) total / pageSize);
    }

    /**
     * 构造函数
     *
     * @param pageRequest 分页请求参数
     * @param total       总记录数
     * @param list        数据列表
     */
    public PageResult(PageRequest pageRequest, long total, List<T> list) {
        this(pageRequest.getPageNum(), pageRequest.getPageSize(), total, list);
    }

    /**
     * 空分页结果
     *
     * @param <T> 数据类型
     * @return 空分页结果
     */
    public static <T> PageResult<T> empty() {
        return new PageResult<>();
    }

    /**
     * 空分页结果
     *
     * @param pageRequest 分页请求参数
     * @param <T>         数据类型
     * @return 空分页结果
     */
    public static <T> PageResult<T> empty(PageRequest pageRequest) {
        return new PageResult<>(pageRequest.getPageNum(), pageRequest.getPageSize(), 0, Collections.emptyList());
    }
}