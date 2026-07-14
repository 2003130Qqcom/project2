package org.example.mapper;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.example.pojo.EmpLog;

/**
 * 员工操作日志 Mapper — 操作 emp_log 表。
 *
 * <p>由 {@code EmpLogServiceImpl} 在新事务中调用。</p>
 */
@Mapper
public interface EmpLogMapper {

    /**
     * 插入操作日志
     *
     * @param empLog 日志实体（operateTime + info）
     */
    @Insert("insert into emp_log (operate_time, info) values (#{operateTime}, #{info})")
    void insert(EmpLog empLog);
}
