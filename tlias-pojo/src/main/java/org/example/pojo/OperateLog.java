package org.example.pojo;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 操作日志实体，映射 operate_log 表。
 *
 * <p>由 LogAspect 切面在标注 @Log 的方法执行后自动写入。</p>
 */
@Data
public class OperateLog {
    private Integer id;               // 主键
    private Integer operateEmpId;     // 操作人ID
    private LocalDateTime operateTime;// 操作时间
    private String className;         // 目标类全限定名
    private String methodName;        // 目标方法名
    private String methodParams;      // 方法参数JSON（截断2000字符）
    private String returnValue;       // 返回值JSON（截断2000字符）
    private Long costTime;            // 执行耗时(ms)
    private String operateEmpName;    // 操作人姓名（关联 emp 表查询）
}
