package org.example.controller;

import lombok.extern.slf4j.Slf4j;
import org.example.pojo.Emp;
import org.example.pojo.LoginInfo;
import org.example.pojo.Result;
import org.example.service.EmpService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 登录认证控制器。
 *
 * <p>支持三种登录方式（由 TokenInterceptor 放行）：</p>
 * <ul>
 *   <li>GET /login?name=songjiang&password=123456（旧版查询参数）</li>
 *   <li>POST /login?username=songjiang&password=123456（查询参数/表单）</li>
 *   <li>POST /login + JSON Body {"username":"songjiang","password":"123456"}</li>
 * </ul>
 *
 * <p>登录成功返回 JWT Token，后续请求需携带在 Header.token 或 Authorization 中。</p>
 */
@Slf4j
@RestController
@RequestMapping("/login")
public class LoginController {

    @Autowired
    private EmpService empService;

    /**
     * 登录（GET/POST 查询参数或表单方式）
     *
     * <p>请求：GET /login?username=songjiang&password=123456</p>
     * <p>响应：{@code Result{data: {id, username, name, token}}}</p>
     *
     * @param name     旧版用户名（name 或 username 至少传一个）
     * @param username 新版用户名
     * @param password 密码（必填）
     */
    @RequestMapping(method = {RequestMethod.GET, RequestMethod.POST})
    public Result login(@RequestParam(required = false) String name,
                        @RequestParam(required = false) String username,
                        @RequestParam String password) {
        String user = (username != null && !username.isEmpty()) ? username : name;
        log.info("登录 user={}", user);
        return doLogin(user, password);
    }

    /**
     * 登录（JSON Body 方式）
     *
     * <p>请求：POST /login + Content-Type: application/json + {"username":"songjiang","password":"123456"}</p>
     *
     * @param emp 含 username + password 的 JSON Body
     */
    @PostMapping(consumes = "application/json")
    public Result loginJson(@RequestBody Emp emp) {
        log.info("登录(JSON) user={}", emp.getUsername());
        return doLogin(emp.getUsername(), emp.getPassword());
    }

    /** 统一登录逻辑：验证用户名密码 → 生成 JWT Token → 返回 LoginInfo */
    private Result doLogin(String username, String password) {
        Emp emp = new Emp();
        emp.setUsername(username);
        emp.setPassword(password);

        LoginInfo loginInfo = empService.login(emp);
        if (loginInfo == null) {
            return Result.error("用户名或密码错误");
        }
        return Result.success(loginInfo);
    }
}
