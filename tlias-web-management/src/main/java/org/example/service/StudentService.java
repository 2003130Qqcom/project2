package org.example.service;

import org.example.pojo.PageResult;
import org.example.pojo.Student;
import org.example.pojo.StudentQueryParam;

import java.util.List;

/**
 * 学员业务接口。
 */
public interface StudentService {

    /**
     * 条件分页查学员
     *
     * @param param 过滤条件 + 分页参数
     * @return 分页结果
     */
    PageResult<Student> page(StudentQueryParam param);

    /**
     * @param id 学员ID
     * @return 学员实体
     */
    Student getById(Integer id);

    /**
     * 新增学员
     *
     * @param student 学员实体
     */
    void add(Student student);

    /**
     * 全量更新学员
     *
     * @param student 学员实体（仅 id 不可变）
     */
    void update(Student student);

    /**
     * 批量删除学员
     *
     * @param ids 学员ID列表
     */
    void deleteByIds(List<Integer> ids);

    /**
     * 违纪扣分处理
     *
     * @param id    学员ID
     * @param score 本次扣分值
     */
    void updateViolation(Integer id, Integer score);
}
