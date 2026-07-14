package org.example.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 员工职位统计图表数据。
 *
 * <p>jobList 为职位名称列表（X轴），dataList 为对应人数（Y轴）。</p>
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class JobOption {
    /** 职位名称列表（如 ["班主任", "讲师", ...]） */
    private List<String> jobList;
    /** 对应人数列表（如 [5, 12, ...]） */
    private List<Object> dataList;
}
