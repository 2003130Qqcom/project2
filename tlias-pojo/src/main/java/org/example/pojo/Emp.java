package org.example.pojo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 员工实体，映射 emp 表。
 *
 * <p>密码字段 {@code @JsonProperty(access = WRITE_ONLY)} 仅接收不入库返回，
 * 与工作经历(emp_expr)为一对多关系，通过 {@code exprList} JSON 字段承载。</p>
 */
@Data
public class Emp {
    private Integer id;                   // 主键
    private String username;              // 用户名（登录用）
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;              // 密码（仅接收，序列化时隐藏）
    private String name;                  // 姓名
    private Integer gender;               // 性别: 1=男, 2=女
    private String phone;                 // 手机号
    private Integer job;                  // 职位: 1=班主任, 2=讲师, 3=学工主管, 4=教研主管, 5=咨询师
    private Integer salary;               // 薪资
    private String image;                 // 头像URL
    private LocalDate entryDate;          // 入职日期
    private Integer deptId;               // 部门ID
    private LocalDateTime createTime;     // 创建时间
    private LocalDateTime updateTime;     // 修改时间
    private String deptName;              // 部门名称（关联查询填充）
    /** 工作经历列表，前端 JSON 字段名为 exprList */
    @JsonProperty("exprList")
    private List<EmpExpr> empExprList;
}
