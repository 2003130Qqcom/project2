package org.example.controller;

import lombok.extern.slf4j.Slf4j;
import org.example.anno.Log;
import org.example.pojo.Dept;
import org.example.pojo.Result;
import org.example.service.DeptService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 部门 API 控制器。
 *
 * <p>路径前缀 /depts</p>
 */
@Slf4j
@RestController
public class DeptController {

    @Autowired
    private DeptService deptService;

    /**
     * 查询全部部门
     *
     * <p>请求：GET /depts</p>
     * <p>响应：{@code Result{data: [Dept...]}}</p>
     */
    @GetMapping("/depts")
    public Result list() {
        log.info("查询全部部门信息");
        return Result.success(deptService.findAll());
    }

    /**
     * 删除部门
     *
     * <p>请求：DELETE /depts?id=1</p>
     *
     * @param id 部门ID（Query String）
     */
    @Log
    @DeleteMapping("/depts")
    public Result delete(@RequestParam Integer id) {
        log.info("删除部门信息：{}", id);
        deptService.deleteById(id);
        return Result.success();
    }

    /**
     * 添加部门
     *
     * <p>请求：POST /depts + JSON Body {@code {"name":"..."}}</p>
     *
     * @param dept 部门实体（name 必填）
     */
    @Log
    @PostMapping("/depts")
    public Result add(@RequestBody Dept dept) {
        log.info("添加部门信息：{}", dept);
        deptService.add(dept);
        return Result.success();
    }

    /**
     * 按 ID 查询部门
     *
     * <p>请求：GET /depts/1</p>
     *
     * @param id 部门ID（路径参数）
     */
    @GetMapping("/depts/{id}")
    public Result getInfo(@PathVariable Integer id) {
        log.info("查询部门信息：{}", id);
        return Result.success(deptService.getById(id));
    }

    /**
     * 修改部门
     *
     * <p>请求：PUT /depts + JSON Body {@code {"id":1,"name":"..."}}</p>
     *
     * @param dept 部门实体（id + name 必填）
     */
    @Log
    @PutMapping("/depts")
    public Result update(@RequestBody Dept dept) {
        log.info("修改部门信息：{}", dept);
        deptService.update(dept);
        return Result.success();
    }
}
