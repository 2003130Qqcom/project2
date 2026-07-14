package org.example.mapper;

import org.apache.ibatis.annotations.*;
import org.example.pojo.Dept;

import java.util.List;

/**
 * 部门 Mapper — 操作 dept 表，全部为注解式 SQL。
 */
@Mapper
public interface DeptMapper {

    /** @return 全部部门（按更新时间倒序） */
    @Select("select id, name, create_time, update_time from dept order by update_time desc")
    List<Dept> findAll();

    /**
     * 按 ID 删除部门
     *
     * @param id 部门ID
     */
    @Delete("delete from dept where id = #{id}")
    void deleteById(Integer id);

    /**
     * 新增部门
     *
     * @param dept 部门实体（name/createTime/updateTime 必填）
     */
    @Insert("insert into dept(name, create_time, update_time) values(#{name}, #{createTime}, #{updateTime})")
    void insert(Dept dept);

    /**
     * 按 ID 查部门
     *
     * @param id 部门ID
     * @return 部门实体，不存在时返回 null
     */
    @Select("select id, name, create_time, update_time from dept where id = #{id}")
    Dept getById(Integer id);

    /**
     * 更新部门名称
     *
     * @param dept 部门实体（id + name + updateTime）
     */
    @Update("update dept set name = #{name}, update_time = #{updateTime} where id = #{id}")
    void update(Dept dept);
}