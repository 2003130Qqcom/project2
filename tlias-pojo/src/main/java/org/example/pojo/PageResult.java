package org.example.pojo;

import lombok.Data;

import java.util.List;

/**
 * 分页响应体，配合 PageHelper 使用。
 *
 * @param <T> 数据行类型
 */
@Data
public class PageResult<T> {

    /** 总记录数 */
    private Long total;
    /** 当前页数据行 */
    private List<T> rows;

    /**
     * @param total 总记录数
     * @param rows  当前页数据行
     */
    public PageResult(Long total, List<T> rows) {
        this.total = total;
        this.rows = rows;
    }
}
