package org.example.utils;

/**
 * 异常诊断工具类
 * <p>
 * 提供异常链分析能力，帮助快速定位根因。
 * 适用于全局异常处理器或问题排查场景。
 * </p>
 *
 * <pre>
 * // 获取根因
 * Throwable root = ExceptionDiagnostics.getRootCause(e);
 * log.error("根因: {}", root.getMessage());
 *
 * // 格式化整个异常链
 * log.error("异常链: {}", ExceptionDiagnostics.formatChain(e));
 * </pre>
 */
public class ExceptionDiagnostics {

    private ExceptionDiagnostics() {
        // 工具类禁止实例化
    }

    /**
     * 遍历异常链，获取最底层的根因
     *
     * @param t 异常对象
     * @return 根因（如果没有更深层的 cause 则返回自身）
     */
    public static Throwable getRootCause(Throwable t) {
        if (t == null) {
            return null;
        }
        Throwable root = t;
        while (root.getCause() != null && root.getCause() != root) {
            root = root.getCause();
        }
        return root;
    }

    /**
     * 获取根因的 message
     *
     * @param t 异常对象
     * @return 根因的消息文本，若异常为 null 则返回 "null"
     */
    public static String getRootCauseMessage(Throwable t) {
        Throwable root = getRootCause(t);
        if (root == null) {
            return "null";
        }
        return root.getClass().getSimpleName() + ": " + root.getMessage();
    }

    /**
     * 格式化整个异常因果链，便于日志输出
     * <p>
     * 输出格式：
     * {@code RootCause: SQLException: Connection refused <- DataAccessException: Could not get connection <- ...}
     * </p>
     *
     * @param t 异常对象
     * @return 从根因到顶层异常格式化的字符串，若为 null 返回 "null"
     */
    public static String formatChain(Throwable t) {
        if (t == null) {
            return "null";
        }

        // 收集整条异常链（从外到内）
        Throwable[] chain = new Throwable[32];
        int depth = 0;
        Throwable cursor = t;
        while (cursor != null && depth < chain.length) {
            chain[depth++] = cursor;
            if (cursor.getCause() == cursor) break;
            cursor = cursor.getCause();
        }

        // 从最深（根因）向外拼
        StringBuilder sb = new StringBuilder();
        for (int i = depth - 1; i >= 0; i--) {
            if (i == depth - 1) {
                sb.append("RootCause: ");
            } else {
                sb.append(" <- ");
            }
            sb.append(formatOne(chain[i]));
        }

        return sb.toString();
    }

    private static String formatOne(Throwable t) {
        return t.getClass().getSimpleName() + ": " + t.getMessage();
    }
}
