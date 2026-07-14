package org.example.service;

import org.example.pojo.Dept;

import java.util.List;

/**
 * 部门业务接口。
 */
public interface DeptService {

    /**
     * @return 全部部门列表（按更新时间倒序）
     */
    List<Dept> findAll();

    /**
     * @param id 部门ID
     */
    void deleteById(Integer id);

    /**
     * 新增部门
     *
     * @param dept 部门实体（name 必填，createTime/updateTime 由 Service 设置）
     */
    void add(Dept dept);

    /**
     * 按 ID 查部门
     *
     * @param id 部门ID
     * @return 部门实体
     */
    Dept getById(Integer id);

    /**
     * 更新部门名称
     *
     * @param dept 部门实体（id + name 必填，updateTime 由 Service 设置）
     */
    void update(Dept dept);
}