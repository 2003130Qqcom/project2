package org.example.controller;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.example.pojo.Result;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * HttpSession / Cookie 演示控制器。
 *
 * <p>仅用于教学演示，项目实际使用 JWT Token 做无状态认证。</p>
 */
@Slf4j
@RestController
public class SessionController {

    /** Cookie 写入演示：GET /c1 */
    @GetMapping("/c1")
    public Result cookie1(HttpServletResponse response) {
        response.addCookie(new Cookie("login_username", "itheima"));
        return Result.success();
    }

    /** Cookie 读取演示：GET /c2 */
    @GetMapping("/c2")
    public Result cookie2(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("login_username".equals(cookie.getName())) {
                    System.out.println("login_username: " + cookie.getValue());
                }
            }
        }
        return Result.success();
    }

    /** Session 写入演示：GET /s1 */
    @GetMapping("/s1")
    public Result session1(HttpSession session) {
        log.info("HttpSession-s1: {}", session.hashCode());
        session.setAttribute("loginUser", "tom");
        return Result.success();
    }

    /** Session 读取演示：GET /s2 */
    @GetMapping("/s2")
    public Result session2(HttpSession session) {
        log.info("HttpSession-s2: {}", session.hashCode());
        Object loginUser = session.getAttribute("loginUser");
        log.info("loginUser: {}", loginUser);
        return Result.success(loginUser);
    }
}
