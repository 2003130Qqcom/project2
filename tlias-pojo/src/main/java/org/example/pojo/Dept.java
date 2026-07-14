package org.example.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 部门实体，映射 dept 表。
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Dept {
    private Integer id;               // 主键
    private String name;              // 部门名称
    private LocalDateTime createTime; // 创建时间
    private LocalDateTime updateTime; // 修改时间
}
