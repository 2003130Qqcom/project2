package org.example.service.impl;

import org.example.mapper.EmpMapper;
import org.example.pojo.JobOption;
import org.example.service.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * 统计报表业务实现 — 聚合 emp 表数据供 ECharts 图表消费。
 *
 * <p>报表数据缓存 30 分钟（由 RedisConfig 默认 TTL 控制）。</p>
 */
@Service
public class ReportServiceImpl implements ReportService {

    @Autowired
    private EmpMapper empMapper;

    /**
     * 从 MyBatis 返回的 [{pos, num}] 拆分为两个平行列表：
     * jobList → X轴标签，dataList → Y轴数值
     */
    @Override
    @Cacheable(value = "report", key = "'empJobData'")
    public JobOption getEmpJobData() {
        List<Map<String, Object>> list = empMapper.countEmpJobData();
        List<String> jobList = list.stream()
                .map(m -> String.valueOf(m.get("pos")))
                .toList();
        List<Object> dataList = list.stream()
                .map(m -> m.get("num"))
                .toList();
        return new JobOption(jobList, dataList);
    }

    /**
     * 直接透传 MyBatis 返回的 [{name, value}] 给前端
     */
    @Override
    @Cacheable(value = "report", key = "'empGenderData'")
    public List<Map<String, Object>> getEmpGenderData() {
        return empMapper.countEmpGenderData();
    }
}
