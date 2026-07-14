package org.example.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.example.pojo.EmpExpr;

import java.util.List;

/**
 * 员工工作经历 Mapper — 操作 emp_expr 表。
 *
 * <p>SQL 定义在 EmpexprMapper.xml 中。</p>
 */
@Mapper
public interface EmpexprMapper {

    /**
     * 批量插入工作经历
     *
     * @param empExprList 工作经历列表
     */
    void insertBatch(@Param("empExprList") List<EmpExpr> empExprList);

    /**
     * 按员工ID批量删除工作经历
     *
     * @param empIds 员工ID列表
     */
    void deleteByEmpId(@Param("empIds") List<Integer> empIds);
}
