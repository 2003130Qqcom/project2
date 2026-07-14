package org.example.pojo;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

/**
 * 员工分页查询参数，由 Controller 从请求 Query String 自动绑定。
 *
 * <pre>
 * GET /emps?page=1&pageSize=10&name=张&gender=1&begin=2026-01-01&end=2026-06-30
 * </pre>
 */
@Data
public class EmpQueryParam {
    /** 页码（默认1），Service 层已做空值保护 */
    private Integer page;
    /** 每页条数（默认10），Service 层已做空值保护 */
    private Integer pageSize;
    /** 姓名模糊匹配 */
    private String name;
    /** 性别精确匹配: 1=男, 2=女 */
    private Integer gender;
    /** 入职日期起始 */
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate begin;
    /** 入职日期截止 */
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate end;
}
