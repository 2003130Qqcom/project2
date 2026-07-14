package org.example.mapper;

import org.apache.ibatis.annotations.*;
import org.example.pojo.Emp;
import org.example.pojo.EmpQueryParam;

import java.util.List;
import java.util.Map;

/**
 * 员工 Mapper — 操作 emp 表。
 *
 * <p>分页查询 {@link #list} 配合 PageHelper 使用，
 * 复杂 ResultMap 定义在 EmpMapper.xml 中。</p>
 */
@Mapper
public interface EmpMapper {

    /**
     * 条件分页查询（配合 PageHelper）
     *
     * @param param 查询条件（name/gender/日期范围）
     * @return 当前页员工列表（含 deptName）
     */
    List<Emp> list(EmpQueryParam param);

    /**
     * 新增员工（自动回填主键到 emp.id）
     *
     * @param emp 员工实体
     */
    @Options(useGeneratedKeys = true, keyProperty = "id")
    @Insert("insert into emp (username, name, gender, phone, job, salary, image, entry_date, dept_id, create_time, update_time) " +
            "values (#{username}, #{name}, #{gender}, #{phone}, #{job}, #{salary}, #{image}, #{entryDate}, #{deptId}, #{createTime}, #{updateTime})")
    void insert(Emp emp);

    /**
     * 批量删除员工
     *
     * @param ids 待删除的员工ID列表
     */
    void deleteById(@Param("ids") List<Integer> ids);

    /**
     * 查询员工详情（含工作经历，一对多）
     *
     * @param id 员工ID
     * @return Emp 含 empExprList 工作经历
     */
    Emp getInfo(Integer id);

    /**
     * 动态更新员工信息
     *
     * @param emp 只更新非 null 字段
     */
    void updateById(Emp emp);

    /** @return [{pos=职位名, num=人数}, ...] */
    List<Map<String, Object>> countEmpJobData();

    /** @return [{name=分类, value=人数}, ...] */
    List<Map<String, Object>> countEmpGenderData();

    /**
     * 全部员工简要信息（班主任下拉选择用）
     *
     * @return [{id, name, job}, ...] 包含所有员工
     */
    @Select("select id, name, job from emp order by name")
    List<Map<String, Object>> findSimpleList();

    /**
     * 更新员工职位
     *
     * @param id  员工ID
     * @param job 新职位: 1=班主任, 2=讲师, 3=学工主管, 4=教研主管, 5=咨询师
     */
    @Update("update emp set job = #{job}, update_time = now() where id = #{id}")
    void updateJob(@Param("id") Integer id, @Param("job") Integer job);

    /** @return 全部员工列表（按更新时间倒序） */
    @Select("select * from emp order by update_time desc")
    List<Emp> findAll();

    /**
     * 登录验证：根据用户名+密码查员工
     *
     * @param emp 含 username 和 password
     * @return 匹配的员工，无匹配返回 null
     */
    @Select("select * from emp where username = #{username} and password = #{password}")
    Emp selectByUserAndPassword(Emp emp);
}
