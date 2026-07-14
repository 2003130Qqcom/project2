package org.example.controller;

import lombok.extern.slf4j.Slf4j;
import org.example.anno.Log;
import org.example.pojo.Clazz;
import org.example.pojo.ClazzQueryParam;
import org.example.pojo.Result;
import org.example.service.ClazzService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 班级 API 控制器。
 *
 * <p>路径前缀 /clazzs</p>
 */
@Slf4j
@RestController
@RequestMapping("/clazzs")
public class ClazzController {

    @Autowired
    private ClazzService clazzService;

    /**
     * 班级条件分页查询（含班主任姓名 + 班级状态）
     *
     * <p>请求：GET /clazzs?page=1&pageSize=10&name=Java&begin=2026-01-01&end=2026-06-30</p>
     * <p>响应：{@code Result{data: PageResult{total, rows: [Clazz{..., masterName, status}]}}}</p>
     *
     * @param param 分页 + 过滤条件（Query String）
     */
    @GetMapping
    public Result list(ClazzQueryParam param) {
        log.info("条件分页查询班级，参数：{}", param);
        return Result.success(clazzService.list(param));
    }

    /**
     * 全部班级简要列表（仅数据库原始字段）
     *
     * <p>请求：GET /clazzs/list</p>
     */
    @GetMapping("/list")
    public Result listAll() {
        log.info("查询所有班级信息(list)");
        return Result.success(clazzService.listAll());
    }

    /**
     * 按 ID 查询班级详情
     *
     * <p>请求：GET /clazzs/1</p>
     */
    @GetMapping("/{id}")
    public Result getInfo(@PathVariable Integer id) {
        log.info("查询班级详情，id：{}", id);
        return Result.success(clazzService.getById(id));
    }

    /**
     * 删除班级
     *
     * <p>请求：DELETE /clazzs/1</p>
     */
    @Log
    @DeleteMapping("/{id}")
    public Result delete(@PathVariable Integer id) {
        log.info("删除班级信息，id：{}", id);
        clazzService.deleteById(id);
        return Result.success();
    }

    /**
     * 添加班级（指定班主任时自动升级职位）
     *
     * <p>请求：POST /clazzs + JSON Body {@code Clazz{name, masterId, ...}}</p>
     */
    @Log
    @PostMapping
    public Result add(@RequestBody Clazz clazz) {
        log.info("添加班级信息：{}", clazz);
        clazzService.add(clazz);
        return Result.success();
    }

    /**
     * 修改班级（指定班主任时自动升级职位）
     *
     * <p>请求：PUT /clazzs + JSON Body {@code Clazz{id, name, masterId, ...}}</p>
     */
    @Log
    @PutMapping
    public Result update(@RequestBody Clazz clazz) {
        log.info("修改班级信息：{}", clazz);
        clazzService.update(clazz);
        return Result.success();
    }
}
