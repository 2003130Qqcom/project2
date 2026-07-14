package org.example.mapper;

import org.apache.ibatis.annotations.*;
import org.example.pojo.Student;
import org.example.pojo.StudentQueryParam;

import java.util.List;

/**
 * 学员 Mapper — 操作 student 表。
 *
 * <p>分页查询 {@link #list} 对应 StudentMapper.xml，其余为注解 SQL。</p>
 */
@Mapper
public interface StudentMapper {

    /**
     * 条件分页查询（配合 PageHelper）
     *
     * @param param 查询条件（name/degree/clazzId + page/pageSize）
     * @return 当前页学员列表
     */
    List<Student> list(StudentQueryParam param);

    /**
     * 按 ID 查学员
     *
     * @param id 学员ID
     * @return 学员实体，不存在时返回 null
     */
    @Select("select * from student where id = #{id}")
    Student getById(Integer id);

    /**
     * 新增学员
     *
     * @param student 学员实体
     */
    @Insert("insert into student(name, no, gender, phone, id_card, is_college, address, degree, graduation_date, clazz_id, violation_count, violation_score, create_time, update_time) " +
            "values(#{name}, #{no}, #{gender}, #{phone}, #{idCard}, #{isCollege}, #{address}, #{degree}, #{graduationDate}, #{clazzId}, #{violationCount}, #{violationScore}, #{createTime}, #{updateTime})")
    void insert(Student student);

    /**
     * 全量更新学员（按 ID 覆盖所有字段）
     *
     * @param student 学员实体（需包含所有需要更新的属性值）
     */
    @Update("update student set name=#{name}, no=#{no}, gender=#{gender}, phone=#{phone}, id_card=#{idCard}, " +
            "is_college=#{isCollege}, address=#{address}, degree=#{degree}, graduation_date=#{graduationDate}, " +
            "clazz_id=#{clazzId}, violation_count=#{violationCount}, violation_score=#{violationScore}, update_time=#{updateTime} " +
            "where id=#{id}")
    void update(Student student);

    /**
     * 批量删除学员
     *
     * @param ids 学员ID列表
     */
    void deleteByIds(@Param("ids") List<Integer> ids);

    /**
     * 违纪扣分：次数+1，分值累加
     *
     * @param id    学员ID
     * @param score 本次扣分值
     */
    @Update("update student set violation_count = violation_count + 1, violation_score = violation_score + #{score}, update_time = now() where id = #{id}")
    void updateViolation(@Param("id") Integer id, @Param("score") Integer score);
}
