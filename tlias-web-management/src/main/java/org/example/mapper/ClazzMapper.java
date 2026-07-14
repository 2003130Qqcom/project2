package org.example.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.example.pojo.Clazz;
import org.example.pojo.ClazzQueryParam;

import java.util.List;

/**
 * 班级 Mapper — 操作 clazz 表。
 *
 * <p>SQL 定义在 ClazzMapper.xml 中（含班主任/状态关联查询）。</p>
 */
@Mapper
public interface ClazzMapper {

    /**
     * 条件分页查询（含班主任姓名、班级状态），配合 PageHelper
     *
     * @param param 条件（name/日期范围 + page/pageSize）
     * @return 当前页班级列表
     */
    List<Clazz> list(ClazzQueryParam param);

    /** @return 全部班级简要信息（仅数据库字段，不含班主任姓名/状态） */
    List<Clazz> listAll();

    /**
     * 按 ID 查班级详情
     *
     * @param id 班级ID
     * @return 班级实体，不存在时返回 null
     */
    Clazz getById(Integer id);

    /**
     * 新增班级
     *
     * @param clazz 班级实体
     */
    void insert(Clazz clazz);

    /**
     * 更新班级
     *
     * @param clazz 班级实体（只更新非 null 字段）
     */
    void update(Clazz clazz);

    /**
     * 按 ID 删除班级
     *
     * @param id 班级ID
     */
    void deleteById(Integer id);
}
