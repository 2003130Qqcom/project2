package org.example.controller;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import lombok.extern.slf4j.Slf4j;
import org.example.mapper.OperateLogMapper;
import org.example.pojo.OperateLog;
import org.example.pojo.PageResult;
import org.example.pojo.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 操作日志查询控制器。
 *
 * <p>日志由 LogAspect 自动写入 operate_log 表，此处仅提供分页查询。</p>
 */
@Slf4j
@RestController
@RequestMapping("/log")
public class LogController {

    @Autowired
    private OperateLogMapper operateLogMapper;

    /**
     * 操作日志分页查询（绕过 Service 层，直接调 Mapper）
     *
     * <p>请求：GET /log/page?page=1&pageSize=10</p>
     * <p>响应：{@code Result{data: PageResult{total, rows: [OperateLog{..., operateEmpName}]}}}</p>
     *
     * @param page     页码（默认1）
     * @param pageSize 每页条数（默认10）
     */
    
    @GetMapping("/page")
    public Result page(@RequestParam(defaultValue = "1") Integer page,
                       @RequestParam(defaultValue = "10") Integer pageSize) {
        log.info("分页查询操作日志，page={}, pageSize={}", page, pageSize);
        PageHelper.startPage(page, pageSize);

        Page<OperateLog> p = (Page<OperateLog>) operateLogMapper.list();
        return Result.success(new PageResult<>(p.getTotal(), p.getResult()));
    }
}
