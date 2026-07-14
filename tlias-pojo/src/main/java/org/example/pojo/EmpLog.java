package org.example.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 员工操作日志实体，映射 emp_log 表。
 *
 * <p>在 EmpServiceImpl 中手动插入，与 operate_log（AOP 自动记录）为互补的两套日志。</p>
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmpLog {
    private Integer id;               // 主键
    private LocalDateTime operateTime; // 操作时间
    private String info;              // 操作描述
}
