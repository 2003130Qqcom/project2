package org.example.exception;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.example.pojo.Result;
import org.example.utils.ExceptionDiagnostics;
import org.example.utils.RequestDiagnostics;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

/**
 * 全局异常处理器。
 *
 * <p>统一拦截 Controller 层抛出的异常，返回 Result 错误响应，
 * 避免异常堆栈直接暴露给前端。</p>
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 请求参数类型不匹配（如 GET /emps?id=abc）
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public Result handleTypeMismatch(MethodArgumentTypeMismatchException e,
                                     HttpServletRequest request) {
        log.warn("请求参数类型不匹配 - request=[{}] 参数={} 期望类型={} 实际值={}",
                RequestDiagnostics.extractSummary(request),
                e.getName(),
                e.getRequiredType() != null ? e.getRequiredType().getSimpleName() : "unknown",
                e.getValue());
        return Result.error("请求参数格式错误：'" + e.getValue() + "' 不是有效的"
                + (e.getRequiredType() != null ? e.getRequiredType().getSimpleName() : "参数"));
    }

    /**
     * HTTP 方法不支持（如 GET 请求了只接受 POST 的接口）
     */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public Result handleMethodNotAllowed(HttpRequestMethodNotSupportedException e,
                                         HttpServletRequest request) {
        log.warn("请求方式不支持 - request=[{}] 方法={} 支持={}",
                RequestDiagnostics.extractSummary(request),
                e.getMethod(),
                e.getSupportedHttpMethods());
        return Result.error("请求方式 " + e.getMethod() + " 不支持，请使用 " + e.getSupportedHttpMethods());
    }

    /**
     * 兜底异常处理（捕获所有未被上述处理器覆盖的异常）
     */
    @ExceptionHandler(Exception.class)
    public Result handleException(Exception e, HttpServletRequest request) {
        log.error("服务器异常 - request=[{}] context=[{}] causeChain=[{}]",
                RequestDiagnostics.extractSummary(request),
                org.example.utils.BaseContext.dump(),
                ExceptionDiagnostics.formatChain(e),
                e);
        return Result.error("服务器发生异常：" + e.getMessage());
    }
}
