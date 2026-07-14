package org.example.controller;

import lombok.extern.slf4j.Slf4j;
import org.example.anno.Log;
import org.example.pojo.Emp;
import org.example.pojo.EmpQueryParam;
import org.example.pojo.Result;
import org.example.service.EmpService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 员工 API 控制器。
 *
 * <p>路径前缀 /emps，数据流：</p>
 * <pre>
 * 前端请求(GET/POST JSON) → Controller(参数绑定+校验) → EmpService(事务) → Mapper → DB
 * 前端响应 ← Result{code, msg, data} ← Controller ← EmpService
 * </pre>
 */
@Slf4j
@RestController
@RequestMapping("/emps")
public class EmpController {

    @Autowired
    private EmpService empService;

    /**
     * 班主任下拉选择 — 返回全部员工 {id, name, job}
     *
     * <p>请求：GET /emps/simple</p>
     * <p>响应：{@code Result{data: [{id, name, job}, ...]}}</p>
     */
    @GetMapping("/simple")
    public Result simpleList() {
        log.info("查询员工简要列表（班主任选择）");
        return Result.success(empService.findSimpleList());
    }

    /**
     * 员工分页查询
     *
     * <p>请求：GET /emps?page=1&pageSize=10&name=张&gender=1&begin=2026-01-01&end=2026-06-30</p>
     * <p>响应：{@code Result{data: PageResult{total, rows: [Emp...]}}}</p>
     *
     * @param param 分页 + 过滤条件（Query String 自动绑定）
     */
    @GetMapping
    public Result page(EmpQueryParam param) {
        log.info("分页查询，参数：{}", param);
        return Result.success(empService.page(param));
    }

    /**
     * 查询全部员工（不分页）
     *
     * <p>请求：GET /emps/list</p>
     */
    @GetMapping("/list")
    public Result list() {
        log.info("查询全部员工信息");
        return Result.success(empService.findAll());
    }

    /**
     * 新增员工（含工作经历）
     *
     * <p>请求：POST /emps + JSON Body {@code Emp{..., exprList: [...]}}</p>
     *
     * @param emp 员工完整信息（RequestBody JSON）
     */
    @Log
    @PostMapping
    public Result save(@RequestBody Emp emp) {
        log.info("保存员工，数据：{}", emp);
        empService.save(emp);
        return Result.success();
    }

    /**
     * 批量删除员工（级联删除工作经历）
     *
     * <p>请求：DELETE /emps?ids=1,2,3</p>
     *
     * @param ids 待删除的员工ID列表（Query String 逗号分隔）
     */
    @Log
    @DeleteMapping
    public Result delete(@RequestParam List<Integer> ids) {
        log.info("删除员工，ids：{}", ids);
        empService.delete(ids);
        return Result.success();
    }

    /**
     * 查询员工详情（含工作经历、关联部门名称）
     *
     * <p>请求：GET /emps/1</p>
     * <p>响应：{@code Result{data: Emp{..., exprList: [...], deptName: "..."}}}</p>
     *
     * @param id 员工ID（路径参数）
     */
    @GetMapping("/{id}")
    public Result getInfo(@PathVariable Integer id) {
        log.info("查询员工详情，id：{}", id);
        return Result.success(empService.getInfo(id));
    }

    /**
     * 更新员工信息（先删旧工作经历，再插入新的）
     *
     * <p>请求：PUT /emps + JSON Body {@code Emp{id, ..., exprList: [...]}}</p>
     *
     * @param emp 员工完整信息（RequestBody JSON，id 必填）
     */
    @Log
    @PutMapping
    public Result update(@RequestBody Emp emp) {
        log.info("修改员工信息：{}", emp);
        empService.update(emp);
        return Result.success();
    }
}
