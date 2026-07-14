package org.example.service;

import org.example.pojo.EmpLog;

/**
 * 员工操作日志业务接口。
 *
 * <p>插入操作在独立事务（REQUIRES_NEW）中执行，避免被外层事务回滚影响日志记录。</p>
 */
public interface EmpLogService {

    /**
     * 插入员工操作日志
     *
     * @param empLog 日志实体（operateTime + info）
     */
    void insertLog(EmpLog empLog);
}
