package org.example.controller;

import lombok.extern.slf4j.Slf4j;
import org.example.anno.Log;
import org.example.pojo.Result;
import org.example.pojo.Student;
import org.example.pojo.StudentQueryParam;
import org.example.service.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

/**
 * 学员 API 控制器。
 *
 * <p>路径前缀 /students</p>
 */
@Slf4j
@RestController
@RequestMapping("/students")
public class StudentController {

    @Autowired
    private StudentService studentService;

    /**
     * 学员分页查询
     *
     * <p>请求：GET /students?page=1&pageSize=10&name=张&degree=4&clazzId=1</p>
     * <p>响应：{@code Result{data: PageResult{total, rows: [Student...]}}}</p>
     *
     * @param param 分页 + 过滤条件（Query String）
     */
    @GetMapping
    public Result page(StudentQueryParam param) {
        log.info("分页查询学员，参数：{}", param);
        return Result.success(studentService.page(param));
    }

    /**
     * 按 ID 查询学员详情
     *
     * <p>请求：GET /students/1</p>
     *
     * @param id 学员ID（路径参数）
     */
    @GetMapping("/{id}")
    public Result getInfo(@PathVariable Integer id) {
        log.info("查询学员详情，id：{}", id);
        return Result.success(studentService.getById(id));
    }

    /**
     * 新增学员
     *
     * <p>请求：POST /students + JSON Body {@code Student{...}}</p>
     *
     * @param student 学员实体（RequestBody JSON）
     */
    @Log
    @PostMapping
    public Result add(@RequestBody Student student) {
        log.info("添加学员信息：{}", student);
        studentService.add(student);
        return Result.success();
    }

    /**
     * 更新学员
     *
     * <p>请求：PUT /students + JSON Body {@code Student{id, ...}}</p>
     *
     * @param student 学员实体（id 必填）
     */
    @Log
    @PutMapping
    public Result update(@RequestBody Student student) {
        log.info("修改学员信息：{}", student);
        studentService.update(student);
        return Result.success();
    }

    /**
     * 批量删除学员
     *
     * <p>请求：DELETE /students/1,2,3</p>
     *
     * @param ids 逗号分隔的学员ID（路径参数）
     */
    @Log
    @DeleteMapping("/{ids}")
    public Result delete(@PathVariable String ids) {
        log.info("批量删除学员，ids：{}", ids);
        List<Integer> idList = Arrays.stream(ids.split(","))
                .map(Integer::parseInt)
                .toList();
        studentService.deleteByIds(idList);
        return Result.success();
    }

    /**
     * 违纪扣分处理
     *
     * <p>请求：PUT /students/violation/1/5</p>
     * <p>效果：违纪次数+1，违纪分值累积+score</p>
     *
     * @param id    学员ID（路径参数）
     * @param score 本次扣分值（路径参数）
     */
    @Log
    @PutMapping("/violation/{id}/{score}")
    public Result violation(@PathVariable Integer id, @PathVariable Integer score) {
        log.info("违纪处理，id：{}，扣分：{}", id, score);
        studentService.updateViolation(id, score);
        return Result.success();
    }
}
