package org.example.anno;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 操作日志注解。
 *
 * <p>标注在 Controller 的增/删/改方法上，
 * 由 {@code LogAspect} 环绕通知拦截并自动记录到 operate_log 表。</p>
 *
 * <pre>
 * &#64;Log
 * &#64;PostMapping
 * public Result save(@RequestBody Emp emp) { ... }
 * </pre>
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Log {
}