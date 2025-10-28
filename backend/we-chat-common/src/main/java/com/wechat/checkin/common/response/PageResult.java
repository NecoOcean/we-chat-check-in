package com.wechat.checkin.common.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * 分页响应结果封装类
 *
 * @param <T> 数据类型
 * @author WeChat Check-in System
 * @since 1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PageResult<T> extends Result<List<T>> {

    private static final long serialVersionUID = 1L;

    /**
     * 当前页码
     */
    private Long current;

    /**
     * 每页大小
     */
    private Long size;

    /**
     * 总记录数
     */
    private Long total;

    /**
     * 总页数
     */
    private Long pages;

    /**
     * 是否有上一页
     */
    private Boolean hasPrevious;

    /**
     * 是否有下一页
     */
    private Boolean hasNext;

    public PageResult() {
        super();
    }

    public PageResult(Long current, Long size, Long total, List<T> records) {
        super(ResultCode.SUCCESS.getCode(), ResultCode.SUCCESS.getMessage(), records);
        this.current = current;
        this.size = size;
        this.total = total;
        this.pages = (total + size - 1) / size; // 计算总页数
        this.hasPrevious = current > 1;
        this.hasNext = current < pages;
    }

    /**
     * 成功分页响应
     */
    public static <T> PageResult<T> success(Long current, Long size, Long total, List<T> records) {
        return new PageResult<>(current, size, total, records);
    }

    /**
     * 空分页响应
     */
    public static <T> PageResult<T> empty(Long current, Long size) {
        return new PageResult<>(current, size, 0L, List.of());
    }

    /**
     * 失败分页响应
     */
    public static <T> PageResult<T> errorPage(String message) {
        PageResult<T> result = new PageResult<>();
        result.setCode(ResultCode.INTERNAL_SERVER_ERROR.getCode());
        result.setMessage(message);
        return result;
    }

    /**
     * 失败分页响应（使用ResultCode枚举）
     */
    public static <T> PageResult<T> errorPage(ResultCode resultCode) {
        PageResult<T> result = new PageResult<>();
        result.setCode(resultCode.getCode());
        result.setMessage(resultCode.getMessage());
        return result;
    }
}