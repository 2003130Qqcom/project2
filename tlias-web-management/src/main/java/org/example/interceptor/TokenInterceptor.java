package org.example.interceptor;

import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.example.service.TokenService;
import org.example.utils.BaseContext;
import org.example.utils.JwtParseResult;
import org.example.utils.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * Token 拦截器
 * 在请求进入 Controller 之前校验 JWT Token 的合法性，
 * 防止未认证的请求访问受保护的接口。
 *
 */
@Component
@Slf4j
public class TokenInterceptor  implements HandlerInterceptor {

    @Autowired
    private TokenService tokenService;
    /**
     * 请求预处理：校验 Token
     *
     * @param request  当前 HTTP 请求
     * @param response 当前 HTTP 响应
     * @param handler  当前请求的目标处理器
     * @return true 继续执行后续流程；false 中断请求
     * @throws Exception 处理过程中可能抛出的异常
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        log.info("执行拦截器");
        String uri = request.getRequestURI();

        // CORS 预检请求直接放行
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }

        // 登录接口和静态资源放行
        if (uri.contains("/login") || uri.endsWith(".html") || uri.endsWith(".css")
                || uri.endsWith(".js") || uri.endsWith(".png") || uri.endsWith(".jpg")
                || uri.endsWith(".ico") || uri.endsWith(".svg") || uri.equals("/")) {
            return true;
        }

        // API 请求需要校验 Token（支持 Header / Query 参数 / Authorization）
        String token = request.getHeader("token");
        if (token == null || token.isEmpty()) {
            token = request.getParameter("token");
        }
        if (token == null || token.isEmpty()) {
            String auth = request.getHeader("Authorization");
            if (auth != null && auth.startsWith("Bearer ")) {
                token = auth.substring(7);
            }
        }
        if (token == null || token.isEmpty()) {
            log.warn("请求 {} 缺少 Token，已拦截", uri);
            response.setContentType("application/json;charset=UTF-8");
            response.setStatus(401);
            response.getWriter().write("{\"code\":0,\"msg\":\"未登录\"}");
            return false;
        }

        // 检查 Token 是否已被登出拉黑
        if (tokenService.isBlacklisted(token)) {
            log.warn("请求 {} Token 已被拉黑（已登出）", uri);
            response.setContentType("application/json;charset=UTF-8");
            response.setStatus(401);
            response.getWriter().write("{\"code\":0,\"msg\":\"Token已失效，请重新登录\"}");
            return false;
        }

        JwtParseResult parseResult = JwtUtils.parseTokenVerbose(token);
        if (!parseResult.isSuccess()) {
            log.warn("请求 {} Token解析失败 - type={}, reason={}",
                    uri, parseResult.getFailureType(), parseResult.getFailureReason());
            response.setContentType("application/json;charset=UTF-8");
            response.setStatus(401);
            response.getWriter().write("{\"code\":0,\"msg\":\""
                    + getUserMessage(parseResult.getFailureType()) + "\"}");
            return false;
        }

        // 将当前用户信息存入 ThreadLocal，供后续流程使用
        Claims claims = parseResult.getClaims();
        Object idObj = claims.get("id");
        if (idObj instanceof Integer) {
            BaseContext.setCurrentId((Integer) idObj);
        } else if (idObj instanceof Number) {
            BaseContext.setCurrentId(((Number) idObj).intValue());
        }
        Object usernameObj = claims.get("username");
        if (usernameObj instanceof String) {
            BaseContext.setCurrentUsername((String) usernameObj);
        }

        return true;
    }

    /**
     * 请求结束后清理 ThreadLocal，防止内存泄漏
     */
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        BaseContext.clear();
    }

    /**
     * 根据 Token 解析失败类型返回用户友好的错误提示
     */
    private String getUserMessage(String failureType) {
        if (failureType == null) {
            return "Token无效，请重新登录";
        }
        switch (failureType) {
            case "EXPIRED":
                return "登录已过期，请重新登录";
            case "BAD_SIGNATURE":
                return "Token签名无效，请重新登录";
            case "MALFORMED":
                return "Token格式错误，请重新登录";
            default:
                return "Token无效，请重新登录";
        }
    }

}
