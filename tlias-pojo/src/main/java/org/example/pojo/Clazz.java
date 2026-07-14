package org.example.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 班级实体，映射 clazz 表。
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Clazz {
    private Integer id;               // 主键
    private String name;              // 班级名称
    private String room;              // 教室
    private LocalDate beginDate;      // 开课日期
    private LocalDate endDate;        // 结课日期
    private Integer masterId;         // 班主任ID（关联 emp.id）
    private Integer subject;          // 学科
    private LocalDateTime createTime; // 创建时间
    private LocalDateTime updateTime; // 修改时间

    // ↓ 以下为关联查询字段，非数据库列
    private String masterName;        // 班主任姓名
    private String status;            // 班级状态: 未开班 / 在读 / 已结课
}