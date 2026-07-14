package org.example.service.impl;

import org.example.mapper.DeptMapper;
import org.example.pojo.Dept;
import org.example.service.DeptService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 部门业务实现 — 直通 Mapper 的简单 CRUD。
 *
 * <p>部门列表使用 Redis 缓存（cacheName = "dept"），
 * 增/删/改操作自动驱逐缓存，保证数据一致。</p>
 */
@Service
public class DeptServiceImpl implements DeptService {

    @Autowired
    private DeptMapper deptMapper;

    @Override
    @Cacheable(value = "dept", key = "'list'")
    public List<Dept> findAll() {
        return deptMapper.findAll();
    }

    @Override
    @CacheEvict(value = "dept", allEntries = true)
    public void deleteById(Integer id) {
        deptMapper.deleteById(id);
    }

    @Override
    @CacheEvict(value = "dept", allEntries = true)
    public void add(Dept dept) {
        dept.setCreateTime(LocalDateTime.now());
        dept.setUpdateTime(LocalDateTime.now());
        deptMapper.insert(dept);
    }

    @Override
    @Cacheable(value = "dept", key = "#id")
    public Dept getById(Integer id) {
        return deptMapper.getById(id);
    }

    @Override
    @CacheEvict(value = "dept", allEntries = true)
    public void update(Dept dept) {
        dept.setUpdateTime(LocalDateTime.now());
        deptMapper.update(dept);
    }
}
