package org.example.service;

import org.example.pojo.Clazz;
import org.example.pojo.ClazzQueryParam;
import org.example.pojo.PageResult;

import java.util.List;

/**
 * 班级业务接口。
 */
public interface ClazzService {

    /**
     * 条件分页查班级（含班主任姓名 + 班级状态）
     *
     * @param param 过滤条件 + 分页参数
     * @return 分页结果
     */
    PageResult<Clazz> list(ClazzQueryParam param);

    /** @return 全部班级简要列表（仅数据库字段） */
    List<Clazz> listAll();

    /** @param id 班级ID */
    Clazz getById(Integer id);

    /** @param id 班级ID */
    void deleteById(Integer id);

    /**
     * 新增班级（指定班主任时自动升级职位为班主任），事务保证原子性
     *
     * @param clazz 班级实体
     */
    void add(Clazz clazz);

    /**
     * 更新班级（指定班主任时自动升级职位为班主任），事务保证原子性
     *
     * @param clazz 班级实体
     */
    void update(Clazz clazz);
}
