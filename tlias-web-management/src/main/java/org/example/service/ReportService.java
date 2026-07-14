package org.example.service;

import org.example.pojo.JobOption;

import java.util.List;
import java.util.Map;

/**
 * 统计报表业务接口。
 */
public interface ReportService {

    /**
     * 员工职位分布统计
     *
     * @return 职位名列表 + 对应人数（用于 ECharts 图表）
     */
    JobOption getEmpJobData();

    /**
     * 员工性别分布统计
     *
     * @return [{name=性别分类, value=人数}, ...]（用于 ECharts 图表）
     */
    List<Map<String, Object>> getEmpGenderData();
}
