package org.example.pojo;

import lombok.Data;

import java.time.LocalDate;

/**
 * 员工工作经历，映射 emp_expr 表，与 Emp 为一对多关系。
 */
@Data
public class EmpExpr {
    private Integer id;       // 主键
    private Integer empId;    // 员工ID（关联 emp.id）
    private LocalDate begin;  // 开始日期
    private LocalDate end;    // 结束日期
    private String company;   // 公司名称
    private String job;       // 职位名称
}
