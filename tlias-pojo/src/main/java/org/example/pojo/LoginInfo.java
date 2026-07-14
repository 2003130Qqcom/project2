package org.example.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 登录成功响应体，包含用户基本信息和 JWT Token。
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginInfo {
    /** 用户ID */
    private Integer id;
    /** 用户名 */
    private String username;
    /** 姓名 */
    private String name;
    /** JWT Token，后续请求需携带 */
    private String token;
}
