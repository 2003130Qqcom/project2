package org.example.aop;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.example.mapper.OperateLogMapper;
import org.example.pojo.OperateLog;
import org.example.utils.BaseContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 操作日志切面
 * 拦截 @Log 注解标注的方法，记录操作日志到 operate_log 表。
 *
 * 记录信息：操作人ID、操作时间、目标类全类名、方法名、方法参数、返回值、执行耗时(ms)
 */
@Slf4j
@Aspect
@Component
public class LogAspect {

    /** 数据库字段长度限制 */
    private static final int MAX_PARAMS_LENGTH = 2000;
    private static final int MAX_RETURN_LENGTH = 2000;

    @Autowired
    private OperateLogMapper operateLogMapper;//进行业务操作的时候会同时往数据库的操作记录写入信息所以需要调用mapper层

    /**
     * 使用 Jackson 2.x ObjectMapper（Spring Boot 4.1.0 默认 Jackson 3.x 的 Bean 类型不兼容）
     */
    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * 环绕通知：拦截所有标注 @Log 注解的方法
     */
    @Around("@annotation(org.example.anno.Log)")
    public Object recordLog(ProceedingJoinPoint joinPoint) throws Throwable {
        // 1. 记录开始时间
        long startTime = System.currentTimeMillis();

        // 2. 获取目标类名和方法名
        String className = joinPoint.getTarget().getClass().getName();
        String methodName = joinPoint.getSignature().getName();

        // 3. 序列化方法参数（过滤掉 HttpServletRequest / HttpServletResponse）
        String methodParams = serializeMethodArgs(joinPoint.getArgs());

        // 4. 从 ThreadLocal 中获取当前操作人ID
        Integer operateEmpId = getCurrentEmpId();

        // 5. 执行目标方法
        Object result = null;
        try {
            result = joinPoint.proceed();
            return result;
        } finally {
            // 6. 计算执行耗时
            long costTime = System.currentTimeMillis() - startTime;

            // 7. 构建操作日志实体
            OperateLog operateLog = new OperateLog();
            operateLog.setOperateEmpId(operateEmpId);
            operateLog.setOperateTime(LocalDateTime.now());
            operateLog.setClassName(className);
            operateLog.setMethodName(methodName);
            operateLog.setMethodParams(truncate(methodParams, MAX_PARAMS_LENGTH));
            operateLog.setReturnValue(truncate(serializeResult(result), MAX_RETURN_LENGTH));
            operateLog.setCostTime(costTime);

            // 8. 保存日志（日志保存失败不应影响业务流程）
            try {
                operateLogMapper.insert(operateLog);
                log.debug("操作日志记录成功: class={}, method={}, cost={}ms", className, methodName, costTime);
            } catch (Exception e) {
                log.error("保存操作日志失败: class={}, method={}", className, methodName, e);
            }
        }
    }

    /**
     * 从 ThreadLocal 中获取当前操作人ID
     * <p>
     * 用户ID由 TokenInterceptor 在请求进入时存入 BaseContext，
     * 此处直接获取，无需重复解析 JWT Token。
     * </p>
     *
     * @return 操作人ID，获取失败返回 null
     */
    private Integer getCurrentEmpId() {
        return BaseContext.getCurrentId();
    }

    /**
     * 序列化方法参数为 JSON 字符串
     * 自动过滤掉 HttpServletRequest、HttpServletResponse 等非业务参数
     */
    private String serializeMethodArgs(Object[] args) {
        if (args == null || args.length == 0) {
            return "";
        }

        // 过滤掉 request/response 类型参数
        List<Object> filteredArgs = new ArrayList<>();
        for (Object arg : args) {
            if (arg instanceof HttpServletRequest || arg instanceof HttpServletResponse) {
                continue;
            }
            filteredArgs.add(arg);
        }

        if (filteredArgs.isEmpty()) {
            return "";
        }

        // 单参数直接序列化，多参数用数组包裹
        Object toSerialize = filteredArgs.size() == 1 ? filteredArgs.getFirst() : filteredArgs;
        return toJson(toSerialize);
    }

    /**
     * 序列化方法返回值为 JSON 字符串
     */
    private String serializeResult(Object result) {
        if (result == null) {
            return "";
        }
        return toJson(result);
    }

    /**
     * 将对象转为 JSON 字符串
     */
    private String toJson(Object obj) {
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            log.warn("JSON序列化失败: {}", e.getMessage());
            return obj.toString();
        }
    }

    /**
     * 截断字符串到指定长度，超出部分用 "..." 替代
     */
    private String truncate(String value, int maxLength) {
        if (value == null) {
            return "";
        }
        if (value.length() <= maxLength) {
            return value;
        }
        return value.substring(0, maxLength - 3) + "...";
    }
}
