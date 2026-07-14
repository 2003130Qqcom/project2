package org.example.service.impl;

import org.example.mapper.EmpLogMapper;
import org.example.pojo.EmpLog;
import org.example.service.EmpLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * 员工操作日志业务实现。
 *
 * <p>使用 REQUIRES_NEW 传播级别确保日志写入独立事务：
 * 即使外层业务回滚，操作日志仍然保留，便于审计。</p>
 */
@Service
public class EmpLogServiceImpl implements EmpLogService {

    @Autowired
    private EmpLogMapper empLogMapper;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @Override
    public void insertLog(EmpLog empLog) {
        empLogMapper.insert(empLog);
    }
}
