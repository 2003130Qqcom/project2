package org.example.service;

import org.example.pojo.Emp;
import org.example.pojo.EmpQueryParam;
import org.example.pojo.LoginInfo;
import org.example.pojo.PageResult;

import java.util.List;
import java.util.Map;

/**
 * 员工业务接口。
 *
 * <p>数据流：Controller(接收HTTP参数) → Service(业务处理/事务) → Mapper(SQL执行)</p>
 */
public interface EmpService {

    /**
     * 条件分页查询员工列表
     *
     * @param param 分页 + 过滤条件（name/gender/日期范围）
     * @return 分页结果（total + rows）
     */
    PageResult<Emp> page(EmpQueryParam param);

    /**
     * 新增员工（含工作经历），事务保证原子性
     *
     * @param emp 员工实体（含 empExprList 工作经历列表）
     */
    void save(Emp emp);

    /**
     * 批量删除员工（级联删除工作经历），事务保证原子性
     *
     * @param ids 待删除的员工ID列表
     */
    void delete(List<Integer> ids);

    /**
     * 查询员工详情（含工作经历）
     *
     * @param id 员工ID
     * @return 员工实体（含 empExprList）
     */
    Emp getInfo(Integer id);

    /**
     * 更新员工信息（先删旧工作经历再插入新的），事务保证原子性
     *
     * @param emp 员工实体（含更新后的 empExprList）
     */
    void update(Emp emp);

    /**
     * 员工简要列表（班主任下拉选择用）
     *
     * @return [{id, name, job}, ...]
     */
    List<Map<String, Object>> findSimpleList();

    /**
     * 更新员工职位
     *
     * @param id  员工ID
     * @param job 新职位: 1=班主任, 2=讲师, 3=学工主管, 4=教研主管, 5=咨询师
     */
    void updateJob(Integer id, Integer job);

    /**
     * 查询全部员工
     *
     * @return 全部员工列表（按更新时间倒序）
     */
    List<Emp> findAll();

    /**
     * 登录认证：验证用户名密码，通过则生成 JWT Token
     *
     * @param emp 含 username + password
     * @return 登录成功返回 LoginInfo（含 token），失败返回 null
     */
    LoginInfo login(Emp emp);
}
