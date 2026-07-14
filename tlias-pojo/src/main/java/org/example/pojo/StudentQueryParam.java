package org.example.pojo;

import lombok.Data;

/**
 * 学员分页查询参数，由 Controller 从请求 Query String 自动绑定。
 *
 * <pre>
 * GET /students?page=1&pageSize=10&name=张&degree=4&clazzId=1
 * </pre>
 */
@Data
public class StudentQueryParam {
    /** 页码 */
    private Integer page;
    /** 每页条数 */
    private Integer pageSize;
    /** 姓名模糊匹配 */
    private String name;
    /** 最高学历: 1=初中, 2=高中, 3=大专, 4=本科, 5=硕士, 6=博士 */
    private Integer degree;
    /** 班级ID精确匹配 */
    private Integer clazzId;
}
