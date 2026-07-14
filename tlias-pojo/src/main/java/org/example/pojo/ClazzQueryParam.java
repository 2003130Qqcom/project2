package org.example.pojo;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

/**
 * 班级分页查询参数，由 Controller 从请求 Query String 自动绑定。
 *
 * <pre>
 * GET /clazzs?page=1&pageSize=10&name=Java&begin=2026-01-01&end=2026-06-30
 * </pre>
 */
@Data
public class ClazzQueryParam {
    /** 页码 */
    private Integer page;
    /** 每页条数 */
    private Integer pageSize;
    /** 班级名称模糊匹配 */
    private String name;
    /** 开课日期起始 */
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate begin;
    /** 结课日期截止 */
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate end;
}
