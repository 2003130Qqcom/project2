package org.example.utils;

import jakarta.servlet.http.HttpServletRequest;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 请求诊断工具类
 * <p>
 * 从 HttpServletRequest 中提取调试所需的请求信息，
 * 包括客户端IP、请求摘要、请求详情、脱敏请求头等。
 * 适用于全局异常处理器、拦截器日志等场景。
 * </p>
 *
 * <pre>
 * // 快速摘要
 * log.info("请求: {}", RequestDiagnostics.extractSummary(request));
 *
 * // 详细诊断
 * log.info("请求详情: {}", RequestDiagnostics.extractDetail(request));
 * </pre>
 */
public class RequestDiagnostics {

    /** 需要脱敏的请求头名称 */
    private static final String[] SENSITIVE_HEADERS = {
            "Authorization", "Cookie", "Set-Cookie", "token", "X-Token"
    };

    private static final String REDACTED = "***REDACTED***";

    private RequestDiagnostics() {
        // 工具类禁止实例化
    }

    /**
     * 获取客户端真实IP
     * <p>
     * 依次检查 X-Forwarded-For、X-Real-IP、Proxy-Client-IP 等代理头，
     * 若均不存在则返回 {@link HttpServletRequest#getRemoteAddr()}。
     * </p>
     *
     * @param request HTTP 请求
     * @return 客户端IP地址
     */
    public static String getClientIp(HttpServletRequest request) {
        if (request == null) {
            return "unknown";
        }

        String ip = request.getHeader("X-Forwarded-For");
        if (ip != null && !ip.isEmpty() && !"unknown".equalsIgnoreCase(ip)) {
            // X-Forwarded-For 可能包含多个IP，取第一个
            int idx = ip.indexOf(',');
            if (idx > 0) {
                ip = ip.substring(0, idx).trim();
            }
            return ip;
        }

        ip = request.getHeader("X-Real-IP");
        if (ip != null && !ip.isEmpty() && !"unknown".equalsIgnoreCase(ip)) {
            return ip;
        }

        ip = request.getHeader("Proxy-Client-IP");
        if (ip != null && !ip.isEmpty() && !"unknown".equalsIgnoreCase(ip)) {
            return ip;
        }

        return request.getRemoteAddr();
    }

    /**
     * 提取一行请求摘要
     * <p>
     * 格式：{@code POST /api/emps?page=1&size=10}
     * </p>
     *
     * @param request HTTP 请求
     * @return 请求摘要字符串
     */
    public static String extractSummary(HttpServletRequest request) {
        if (request == null) {
            return "null";
        }

        StringBuilder sb = new StringBuilder();
        sb.append(request.getMethod()).append(" ").append(request.getRequestURI());

        String query = request.getQueryString();
        if (query != null && !query.isEmpty()) {
            sb.append("?").append(query);
        }

        return sb.toString();
    }

    /**
     * 提取请求详细诊断信息
     * <p>
     * 包含：请求摘要、客户端IP、User-Agent、请求头（敏感信息脱敏）
     * </p>
     *
     * @param request HTTP 请求
     * @return 多信息请求详情字符串
     */
    public static String extractDetail(HttpServletRequest request) {
        if (request == null) {
            return "null";
        }

        String summary = extractSummary(request);
        String clientIp = getClientIp(request);
        String userAgent = request.getHeader("User-Agent");

        StringBuilder sb = new StringBuilder();
        sb.append(summary);
        sb.append(" | client=").append(clientIp != null ? clientIp : "unknown");
        if (userAgent != null && !userAgent.isEmpty()) {
            sb.append(" | ua=").append(userAgent);
        }
        sb.append(" | headers=").append(sanitizeHeaders(request));

        return sb.toString();
    }

    /**
     * 获取脱敏后的请求头 Map
     * <p>
     * Authorization、Cookie、token 等敏感头的值会被替换为 "***REDACTED***"
     * </p>
     *
     * @param request HTTP 请求
     * @return 脱敏后的请求头 Map
     */
    public static Map<String, String> sanitizeHeaders(HttpServletRequest request) {
        Map<String, String> result = new LinkedHashMap<>();
        if (request == null) {
            return result;
        }

        java.util.Enumeration<String> headerNames = request.getHeaderNames();
        if (headerNames == null) {
            return result;
        }

        while (headerNames.hasMoreElements()) {
            String name = headerNames.nextElement();
            String value = request.getHeader(name);
            if (isSensitive(name)) {
                value = REDACTED;
            }
            result.put(name, value);
        }

        return result;
    }

    /**
     * 判断请求头名称是否为敏感信息
     */
    private static boolean isSensitive(String headerName) {
        if (headerName == null) {
            return false;
        }
        for (String sensitive : SENSITIVE_HEADERS) {
            if (sensitive.equalsIgnoreCase(headerName)) {
                return true;
            }
        }
        return false;
    }
}
