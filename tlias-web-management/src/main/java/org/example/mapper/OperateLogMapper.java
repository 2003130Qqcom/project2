package org.example.mapper;

import org.example.pojo.OperateLog;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 操作日志 Mapper — 操作 operate_log 表。
 *
 * <p>写入由 LogAspect 自动完成，查询由 LogController 提供分页接口。</p>
 */
@Mapper
public interface OperateLogMapper {

    /**
     * 插入操作日志（由 LogAspect 调用）
     *
     * @param log 操作日志实体
     */
    @Insert("insert into operate_log (operate_emp_id, operate_time, class_name, method_name, method_params, return_value, cost_time) " +
            "values (#{operateEmpId}, #{operateTime}, #{className}, #{methodName}, #{methodParams}, #{returnValue}, #{costTime})")
    void insert(OperateLog log);

    /**
     * 查询全部操作日志（配合 PageHelper 分页），关联 emp 表获取操作人姓名
     *
     * @return 含 operate_emp_name 的日志列表
     */
    @Select("select ol.id, ol.operate_emp_id, ol.operate_time, ol.class_name, ol.method_name, " +
            "ol.method_params, ol.return_value, ol.cost_time, e.name as operate_emp_name " +
            "from operate_log ol left join emp e on ol.operate_emp_id = e.id " +
            "order by ol.operate_time desc")
    List<OperateLog> list();
}
