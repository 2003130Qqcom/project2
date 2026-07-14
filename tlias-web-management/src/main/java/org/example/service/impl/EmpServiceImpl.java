package org.example.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.example.mapper.EmpMapper;
import org.example.mapper.EmpexprMapper;
import org.example.pojo.*;
import org.example.service.EmpLogService;
import org.example.service.EmpService;
import org.example.utils.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 员工业务实现。
 *
 * <p>核心数据流：
 * Controller → page/save/update/delete/login → Mapper → DB
 * 写操作使用 @Transactional 保证原子性，
 * 日志记录委托给 EmpLogService（新事务，不受外层回滚影响）。</p>
 */
@Service
public class EmpServiceImpl implements EmpService {

    @Autowired
    private EmpMapper empMapper;
    @Autowired
    private EmpexprMapper empexprMapper;
    @Autowired
    private EmpLogService empLogService;

    // ───────────── 查询 ─────────────

    @Override
    public PageResult<Emp> page(EmpQueryParam param) {
        int page = param.getPage() != null ? param.getPage() : 1;
        int pageSize = param.getPageSize() != null ? param.getPageSize() : 10;
        PageHelper.startPage(page, pageSize);

        List<Emp> empList = empMapper.list(param);
        Page<Emp> p = (Page<Emp>) empList;
        return new PageResult<>(p.getTotal(), p.getResult());
    }

    @Override
    public Emp getInfo(Integer id) {
        return empMapper.getInfo(id);
    }

    @Override
    @Cacheable(value = "emp", key = "'simpleList'")
    public List<Map<String, Object>> findSimpleList() {
        return empMapper.findSimpleList();
    }

    @Override
    @Cacheable(value = "emp", key = "'all'")
    public List<Emp> findAll() {
        return empMapper.findAll();
    }

    // ───────────── 增删改 ─────────────

    @Transactional(rollbackFor = Exception.class)
    @Override
    @CacheEvict(value = "emp", allEntries = true)
    public void save(Emp emp) {
        // ❶ 保存员工主表（自动回填 emp.id）
        emp.setCreateTime(LocalDateTime.now());
        emp.setUpdateTime(LocalDateTime.now());
        empMapper.insert(emp);

        // ❷ 保存工作经历（存在时）
        List<EmpExpr> exprList = emp.getEmpExprList();
        if (!CollectionUtils.isEmpty(exprList)) {
            exprList.forEach(e -> e.setEmpId(emp.getId()));
            empexprMapper.insertBatch(exprList);
        }

        // ❸ 操作日志（新事务，不随外层回滚）
        empLogService.insertLog(new EmpLog(emp.getId(), LocalDateTime.now(), "保存员工信息"));
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    @CacheEvict(value = "emp", allEntries = true)
    public void delete(List<Integer> ids) {
        empexprMapper.deleteByEmpId(ids);    // 先删工作经历（子表）
        empMapper.deleteById(ids);            // 再删员工（主表）
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    @CacheEvict(value = "emp", allEntries = true)
    public void update(Emp emp) {
        // ❶ 更新员工主表
        emp.setUpdateTime(LocalDateTime.now());
        empMapper.updateById(emp);

        // ❷ 重建工作经历：删旧 → 插新
        empexprMapper.deleteByEmpId(List.of(emp.getId()));
        List<EmpExpr> exprList = emp.getEmpExprList();
        if (!CollectionUtils.isEmpty(exprList)) {
            exprList.forEach(e -> e.setEmpId(emp.getId()));
            empexprMapper.insertBatch(exprList);
        }
    }

    @Override
    @CacheEvict(value = "emp", allEntries = true)
    public void updateJob(Integer id, Integer job) {
        empMapper.updateJob(id, job);
    }

    // ───────────── 认证 ─────────────

    /**
     * 登录流程：
     * ❶ 查 emp 表匹配 username + password
     * ❷ 通过则生成 JWT Token（payload: id + username）
     * ❸ 返回 LoginInfo 含 token，前端后续请求携带
     */
    @Override
    public LoginInfo login(Emp emp) {
        Emp e = empMapper.selectByUserAndPassword(emp);
        if (e == null) {
            return null;
        }
        // 数据库已存明文密码（项目早期简化），二次比对
        if (!e.getPassword().equals(emp.getPassword())) {
            return null;
        }

        Map<String, Object> claims = new HashMap<>();
        claims.put("id", e.getId());
        claims.put("username", e.getUsername());
        String token = JwtUtils.generateToken(claims);

        return new LoginInfo(e.getId(), e.getUsername(), e.getName(), token);
    }
}
