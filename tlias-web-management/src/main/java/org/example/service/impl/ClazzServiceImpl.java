package org.example.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.example.mapper.ClazzMapper;
import org.example.mapper.EmpMapper;
import org.example.pojo.Clazz;
import org.example.pojo.ClazzQueryParam;
import org.example.pojo.PageResult;
import org.example.service.ClazzService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 班级业务实现。
 *
 * <p>核心规则：指定班主任时自动将对应员工的职位升级为班主任(job=1)。</p>
 */
@Service
public class ClazzServiceImpl implements ClazzService {

    @Autowired
    private ClazzMapper clazzMapper;
    @Autowired
    private EmpMapper empMapper;

    @Override
    public PageResult<Clazz> list(ClazzQueryParam param) {
        PageHelper.startPage(
                param.getPage() != null ? param.getPage() : 1,
                param.getPageSize() != null ? param.getPageSize() : 10);
        List<Clazz> clazzList = clazzMapper.list(param);
        Page<Clazz> p = (Page<Clazz>) clazzList;
        return new PageResult<>(p.getTotal(), p.getResult());
    }

    @Override
    @Cacheable(value = "clazz", key = "'listAll'")
    public List<Clazz> listAll() {
        return clazzMapper.listAll();
    }

    @Override
    @Cacheable(value = "clazz", key = "#id")
    public Clazz getById(Integer id) {
        return clazzMapper.getById(id);
    }

    @Override
    @CacheEvict(value = "clazz", allEntries = true)
    public void deleteById(Integer id) {
        clazzMapper.deleteById(id);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    @CacheEvict(value = "clazz", allEntries = true)
    public void add(Clazz clazz) {
        clazz.setCreateTime(LocalDateTime.now());
        clazz.setUpdateTime(LocalDateTime.now());
        clazzMapper.insert(clazz);
        promoteToHeadTeacher(clazz.getMasterId());
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    @CacheEvict(value = "clazz", allEntries = true)
    public void update(Clazz clazz) {
        clazz.setUpdateTime(LocalDateTime.now());
        clazzMapper.update(clazz);
        promoteToHeadTeacher(clazz.getMasterId());
    }

    /** 指定班主任时，将对应员工职位升级为班主任(job=1) */
    private void promoteToHeadTeacher(Integer masterId) {
        if (masterId != null) {
            empMapper.updateJob(masterId, 1);
        }
    }
}
