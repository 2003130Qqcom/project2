package org.example.controller;

import lombok.extern.slf4j.Slf4j;
import org.example.pojo.Result;
import org.example.service.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 统计报表 API 控制器。
 *
 * <p>路径前缀 /report，数据供前端 ECharts 图表消费。</p>
 */
@Slf4j
@RestController
@RequestMapping("/report")
public class ReportController {

    @Autowired
    private ReportService reportService;

    /**
     * 员工职位分布统计
     *
     * <p>请求：GET /report/empJobData</p>
     * <p>响应：{@code Result{data: {jobList: ["班主任",...], dataList: [5,...]}}}</p>
     */
    @GetMapping("/empJobData")
    public Result getEmpJobData() {
        log.info("统计员工职位数据");
        return Result.success(reportService.getEmpJobData());
    }

    /**
     * 员工性别分布统计
     *
     * <p>请求：GET /report/empGenderData</p>
     * <p>响应：{@code Result{data: [{name:"男性员工",value:10}, {name:"女性员工",value:8}]}}</p>
     */
    @GetMapping("/empGenderData")
    public Result getEmpGenderData() {
        log.info("统计员工性别数据");
        return Result.success(reportService.getEmpGenderData());
    }
}
